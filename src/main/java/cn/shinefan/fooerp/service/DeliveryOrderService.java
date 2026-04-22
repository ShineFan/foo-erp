package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.web.dto.DeliveryOrderDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

public interface DeliveryOrderService {
    DeliveryOrderDto create(DeliveryOrderDto dto);
    DeliveryOrderDto getById(Long id);
    DeliveryOrderDto update(Long id, DeliveryOrderDto dto);
    void delete(Long id);
    IPage<DeliveryOrderDto> list(int page, int size, String status);
    List<DeliveryOrderDto> findByOrderId(Long orderId);
    DeliveryOrderDto updateDeliveryStatus(Long id, String status);
}
