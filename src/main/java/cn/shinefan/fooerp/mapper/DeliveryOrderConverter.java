package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.DeliveryOrder;
import cn.shinefan.fooerp.model.DeliveryOrderItem;
import cn.shinefan.fooerp.web.dto.DeliveryOrderDto;
import cn.shinefan.fooerp.web.dto.DeliveryOrderItemDto;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryOrderConverter {

    public static DeliveryOrderDto toDto(DeliveryOrder deliveryOrder, List<DeliveryOrderItem> items) {
        if (deliveryOrder == null) {
            return null;
        }
        DeliveryOrderDto dto = new DeliveryOrderDto();
        dto.setId(deliveryOrder.getId());
        dto.setOrderId(deliveryOrder.getOrderId());
        dto.setDeliveryNo(deliveryOrder.getDeliveryNo());
        dto.setStatus(deliveryOrder.getStatus());
        dto.setDeliveryAddress(deliveryOrder.getDeliveryAddress());
        dto.setDeliveryDate(deliveryOrder.getDeliveryDate());
        dto.setTrackingNumber(deliveryOrder.getTrackingNumber());
        dto.setCarrier(deliveryOrder.getCarrier());
        dto.setCarrierContact(deliveryOrder.getCarrierContact());
        dto.setRemark(deliveryOrder.getRemark());
        dto.setCreatedAt(deliveryOrder.getCreatedAt());
        dto.setUpdatedAt(deliveryOrder.getUpdatedAt());

        if (items != null) {
            dto.setItems(items.stream().map(DeliveryOrderConverter::itemToDto).collect(Collectors.toList()));
        }
        return dto;
    }

    public static DeliveryOrderItemDto itemToDto(DeliveryOrderItem item) {
        if (item == null) {
            return null;
        }
        DeliveryOrderItemDto dto = new DeliveryOrderItemDto();
        dto.setId(item.getId());
        dto.setDeliveryOrderId(item.getDeliveryOrderId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setOrderedQuantity(item.getOrderedQuantity());
        dto.setDeliveredQuantity(item.getDeliveredQuantity());
        dto.setRemainingQuantity(item.getRemainingQuantity());
        dto.setRemark(item.getRemark());
        return dto;
    }

    public static DeliveryOrder toEntity(DeliveryOrderDto dto) {
        if (dto == null) {
            return null;
        }
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setId(dto.getId());
        deliveryOrder.setOrderId(dto.getOrderId());
        deliveryOrder.setDeliveryNo(dto.getDeliveryNo());
        deliveryOrder.setStatus(dto.getStatus());
        deliveryOrder.setDeliveryAddress(dto.getDeliveryAddress());
        deliveryOrder.setDeliveryDate(dto.getDeliveryDate());
        deliveryOrder.setTrackingNumber(dto.getTrackingNumber());
        deliveryOrder.setCarrier(dto.getCarrier());
        deliveryOrder.setCarrierContact(dto.getCarrierContact());
        deliveryOrder.setRemark(dto.getRemark());
        return deliveryOrder;
    }
}
