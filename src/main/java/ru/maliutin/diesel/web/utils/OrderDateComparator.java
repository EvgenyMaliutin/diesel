package ru.maliutin.diesel.web.utils;

import ru.maliutin.diesel.domain.order.Orders;

import java.util.Comparator;

public class OrderDateComparator implements Comparator<Orders> {
    @Override
    public int compare(Orders o1, Orders o2) {
        if (o1.getDate().isAfter(o2.getDate()))
            return 1;
        if (o1.getDate().isBefore(o2.getDate()))
            return -1;
        return 0;
    }

    @Override
    public Comparator<Orders> reversed() {
        return Comparator.super.reversed();
    }
}
