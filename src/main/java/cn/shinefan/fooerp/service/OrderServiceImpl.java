package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.OrderConverter;
import cn.shinefan.fooerp.mapper.OrderItemMapper;
import cn.shinefan.fooerp.mapper.OrderMapper;
import cn.shinefan.fooerp.model.Order;
import cn.shinefan.fooerp.model.OrderItem;
import cn.shinefan.fooerp.util.SnowflakeIdGenerator;
import cn.shinefan.fooerp.web.dto.OrderDto;
import cn.shinefan.fooerp.web.dto.OrderItemDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SnowflakeIdGenerator idGenerator;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, SnowflakeIdGenerator idGenerator) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    @Transactional
    public OrderDto create(OrderDto dto) {
        Order order = new Order();
        order.setId(idGenerator.nextId());
        order.setOrderNo(generateOrderNo());
        order.setCustomerId(dto.getCustomerId());
        order.setCustomerName(dto.getCustomerName());
        order.setStatus("PENDING");
        order.setRemark(dto.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderItemDto itemDto : dto.getItems()) {
                totalAmount = totalAmount.add(itemDto.getSubtotal());
            }
        }
        order.setTotalAmount(totalAmount);
        
        orderMapper.insert(order);
        
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderItemDto itemDto : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setId(idGenerator.nextId());
                item.setOrderId(order.getId());
                item.setProductId(itemDto.getProductId());
                item.setProductName(itemDto.getProductName());
                item.setPrice(itemDto.getPrice());
                item.setQuantity(itemDto.getQuantity());
                item.setSubtotal(itemDto.getSubtotal());
                orderItemMapper.insert(item);
            }
        }
        
        return getById(order.getId());
    }

    @Override
    public OrderDto getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        List<OrderItem> items = orderItemMapper.findByOrderId(id);
        return OrderConverter.toDto(order, items);
    }

    @Override
    @Transactional
    public OrderDto update(Long id, OrderDto dto) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        
        order.setCustomerId(dto.getCustomerId());
        order.setCustomerName(dto.getCustomerName());
        order.setRemark(dto.getRemark());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 删除旧明细，插入新明细
        orderItemMapper.delete(new QueryWrapper<OrderItem>().eq("order_id", id));
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderItemDto itemDto : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setId(idGenerator.nextId());
                item.setOrderId(id);
                item.setProductId(itemDto.getProductId());
                item.setProductName(itemDto.getProductName());
                item.setPrice(itemDto.getPrice());
                item.setQuantity(itemDto.getQuantity());
                item.setSubtotal(itemDto.getSubtotal());
                orderItemMapper.insert(item);
                totalAmount = totalAmount.add(itemDto.getSubtotal());
            }
        }
        order.setTotalAmount(totalAmount);
        orderMapper.updateById(order);
        
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        orderItemMapper.delete(new QueryWrapper<OrderItem>().eq("order_id", id));
        orderMapper.deleteById(id);
    }

    @Override
    public IPage<OrderDto> list(int page, int size, String status) {
        Page<Order> pageParam = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("created_at");
        
        IPage<Order> orderPage = orderMapper.selectPage(pageParam, wrapper);
        
        return orderPage.convert(order -> {
            List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
            return OrderConverter.toDto(order, items);
        });
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long id, String status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return null;
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return getById(id);
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }
}
