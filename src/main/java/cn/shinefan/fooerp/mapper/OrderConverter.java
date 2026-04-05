package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.Order;
import cn.shinefan.fooerp.model.OrderItem;
import cn.shinefan.fooerp.web.dto.OrderDto;
import cn.shinefan.fooerp.web.dto.OrderItemDto;
import java.util.List;
import java.util.stream.Collectors;

public class OrderConverter {
    
    public static OrderDto toDto(Order order, List<OrderItem> items) {
        if (order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setCustomerId(order.getCustomerId());
        dto.setCustomerName(order.getCustomerName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        if (items != null) {
            dto.setItems(items.stream().map(OrderConverter::itemToDto).collect(Collectors.toList()));
        }
        return dto;
    }
    
    public static OrderItemDto itemToDto(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
    
    public static Order toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }
        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderNo(dto.getOrderNo());
        order.setCustomerId(dto.getCustomerId());
        order.setCustomerName(dto.getCustomerName());
        order.setTotalAmount(dto.getTotalAmount());
        order.setStatus(dto.getStatus());
        order.setRemark(dto.getRemark());
        return order;
    }
}
