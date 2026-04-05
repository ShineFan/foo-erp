package cn.shinefan.fooerp.service;

import cn.shinefan.fooerp.mapper.ProductConverter;
import cn.shinefan.fooerp.mapper.ProductMapper;
import cn.shinefan.fooerp.model.Product;
import cn.shinefan.fooerp.util.SnowflakeIdGenerator;
import cn.shinefan.fooerp.web.dto.ProductDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        Product product = ProductConverter.toEntity(dto);
        product.setId(idGenerator.nextId());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        return ProductConverter.toDto(product);
    }

    @Override
    public ProductDto getById(Long id) {
        Product product = productMapper.selectById(id);
        return ProductConverter.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product product = ProductConverter.toEntity(dto);
        product.setId(id);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        return ProductConverter.toDto(productMapper.selectById(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public IPage<ProductDto> list(int page, int size) {
        Page<Product> productPage = new Page<>(page, size);
        IPage<Product> result = productMapper.selectPage(productPage, new QueryWrapper<>());
        return result.convert(ProductConverter::toDto);
    }
}
