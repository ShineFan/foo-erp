package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.ProductMapper;
import cn.shinefan.fooerp.mapper.StockLogMapper;
import cn.shinefan.fooerp.model.Product;
import cn.shinefan.fooerp.util.SnowflakeIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private StockLogMapper stockLogMapper;

    @Mock
    private SnowflakeIdGenerator idGenerator;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void testIncreaseStock_Success() {
        // 准备测试数据
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(100);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        when(productMapper.selectById(1L)).thenReturn(product);
        when(idGenerator.nextId()).thenReturn(1001L);

        // 执行测试
        stockService.increaseStock(1L, 50, "采购入库", 1L);

        // 验证
        verify(productMapper, times(1)).updateById(any(Product.class));
        verify(stockLogMapper, times(1)).insert(any());
        assertEquals(150, product.getStock());
    }

    @Test
    void testIncreaseStock_ProductNotFound() {
        when(productMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stockService.increaseStock(1L, 50, "采购入库", 1L);
        });

        assertEquals("Product not found: 1", exception.getMessage());
    }

    @Test
    void testDecreaseStock_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(100);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        when(productMapper.selectById(1L)).thenReturn(product);
        when(idGenerator.nextId()).thenReturn(1001L);

        stockService.decreaseStock(1L, 30, "销售出库", 1L);

        verify(productMapper, times(1)).updateById(any(Product.class));
        verify(stockLogMapper, times(1)).insert(any());
        assertEquals(70, product.getStock());
    }

    @Test
    void testDecreaseStock_InsufficientStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(20);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        when(productMapper.selectById(1L)).thenReturn(product);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stockService.decreaseStock(1L, 30, "销售出库", 1L);
        });

        assertEquals("Insufficient stock for product: 1", exception.getMessage());
    }

    @Test
    void testAdjustStock_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(100);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        when(productMapper.selectById(1L)).thenReturn(product);
        when(idGenerator.nextId()).thenReturn(1001L);

        stockService.adjustStock(1L, 120, "库存调整", 1L);

        verify(productMapper, times(1)).updateById(any(Product.class));
        verify(stockLogMapper, times(1)).insert(any());
        assertEquals(120, product.getStock());
    }

    @Test
    void testGetCurrentStock() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(80);

        when(productMapper.selectById(1L)).thenReturn(product);

        Integer stock = stockService.getCurrentStock(1L);
        assertEquals(80, stock);
    }

    @Test
    void testIsStockSufficient_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(80);

        when(productMapper.selectById(1L)).thenReturn(product);

        assertTrue(stockService.isStockSufficient(1L, 50));
    }

    @Test
    void testIsStockSufficient_Insufficient() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(80);

        when(productMapper.selectById(1L)).thenReturn(product);

        assertFalse(stockService.isStockSufficient(1L, 100));
    }
}
