package ru.maliutin.diesel.web.utils;

import ru.maliutin.diesel.domain.order.Orders;

import java.util.Comparator;

public class OrderStatusComparator implements Comparator<Orders> {
    @Override
    public int compare(Orders o1, Orders o2) {
        return Integer.compare(o1.getOrderStatus().ordinal(), o2.getOrderStatus().ordinal());
    }
}
