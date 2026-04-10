package cn.shinefan.fooerp.web;

import cn.shinefan.fooerp.service.StockService;
import cn.shinefan.fooerp.web.dto.StockLogDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/increase")
    public ResponseEntity<Map<String, Object>> increaseStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) Long operatorId) {
        stockService.increaseStock(productId, quantity, reason, operatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock increased successfully");
        response.put("currentStock", stockService.getCurrentStock(productId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decrease")
    public ResponseEntity<Map<String, Object>> decreaseStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) Long operatorId) {
        stockService.decreaseStock(productId, quantity, reason, operatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock decreased successfully");
        response.put("currentStock", stockService.getCurrentStock(productId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adjust")
    public ResponseEntity<Map<String, Object>> adjustStock(
            @RequestParam Long productId,
            @RequestParam Integer newStock,
            @RequestParam String reason,
            @RequestParam(required = false) Long operatorId) {
        stockService.adjustStock(productId, newStock, reason, operatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock adjusted successfully");
        response.put("currentStock", stockService.getCurrentStock(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getCurrentStock(@PathVariable Long productId) {
        Integer stock = stockService.getCurrentStock(productId);
        if (stock == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("currentStock", stock);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs/{productId}")
    public ResponseEntity<IPage<StockLogDto>> getStockLogs(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(stockService.getStockLogs(productId, page, size));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        boolean isSufficient = stockService.isStockSufficient(productId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("quantity", quantity);
        response.put("isSufficient", isSufficient);
        response.put("currentStock", stockService.getCurrentStock(productId));
        return ResponseEntity.ok(response);
    }
}
