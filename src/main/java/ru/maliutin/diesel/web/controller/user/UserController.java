package ru.maliutin.diesel.web.controller.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.maliutin.diesel.domain.order.OrderProduct;
import ru.maliutin.diesel.domain.order.Orders;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.service.OrderService;
import ru.maliutin.diesel.service.UserService;
import ru.maliutin.diesel.web.dto.user.UserDto;
import ru.maliutin.diesel.web.mappers.OrderMapper;
import ru.maliutin.diesel.web.mappers.UserMapper;
import ru.maliutin.diesel.web.utils.OrderDateComparator;
import ru.maliutin.diesel.web.utils.OrderStatusComparator;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/personal")
    public String ordersUser(@AuthenticationPrincipal UserDetails userDetails,Model model){
        User user = userService.getByUsername(userDetails.getUsername());
        model.addAttribute("orders", user.getOrders().stream()
                .sorted(new OrderStatusComparator()
                        .thenComparing(new OrderDateComparator().reversed()))
                .toList());
        return "user/personal";
    }
}
