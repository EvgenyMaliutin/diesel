package ru.maliutin.diesel.web.mappers;

import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.web.dto.product.ProductDto;

public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

}
