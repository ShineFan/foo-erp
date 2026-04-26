package cn.shinefan.fooerp.web.dto;

import cn.shinefan.fooerp.model.DeliveryStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class DeliveryOrderDto {
    private Long id;

    @NotNull(message = "orderId is required")
    private Long orderId;
    private String deliveryNo;
    private DeliveryStatus status;

    @NotNull(message = "deliveryAddress is required")
    private String deliveryAddress;
    private LocalDateTime deliveryDate;
    private String trackingNumber;
    private String carrier;
    private String carrierContact;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DeliveryOrderItemDto> items;

    public DeliveryOrderDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCarrierContact() {
        return carrierContact;
    }

    public void setCarrierContact(String carrierContact) {
        this.carrierContact = carrierContact;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DeliveryOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<DeliveryOrderItemDto> items) {
        this.items = items;
    }
}
