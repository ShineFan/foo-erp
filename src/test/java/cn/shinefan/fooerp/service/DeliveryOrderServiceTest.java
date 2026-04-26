package cn.shinefan.fooerp.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DeliveryOrderServiceTest {

    @Mock
    private DeliveryOrderMapper deliveryOrderMapper;

    @Mock
    private DeliveryOrderItemMapper deliveryOrderItemMapper;

    @Mock
    private SnowflakeIdGenerator idGenerator;

    @InjectMocks
    private DeliveryOrderServiceImpl deliveryOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== create() ====================

    @Test
    void testCreate_Success() {
        when(idGenerator.nextId()).thenReturn(1001L, 2001L);
        when(deliveryOrderMapper.insert(any(DeliveryOrder.class))).thenReturn(1);
        when(deliveryOrderItemMapper.insert(any(DeliveryOrderItem.class))).thenReturn(1);

        DeliveryOrder savedOrder = createDeliveryOrder(1001L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1001L)).thenReturn(savedOrder);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1001L)).thenReturn(Collections.emptyList());

        DeliveryOrderDto dto = new DeliveryOrderDto();
        dto.setOrderId(10L);
        dto.setDeliveryAddress("123 Main St");

        DeliveryOrderItemDto itemDto = new DeliveryOrderItemDto();
        itemDto.setProductId(1L);
        itemDto.setProductName("Widget");
        itemDto.setOrderedQuantity(5);
        dto.setItems(Collections.singletonList(itemDto));

        DeliveryOrderDto result = deliveryOrderService.create(dto);

        assertNotNull(result);
        assertEquals(1001L, result.getId());
        assertEquals(DeliveryStatus.PENDING, result.getStatus());
        verify(deliveryOrderMapper).insert(any(DeliveryOrder.class));
        verify(deliveryOrderItemMapper).insert(any(DeliveryOrderItem.class));
    }

    @Test
    void testCreate_SetsDeliveredQuantityToZero() {
        when(idGenerator.nextId()).thenReturn(1002L, 2002L);
        when(deliveryOrderMapper.insert(any(DeliveryOrder.class))).thenReturn(1);

        DeliveryOrder savedOrder = createDeliveryOrder(1002L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1002L)).thenReturn(savedOrder);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1002L)).thenReturn(Collections.emptyList());

        DeliveryOrderDto dto = new DeliveryOrderDto();
        dto.setOrderId(10L);
        dto.setDeliveryAddress("123 Main St");

        DeliveryOrderItemDto itemDto = new DeliveryOrderItemDto();
        itemDto.setProductId(1L);
        itemDto.setOrderedQuantity(10);
        dto.setItems(Collections.singletonList(itemDto));

        deliveryOrderService.create(dto);

        verify(deliveryOrderItemMapper).insert(argThat(item ->
                item.getDeliveredQuantity() == 0 &&
                item.getRemainingQuantity() == 10
        ));
    }

    // ==================== getById() ====================

    @Test
    void testGetById_Success() {
        DeliveryOrder order = createDeliveryOrder(1L, DeliveryStatus.SHIPPED);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(order);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1L)).thenReturn(Collections.emptyList());

        DeliveryOrderDto result = deliveryOrderService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_NotFound() {
        when(deliveryOrderMapper.selectById(999L)).thenReturn(null);

        DeliveryOrderDto result = deliveryOrderService.getById(999L);

        assertNull(result);
    }

    // ==================== update() ====================

    @Test
    void testUpdate_Success() {
        DeliveryOrder existing = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(existing);
        when(deliveryOrderMapper.updateById(any(DeliveryOrder.class))).thenReturn(1);

        DeliveryOrderItem existingItem = new DeliveryOrderItem();
        existingItem.setId(10L);
        existingItem.setDeliveryOrderId(1L);
        existingItem.setProductId(1L);
        existingItem.setProductName("Widget");
        existingItem.setOrderedQuantity(10);
        existingItem.setDeliveredQuantity(3);
        existingItem.setRemainingQuantity(7);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1L))
                .thenReturn(Collections.singletonList(existingItem));

        DeliveryOrderDto dto = new DeliveryOrderDto();
        dto.setDeliveryAddress("New Address");

        DeliveryOrderItemDto updateItemDto = new DeliveryOrderItemDto();
        updateItemDto.setId(10L);
        updateItemDto.setProductId(1L);
        updateItemDto.setProductName("Widget Updated");
        updateItemDto.setOrderedQuantity(15);
        dto.setItems(Collections.singletonList(updateItemDto));

        DeliveryOrderDto result = deliveryOrderService.update(1L, dto);

        verify(deliveryOrderItemMapper).updateById(argThat(item ->
                item.getProductName().equals("Widget Updated") &&
                item.getDeliveredQuantity() == 3
        ));
    }

    @Test
    void testUpdate_NotFound() {
        when(deliveryOrderMapper.selectById(999L)).thenReturn(null);

        DeliveryOrderDto dto = new DeliveryOrderDto();
        DeliveryOrderDto result = deliveryOrderService.update(999L, dto);

        assertNull(result);
        verify(deliveryOrderMapper, never()).updateById(any());
    }

    @Test
    void update_InsertsNewItem() {
        DeliveryOrder existing = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(existing);
        when(deliveryOrderMapper.updateById(any(DeliveryOrder.class))).thenReturn(1);
        when(idGenerator.nextId()).thenReturn(9999L);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1L)).thenReturn(Collections.emptyList());

        DeliveryOrderDto dto = new DeliveryOrderDto();
        DeliveryOrderItemDto newItem = new DeliveryOrderItemDto();
        newItem.setProductId(2L);
        newItem.setProductName("New Item");
        newItem.setOrderedQuantity(5);
        dto.setItems(Collections.singletonList(newItem));

        deliveryOrderService.update(1L, dto);

        verify(deliveryOrderItemMapper).insert(argThat(item ->
                item.getProductId().equals(2L) && item.getDeliveredQuantity() == 0
        ));
    }

    @Test
    void update_DeletesRemovedItem() {
        DeliveryOrder existing = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(existing);
        when(deliveryOrderMapper.updateById(any(DeliveryOrder.class))).thenReturn(1);

        DeliveryOrderItem existingItem = new DeliveryOrderItem();
        existingItem.setId(10L);
        existingItem.setDeliveryOrderId(1L);
        existingItem.setProductId(1L);
        existingItem.setOrderedQuantity(10);
        existingItem.setDeliveredQuantity(0);
        existingItem.setRemainingQuantity(10);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1L))
                .thenReturn(Collections.singletonList(existingItem));

        DeliveryOrderDto dto = new DeliveryOrderDto();
        dto.setItems(Collections.emptyList());

        deliveryOrderService.update(1L, dto);

        verify(deliveryOrderItemMapper).deleteById((java.io.Serializable) 10L);
    }

    // ==================== delete() ====================

    @Test
    void testDelete_Success() {
        DeliveryOrder existing = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(existing);
        when(deliveryOrderMapper.deleteById(1L)).thenReturn(1);

        boolean result = deliveryOrderService.delete(1L);

        assertTrue(result);
        verify(deliveryOrderItemMapper).delete(any());
        verify(deliveryOrderMapper).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(deliveryOrderMapper.selectById(999L)).thenReturn(null);

        boolean result = deliveryOrderService.delete(999L);

        assertFalse(result);
        verify(deliveryOrderMapper, never()).deleteById(anyLong());
    }

    // ==================== updateDeliveryStatus() ====================

    @Test
    void testUpdateDeliveryStatus_PendingToShipped() {
        DeliveryOrder order = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(order);
        when(deliveryOrderMapper.updateById(any(DeliveryOrder.class))).thenReturn(1);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1L)).thenReturn(Collections.emptyList());

        DeliveryOrderDto result = deliveryOrderService.updateDeliveryStatus(1L, DeliveryStatus.SHIPPED);

        assertNotNull(result);
        verify(deliveryOrderMapper).updateById(argThat(o -> o.getStatus() == DeliveryStatus.SHIPPED));
    }

    @Test
    void testUpdateDeliveryStatus_SetsDeliveryDateWhenDelivered() {
        DeliveryOrder order = createDeliveryOrder(1L, DeliveryStatus.IN_TRANSIT);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(order);
        when(deliveryOrderMapper.updateById(any(DeliveryOrder.class))).thenReturn(1);
        when(deliveryOrderItemMapper.findByDeliveryOrderId(1L)).thenReturn(Collections.emptyList());

        deliveryOrderService.updateDeliveryStatus(1L, DeliveryStatus.DELIVERED);

        verify(deliveryOrderMapper).updateById(argThat(o ->
                o.getStatus() == DeliveryStatus.DELIVERED && o.getDeliveryDate() != null
        ));
    }

    @Test
    void testUpdateDeliveryStatus_InvalidTransition() {
        DeliveryOrder order = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        when(deliveryOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(IllegalArgumentException.class, () ->
                deliveryOrderService.updateDeliveryStatus(1L, DeliveryStatus.DELIVERED)
        );

        verify(deliveryOrderMapper, never()).updateById(any());
    }

    @Test
    void testUpdateDeliveryStatus_NotFound() {
        when(deliveryOrderMapper.selectById(999L)).thenReturn(null);

        DeliveryOrderDto result = deliveryOrderService.updateDeliveryStatus(999L, DeliveryStatus.SHIPPED);

        assertNull(result);
    }

    // ==================== list() ====================

    @Test
    void testList_BatchFetchesItems() {
        Page<DeliveryOrder> page = new Page<>(1, 10);
        DeliveryOrder order1 = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        DeliveryOrder order2 = createDeliveryOrder(2L, DeliveryStatus.SHIPPED);
        page.setRecords(Arrays.asList(order1, order2));
        page.setTotal(2);

        when(deliveryOrderMapper.selectPage(any(Page.class), any())).thenReturn(page);
        when(deliveryOrderItemMapper.findByDeliveryOrderIds(Arrays.asList(1L, 2L)))
                .thenReturn(Collections.emptyList());

        IPage<DeliveryOrderDto> result = deliveryOrderService.list(1, 10, null);

        // Verify batch query, not per-row
        verify(deliveryOrderItemMapper).findByDeliveryOrderIds(Arrays.asList(1L, 2L));
        verify(deliveryOrderItemMapper, never()).findByDeliveryOrderId(any());
        assertEquals(2, result.getRecords().size());
    }

    // ==================== findByOrderId() ====================

    @Test
    void testFindByOrderId_BatchFetchesItems() {
        DeliveryOrder order1 = createDeliveryOrder(1L, DeliveryStatus.PENDING);
        DeliveryOrder order2 = createDeliveryOrder(2L, DeliveryStatus.SHIPPED);

        when(deliveryOrderMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(order1, order2));
        when(deliveryOrderItemMapper.findByDeliveryOrderIds(Arrays.asList(1L, 2L)))
                .thenReturn(Collections.emptyList());

        List<DeliveryOrderDto> result = deliveryOrderService.findByOrderId(10L);

        verify(deliveryOrderItemMapper).findByDeliveryOrderIds(Arrays.asList(1L, 2L));
        verify(deliveryOrderItemMapper, never()).findByDeliveryOrderId(any());
        assertEquals(2, result.size());
    }

    // ==================== status transition rules ====================

    @Test
    void testStatusTransition_PendingCannotGoToDelivered() {
        assertFalse(DeliveryStatus.PENDING.canTransitionTo(DeliveryStatus.DELIVERED));
    }

    @Test
    void testStatusTransition_PendingCannotGoToInTransit() {
        assertFalse(DeliveryStatus.PENDING.canTransitionTo(DeliveryStatus.IN_TRANSIT));
    }

    @Test
    void testStatusTransition_ShippedToInTransit() {
        assertTrue(DeliveryStatus.SHIPPED.canTransitionTo(DeliveryStatus.IN_TRANSIT));
    }

    @Test
    void testStatusTransition_InTransitToDelivered() {
        assertTrue(DeliveryStatus.IN_TRANSIT.canTransitionTo(DeliveryStatus.DELIVERED));
    }

    @Test
    void testStatusTransition_DeliveredToReturned() {
        assertTrue(DeliveryStatus.DELIVERED.canTransitionTo(DeliveryStatus.RETURNED));
    }

    @Test
    void testStatusTransition_DeliveredCannotGoToPending() {
        assertFalse(DeliveryStatus.DELIVERED.canTransitionTo(DeliveryStatus.PENDING));
    }

    // ==================== helper ====================

    private DeliveryOrder createDeliveryOrder(Long id, DeliveryStatus status) {
        DeliveryOrder order = new DeliveryOrder();
        order.setId(id);
        order.setOrderId(10L);
        order.setDeliveryNo("DO123");
        order.setStatus(status);
        order.setDeliveryAddress("123 Main St");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }
}
