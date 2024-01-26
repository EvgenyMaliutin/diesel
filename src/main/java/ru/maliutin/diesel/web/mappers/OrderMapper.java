package ru.maliutin.diesel.web.mappers;

import ru.maliutin.diesel.domain.order.Orders;
import ru.maliutin.diesel.web.dto.order.OrdersDto;

public interface OrderMapper {
    /**
     * Преобразование модели объекта Orders в объект данных OrdersDto.
     * @param orders объект модели.
     * @return объект данных.
     */
    OrdersDto toDto(Orders orders);

    /**
     * Преобразование объекта данных OrdersDto в объект модели Orders.
     * @param ordersDto объект данных.
     * @return объект модели.
     */
    Orders toEntity(OrdersDto ordersDto);
}

