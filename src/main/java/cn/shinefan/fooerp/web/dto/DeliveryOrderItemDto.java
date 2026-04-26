package cn.shinefan.fooerp.web.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class DeliveryOrderItemDto {
    private Long id;
    private Long deliveryOrderId;

    @NotNull(message = "productId is required")
    private Long productId;
    private String productName;

    @NotNull(message = "orderedQuantity is required")
    @Min(value = 1, message = "orderedQuantity must be at least 1")
    private Integer orderedQuantity;
    private Integer deliveredQuantity;
    private Integer remainingQuantity;
    private String remark;

    public DeliveryOrderItemDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(Long deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public Integer getDeliveredQuantity() {
        return deliveredQuantity;
    }

    public void setDeliveredQuantity(Integer deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
