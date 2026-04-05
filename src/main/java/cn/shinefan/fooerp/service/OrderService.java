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
