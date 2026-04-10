package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.web.dto.StockLogDto;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface StockService {
    /**
     * 增加库存
     */
    void increaseStock(Long productId, Integer quantity, String reason, Long operatorId);

    /**
     * 减少库存
     */
    void decreaseStock(Long productId, Integer quantity, String reason, Long operatorId);

    /**
     * 调整库存（直接设置库存值）
     */
    void adjustStock(Long productId, Integer newStock, String reason, Long operatorId);

    /**
     * 获取产品当前库存
     */
    Integer getCurrentStock(Long productId);

    /**
     * 获取库存变动记录
     */
    IPage<StockLogDto> getStockLogs(Long productId, int page, int size);

    /**
     * 检查库存是否充足
     */
    boolean isStockSufficient(Long productId, Integer quantity);
}
