# 订单管理功能实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现订单管理功能，包括订单主体和订单明细，支持完整的 CRUD 和状态流转。

**Architecture:** 基于现有 3 层架构：REST Controller -> Service -> Mapper。遵循项目现有模式：
- 使用 Lombok（项目现有模式）
- MyBatis-Plus
- 雪花算法生成 ID
- 分页查询

**Tech Stack:** Java 8, Spring Boot 2.7.0, MyBatis-Plus, Lombok, Jackson

---

## Task Breakdown

### Task 1: 创建订单实体 Order.java

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/model/Order.java`

**Step 1: 创建 Order.java**

```java
package cn.shinefan.fooerp.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("customer_id")
    private Long customerId;

    @TableField("customer_name")
    private String customerName;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 2: 创建订单明细实体 OrderItem.java

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/model/OrderItem.java`

**Step 1: 创建 OrderItem.java**

```java
package cn.shinefan.fooerp.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("order_item")
public class OrderItem {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("product_id")
    private Long productId;

    @TableField("product_name")
    private String productName;

    @TableField("price")
    private BigDecimal price;

    @TableField("quantity")
    private Integer quantity;

    @TableField("subtotal")
    private BigDecimal subtotal;
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 3: 创建订单明细 DTO

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/web/dto/OrderItemDto.java`

**Step 1: 创建 OrderItemDto.java**

```java
package cn.shinefan.fooerp.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 4: 创建订单 DTO

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/web/dto/OrderDto.java`

**Step 1: 创建 OrderDto.java**

```java
package cn.shinefan.fooerp.web.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDto> items;
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 5: 创建订单 Mapper

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/mapper/OrderMapper.java`

**Step 1: 创建 OrderMapper.java**

```java
package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 6: 创建订单明细 Mapper

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/mapper/OrderItemMapper.java`

**Step 1: 创建 OrderItemMapper.java**

```java
package cn.shinefan.fooerp.mapper;

import cn.shinefan.fooerp.model.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 7: 创建 OrderConverter

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/mapper/OrderConverter.java`

**Step 1: 创建 OrderConverter.java**

```java
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
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 8: 创建 OrderService 接口

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/service/OrderService.java`

**Step 1: 创建 OrderService.java**

```java
package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.web.dto.OrderDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface OrderService {
    OrderDto create(OrderDto dto);
    OrderDto getById(Long id);
    OrderDto update(Long id, OrderDto dto);
    void delete(Long id);
    IPage<OrderDto> list(int page, int size, String status);
    OrderDto updateStatus(Long id, String status);
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 9: 创建 OrderServiceImpl

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/service/OrderServiceImpl.java`

**Step 1: 创建 OrderServiceImpl.java**

```java
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
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 10: 创建 OrderController

**Files:**
- Create: `src/main/java/cn/shinefan/fooerp/web/OrderController.java`

**Step 1: 创建 OrderController.java**

```java
package cn.shinefan.fooerp.web;

import cn.shinefan.fooerp.service.OrderService;
import cn.shinefan.fooerp.web.dto.OrderDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @GetMapping
    public ResponseEntity<IPage<OrderDto>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.list(page, size, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        OrderDto dto = orderService.getById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @RequestBody OrderDto dto) {
        OrderDto result = orderService.update(id, dto);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long id, @RequestParam String status) {
        OrderDto dto = orderService.updateStatus(id, status);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}
```

**Step 2: 验证编译**

Run: `mvn compile -q`
Expected: SUCCESS

---

### Task 11: 更新数据库 Schema

**Files:**
- Modify: `src/main/resources/schema.sql`

**Step 1: 添加表结构**

```sql
-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `customer_name` VARCHAR(128) COMMENT '客户名称',
    `total_amount` DECIMAL(19,2) DEFAULT 0 COMMENT '订单总金额',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    `remark` TEXT COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `product_name` VARCHAR(128) COMMENT '产品名称',
    `price` DECIMAL(19,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `subtotal` DECIMAL(19,2) NOT NULL COMMENT '小计',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

**Step 2: 验证文件存在**

Run: `ls src/main/resources/schema.sql`
Expected: 文件存在

---

### Task 12: 最终验证

**Step 1: 编译整个项目**

Run: `mvn clean compile -q`
Expected: SUCCESS (无错误)

**Step 2: 运行测试**

Run: `mvn test -q`
Expected: SUCCESS (或已知失败)

---

## Next Actions

**Plan complete and saved to `docs/plans/2026-04-06-order-design.md`.**

Two execution options:

1. **Subagent-Driven (this session)** - I dispatch fresh subagent per task, review between tasks, fast iteration
2. **Parallel Session (separate)** - Open new session with executing-plans, batch execution with checkpoints

Which approach?
