package ru.maliutin.diesel.web.mappers.impl;

import org.springframework.stereotype.Service;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.web.dto.product.ProductDto;
import ru.maliutin.diesel.web.mappers.ProductMapper;

@Service
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDto toDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getProgramNumber());
        productDto.setNumber(product.getCatalogNumber());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setAmount(product.getAmount());
        return productDto;
    }

    @Override
    public Product toEntity(ProductDto productDto) {
        Product product = new Product();
        product.setProgramNumber(productDto.getId());
        return product;
    }
}
