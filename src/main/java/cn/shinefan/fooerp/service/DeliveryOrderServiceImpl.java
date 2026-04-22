package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.DeliveryOrderConverter;
import cn.shinefan.fooerp.mapper.DeliveryOrderItemMapper;
import cn.shinefan.fooerp.mapper.DeliveryOrderMapper;
import cn.shinefan.fooerp.model.DeliveryOrder;
import cn.shinefan.fooerp.model.DeliveryOrderItem;
import cn.shinefan.fooerp.util.SnowflakeIdGenerator;
import cn.shinefan.fooerp.web.dto.DeliveryOrderDto;
import cn.shinefan.fooerp.web.dto.DeliveryOrderItemDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

    private final DeliveryOrderMapper deliveryOrderMapper;
    private final DeliveryOrderItemMapper deliveryOrderItemMapper;
    private final SnowflakeIdGenerator idGenerator;

    @Autowired
    public DeliveryOrderServiceImpl(DeliveryOrderMapper deliveryOrderMapper,
                                     DeliveryOrderItemMapper deliveryOrderItemMapper,
                                     SnowflakeIdGenerator idGenerator) {
        this.deliveryOrderMapper = deliveryOrderMapper;
        this.deliveryOrderItemMapper = deliveryOrderItemMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    @Transactional
    public DeliveryOrderDto create(DeliveryOrderDto dto) {
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setId(idGenerator.nextId());
        deliveryOrder.setOrderId(dto.getOrderId());
        deliveryOrder.setDeliveryNo(generateDeliveryNo());
        deliveryOrder.setStatus("PENDING");
        deliveryOrder.setDeliveryAddress(dto.getDeliveryAddress());
        deliveryOrder.setDeliveryDate(dto.getDeliveryDate());
        deliveryOrder.setTrackingNumber(dto.getTrackingNumber());
        deliveryOrder.setCarrier(dto.getCarrier());
        deliveryOrder.setCarrierContact(dto.getCarrierContact());
        deliveryOrder.setRemark(dto.getRemark());
        deliveryOrder.setCreatedAt(LocalDateTime.now());
        deliveryOrder.setUpdatedAt(LocalDateTime.now());

        deliveryOrderMapper.insert(deliveryOrder);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (DeliveryOrderItemDto itemDto : dto.getItems()) {
                DeliveryOrderItem item = new DeliveryOrderItem();
                item.setId(idGenerator.nextId());
                item.setDeliveryOrderId(deliveryOrder.getId());
                item.setProductId(itemDto.getProductId());
                item.setProductName(itemDto.getProductName());
                item.setOrderedQuantity(itemDto.getOrderedQuantity());
                item.setDeliveredQuantity(0);
                item.setRemainingQuantity(itemDto.getOrderedQuantity());
                item.setRemark(itemDto.getRemark());
                deliveryOrderItemMapper.insert(item);
            }
        }

        return getById(deliveryOrder.getId());
    }

    @Override
    public DeliveryOrderDto getById(Long id) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            return null;
        }
        List<DeliveryOrderItem> items = deliveryOrderItemMapper.findByDeliveryOrderId(id);
        return DeliveryOrderConverter.toDto(deliveryOrder, items);
    }

    @Override
    @Transactional
    public DeliveryOrderDto update(Long id, DeliveryOrderDto dto) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            return null;
        }

        deliveryOrder.setDeliveryAddress(dto.getDeliveryAddress());
        deliveryOrder.setDeliveryDate(dto.getDeliveryDate());
        deliveryOrder.setTrackingNumber(dto.getTrackingNumber());
        deliveryOrder.setCarrier(dto.getCarrier());
        deliveryOrder.setCarrierContact(dto.getCarrierContact());
        deliveryOrder.setRemark(dto.getRemark());
        deliveryOrder.setUpdatedAt(LocalDateTime.now());
        deliveryOrderMapper.updateById(deliveryOrder);

        deliveryOrderItemMapper.delete(new QueryWrapper<DeliveryOrderItem>().eq("delivery_order_id", id));
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (DeliveryOrderItemDto itemDto : dto.getItems()) {
                DeliveryOrderItem item = new DeliveryOrderItem();
                item.setId(idGenerator.nextId());
                item.setDeliveryOrderId(id);
                item.setProductId(itemDto.getProductId());
                item.setProductName(itemDto.getProductName());
                item.setOrderedQuantity(itemDto.getOrderedQuantity());
                item.setDeliveredQuantity(itemDto.getDeliveredQuantity());
                item.setRemainingQuantity(itemDto.getRemainingQuantity());
                item.setRemark(itemDto.getRemark());
                deliveryOrderItemMapper.insert(item);
            }
        }

        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        deliveryOrderItemMapper.delete(new QueryWrapper<DeliveryOrderItem>().eq("delivery_order_id", id));
        deliveryOrderMapper.deleteById(id);
    }

    @Override
    public IPage<DeliveryOrderDto> list(int page, int size, String status) {
        Page<DeliveryOrder> pageParam = new Page<>(page, size);
        QueryWrapper<DeliveryOrder> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("created_at");

        IPage<DeliveryOrder> deliveryOrderPage = deliveryOrderMapper.selectPage(pageParam, wrapper);

        return deliveryOrderPage.convert(deliveryOrder -> {
            List<DeliveryOrderItem> items = deliveryOrderItemMapper.findByDeliveryOrderId(deliveryOrder.getId());
            return DeliveryOrderConverter.toDto(deliveryOrder, items);
        });
    }

    @Override
    public List<DeliveryOrderDto> findByOrderId(Long orderId) {
        QueryWrapper<DeliveryOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.orderByDesc("created_at");

        List<DeliveryOrder> deliveryOrders = deliveryOrderMapper.selectList(wrapper);
        return deliveryOrders.stream()
                .map(deliveryOrder -> {
                    List<DeliveryOrderItem> items = deliveryOrderItemMapper.findByDeliveryOrderId(deliveryOrder.getId());
                    return DeliveryOrderConverter.toDto(deliveryOrder, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryOrderDto updateDeliveryStatus(Long id, String status) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            return null;
        }
        deliveryOrder.setStatus(status);
        deliveryOrder.setUpdatedAt(LocalDateTime.now());

        if ("DELIVERED".equals(status)) {
            deliveryOrder.setDeliveryDate(LocalDateTime.now());
        }

        deliveryOrderMapper.updateById(deliveryOrder);
        return getById(id);
    }

    private String generateDeliveryNo() {
        return "DO" + System.currentTimeMillis();
    }
}
