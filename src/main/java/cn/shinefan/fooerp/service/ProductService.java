package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.web.dto.ProductDto;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ProductService {
    ProductDto create(ProductDto dto);
    ProductDto getById(Long id);
    ProductDto update(Long id, ProductDto dto);
    void delete(Long id);
    IPage<ProductDto> list(int page, int size);
}
