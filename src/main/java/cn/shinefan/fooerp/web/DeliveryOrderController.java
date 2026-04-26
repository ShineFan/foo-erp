package cn.shinefan.fooerp.web;

import cn.shinefan.fooerp.model.DeliveryStatus;
import cn.shinefan.fooerp.service.DeliveryOrderService;
import cn.shinefan.fooerp.web.dto.DeliveryOrderDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-orders")
public class DeliveryOrderController {

    private final DeliveryOrderService deliveryOrderService;

    @Autowired
    public DeliveryOrderController(DeliveryOrderService deliveryOrderService) {
        this.deliveryOrderService = deliveryOrderService;
    }

    @PostMapping
    public ResponseEntity<DeliveryOrderDto> create(@Valid @RequestBody DeliveryOrderDto dto) {
        return ResponseEntity.ok(deliveryOrderService.create(dto));
    }

    @GetMapping
    public ResponseEntity<IPage<DeliveryOrderDto>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) DeliveryStatus status) {
        return ResponseEntity.ok(deliveryOrderService.list(page, size, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryOrderDto> getById(@PathVariable Long id) {
        DeliveryOrderDto dto = deliveryOrderService.getById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryOrderDto>> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryOrderService.findByOrderId(orderId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryOrderDto> update(@PathVariable Long id, @Valid @RequestBody DeliveryOrderDto dto) {
        DeliveryOrderDto result = deliveryOrderService.update(id, dto);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = deliveryOrderService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryOrderDto> updateStatus(@PathVariable Long id, @RequestParam DeliveryStatus status) {
        DeliveryOrderDto dto = deliveryOrderService.updateDeliveryStatus(id, status);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}
