package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.converter.DeliveryOrderConverter;
import cn.shinefan.fooerp.mapper.DeliveryOrderItemMapper;
import cn.shinefan.fooerp.mapper.DeliveryOrderMapper;
import cn.shinefan.fooerp.model.DeliveryOrder;
import cn.shinefan.fooerp.model.DeliveryOrderItem;
import cn.shinefan.fooerp.model.DeliveryStatus;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        deliveryOrder.setStatus(DeliveryStatus.PENDING);
        deliveryOrder.setDeliveryAddress(dto.getDeliveryAddress());
        deliveryOrder.setDeliveryDate(dto.getDeliveryDate());
        deliveryOrder.setTrackingNumber(dto.getTrackingNumber());
        deliveryOrder.setCarrier(dto.getCarrier());
        deliveryOrder.setCarrierContact(dto.getCarrierContact());
        deliveryOrder.setRemark(dto.getRemark());

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
        deliveryOrderMapper.updateById(deliveryOrder);

        if (dto.getItems() != null) {
            updateItems(id, dto.getItems());
        }

        return getById(id);
    }

    private void updateItems(Long deliveryOrderId, List<DeliveryOrderItemDto> itemDtos) {
        List<DeliveryOrderItem> existingItems = deliveryOrderItemMapper.findByDeliveryOrderId(deliveryOrderId);
        Map<Long, DeliveryOrderItem> existingMap = existingItems.stream()
                .collect(Collectors.toMap(DeliveryOrderItem::getId, i -> i));

        List<Long> dtoItemIds = new ArrayList<>();
        for (DeliveryOrderItemDto itemDto : itemDtos) {
            if (itemDto.getId() != null && existingMap.containsKey(itemDto.getId())) {
                // Update existing item
                DeliveryOrderItem existing = existingMap.get(itemDto.getId());
                existing.setProductId(itemDto.getProductId());
                existing.setProductName(itemDto.getProductName());
                existing.setOrderedQuantity(itemDto.getOrderedQuantity());
                existing.setRemark(itemDto.getRemark());
                // Preserve deliveredQuantity and recalculate remaining
                existing.setRemainingQuantity(existing.getOrderedQuantity() - existing.getDeliveredQuantity());
                deliveryOrderItemMapper.updateById(existing);
                dtoItemIds.add(itemDto.getId());
            } else {
                // Insert new item
                DeliveryOrderItem item = new DeliveryOrderItem();
                item.setId(idGenerator.nextId());
                item.setDeliveryOrderId(deliveryOrderId);
                item.setProductId(itemDto.getProductId());
                item.setProductName(itemDto.getProductName());
                item.setOrderedQuantity(itemDto.getOrderedQuantity());
                item.setDeliveredQuantity(0);
                item.setRemainingQuantity(itemDto.getOrderedQuantity());
                item.setRemark(itemDto.getRemark());
                deliveryOrderItemMapper.insert(item);
            }
        }

        // Delete items not in the DTO
        for (DeliveryOrderItem existing : existingItems) {
            if (!dtoItemIds.contains(existing.getId())) {
                deliveryOrderItemMapper.deleteById(existing.getId());
            }
        }
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            return false;
        }
        deliveryOrderItemMapper.delete(new QueryWrapper<DeliveryOrderItem>().eq("delivery_order_id", id));
        deliveryOrderMapper.deleteById(id);
        return true;
    }

    @Override
    public IPage<DeliveryOrderDto> list(int page, int size, DeliveryStatus status) {
        Page<DeliveryOrder> pageParam = new Page<>(page, size);
        QueryWrapper<DeliveryOrder> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status.name());
        }
        wrapper.orderByDesc("created_at");

        IPage<DeliveryOrder> deliveryOrderPage = deliveryOrderMapper.selectPage(pageParam, wrapper);

        List<Long> orderIds = deliveryOrderPage.getRecords().stream()
                .map(DeliveryOrder::getId)
                .collect(Collectors.toList());

        Map<Long, List<DeliveryOrderItem>> itemsMap = batchFetchItems(orderIds);

        return deliveryOrderPage.convert(deliveryOrder -> {
            List<DeliveryOrderItem> items = itemsMap.getOrDefault(deliveryOrder.getId(), Collections.emptyList());
            return DeliveryOrderConverter.toDto(deliveryOrder, items);
        });
    }

    @Override
    public List<DeliveryOrderDto> findByOrderId(Long orderId) {
        QueryWrapper<DeliveryOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.orderByDesc("created_at");

        List<DeliveryOrder> deliveryOrders = deliveryOrderMapper.selectList(wrapper);

        List<Long> orderIds = deliveryOrders.stream()
                .map(DeliveryOrder::getId)
                .collect(Collectors.toList());

        Map<Long, List<DeliveryOrderItem>> itemsMap = batchFetchItems(orderIds);

        return deliveryOrders.stream()
                .map(deliveryOrder -> {
                    List<DeliveryOrderItem> items = itemsMap.getOrDefault(deliveryOrder.getId(), Collections.emptyList());
                    return DeliveryOrderConverter.toDto(deliveryOrder, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryOrderDto updateDeliveryStatus(Long id, DeliveryStatus newStatus) {
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(id);
        if (deliveryOrder == null) {
            return null;
        }

        DeliveryStatus currentStatus = deliveryOrder.getStatus();
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        deliveryOrder.setStatus(newStatus);

        if (DeliveryStatus.DELIVERED == newStatus) {
            deliveryOrder.setDeliveryDate(LocalDateTime.now());
        }

        deliveryOrderMapper.updateById(deliveryOrder);
        return getById(id);
    }

    private Map<Long, List<DeliveryOrderItem>> batchFetchItems(List<Long> deliveryOrderIds) {
        if (deliveryOrderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<DeliveryOrderItem> allItems = deliveryOrderItemMapper.findByDeliveryOrderIds(deliveryOrderIds);
        return allItems.stream().collect(Collectors.groupingBy(DeliveryOrderItem::getDeliveryOrderId));
    }

    private String generateDeliveryNo() {
        return "DO" + idGenerator.nextId();
    }
}
