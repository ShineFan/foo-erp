package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.StockLogMapper;
import cn.shinefan.fooerp.mapper.ProductMapper;
import cn.shinefan.fooerp.model.Product;
import cn.shinefan.fooerp.model.StockLog;
import cn.shinefan.fooerp.util.SnowflakeIdGenerator;
import cn.shinefan.fooerp.web.dto.StockLogDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StockLogMapper stockLogMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public void increaseStock(Long productId, Integer quantity, String reason, Long operatorId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found: " + productId);
        }

        int beforeStock = product.getStock();
        int afterStock = beforeStock + quantity;
        product.setStock(afterStock);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);

        createStockLog(productId, quantity, beforeStock, afterStock, "INCREASE", reason, operatorId);
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, Integer quantity, String reason, Long operatorId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found: " + productId);
        }

        int beforeStock = product.getStock();
        if (beforeStock < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        int afterStock = beforeStock - quantity;
        product.setStock(afterStock);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);

        createStockLog(productId, -quantity, beforeStock, afterStock, "DECREASE", reason, operatorId);
    }

    @Override
    @Transactional
    public void adjustStock(Long productId, Integer newStock, String reason, Long operatorId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found: " + productId);
        }

        int beforeStock = product.getStock();
        int quantity = newStock - beforeStock;
        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);

        String type = quantity > 0 ? "INCREASE" : quantity < 0 ? "DECREASE" : "ADJUST";
        createStockLog(productId, quantity, beforeStock, newStock, type, reason, operatorId);
    }

    @Override
    public Integer getCurrentStock(Long productId) {
        Product product = productMapper.selectById(productId);
        return product != null ? product.getStock() : null;
    }

    @Override
    public IPage<StockLogDto> getStockLogs(Long productId, int page, int size) {
        Page<StockLog> stockLogPage = new Page<>(page, size);
        QueryWrapper<StockLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        queryWrapper.orderByDesc("created_at");
        IPage<StockLog> result = stockLogMapper.selectPage(stockLogPage, queryWrapper);
        return result.convert(stockLog -> {
            StockLogDto dto = new StockLogDto();
            dto.setId(stockLog.getId());
            dto.setProductId(stockLog.getProductId());
            dto.setQuantity(stockLog.getQuantity());
            dto.setBeforeStock(stockLog.getBeforeStock());
            dto.setAfterStock(stockLog.getAfterStock());
            dto.setType(stockLog.getType());
            dto.setReason(stockLog.getReason());
            dto.setOperatorId(stockLog.getOperatorId());
            dto.setCreatedAt(stockLog.getCreatedAt());
            dto.setUpdatedAt(stockLog.getUpdatedAt());
            return dto;
        });
    }

    @Override
    public boolean isStockSufficient(Long productId, Integer quantity) {
        Integer currentStock = getCurrentStock(productId);
        return currentStock != null && currentStock >= quantity;
    }

    private void createStockLog(Long productId, Integer quantity, Integer beforeStock,
                                Integer afterStock, String type, String reason, Long operatorId) {
        StockLog stockLog = new StockLog();
        stockLog.setId(idGenerator.nextId());
        stockLog.setProductId(productId);
        stockLog.setQuantity(quantity);
        stockLog.setBeforeStock(beforeStock);
        stockLog.setAfterStock(afterStock);
        stockLog.setType(type);
        stockLog.setReason(reason);
        stockLog.setOperatorId(operatorId);
        stockLog.setCreatedAt(LocalDateTime.now());
        stockLog.setUpdatedAt(LocalDateTime.now());
        stockLogMapper.insert(stockLog);
    }
}
