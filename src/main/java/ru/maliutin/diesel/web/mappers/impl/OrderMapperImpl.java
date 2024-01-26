package ru.maliutin.diesel.web.mappers.impl;


import org.springframework.stereotype.Service;
import ru.maliutin.diesel.domain.order.Orders;
import ru.maliutin.diesel.web.dto.order.OrdersDto;
import ru.maliutin.diesel.web.mappers.OrderMapper;

@Service
public class OrderMapperImpl implements OrderMapper {
    @Override
    public OrdersDto toDto(Orders orders) {
        OrdersDto orderDto = new OrdersDto();
        orderDto.setId(orders.getId());
        orderDto.setDate(orders.getDate());
        orderDto.setOrderStatus(orders.getOrderStatus());
        return null;
    }

    @Override
    public Orders toEntity(OrdersDto ordersDto) {
        return null;
    }
}
