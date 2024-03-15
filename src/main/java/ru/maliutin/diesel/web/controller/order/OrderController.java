package ru.maliutin.diesel.web.controller.order;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.maliutin.diesel.domain.order.*;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.domain.user.User;
import ru.maliutin.diesel.service.AnonymousOrderService;
import ru.maliutin.diesel.service.OrderService;
import ru.maliutin.diesel.service.ProductService;
import ru.maliutin.diesel.service.UserService;

import java.util.*;


@Controller
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController{

    private UserService userService;
    private OrderService orderService;
    private AnonymousOrderService anonymousOrderService;
    private ProductService productService;

    /**
     * Показ заказа с товарами.
     * @return
     */
    @GetMapping()
    public String viewOrder(@AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            HttpServletRequest request,
                            HttpServletResponse response){
        if (userDetails != null){
            // Если пользователь аутентифицирован получаем его заказ со статусом СОЗДАН и выводим в представление
            User user = userService.getByUsername(userDetails.getUsername());
            Orders order = orderService.getCreatedOrderByUser(user);
            List<OrderProduct> products = order.getProducts();
            model.addAttribute("products", products);
            model.addAttribute("order", order);
        }else {
            // Если пользователь не аутентифицирован
            String userId = getAnonymousUserId(request);
            setCookieUserId(userId, response);
            AnonymousOrder order = anonymousOrderService.getOrderByUserId(userId);
            List<AnonymousOrderProduct> products = order.getProducts();
            model.addAttribute("products", products);
            model.addAttribute("order", order);
        }
        return "orders/basket";
    }

    /**
     * Добавление товара в корзину.
     * @return
     */
    @PostMapping("/add/{id}")
    public String addToOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable("id") int productId,
                             @RequestParam(value = "amount", required = false) Integer amount,
                             HttpSession session,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestHeader(value = "referer", required = false) String referer,
                             RedirectAttributes redirectAttributes) {

        String lastFind = null;
        if (session.getAttribute("find") != null){
            lastFind = session.getAttribute("find").toString();
        }
        if (userDetails != null){
            // Если пользователь аутентифицирован
            User user = userService.getByUsername(userDetails.getUsername());
            Product productInOrder = productService.findByProgramNumber(productId);
            orderService.addProductToOrder(user, productInOrder, amount);
        }else{
            // Если пользователь не аутентифицирован
            String userId = getAnonymousUserId(request);
            setCookieUserId(userId, response);
            Product productInOrder = productService.findByProgramNumber(productId);
            anonymousOrderService.addProductInOrder(userId, productInOrder, amount);
        }
        // Добавляем ответ об успешном добавлении в корзину
        redirectAttributes.addFlashAttribute("addToCartMessage", "Товар добавлен в корзину!");
        if (lastFind != null) {
            // Добавляем параметр поиска из предыдущего запроса для перенаправления.
            redirectAttributes.addAttribute("find", lastFind);
        }

        return "redirect:" + referer;
    }


    /**
     * Удаление продукта из заказа.
     * @return
     */
    @DeleteMapping("/del_product/{id}")
    public String removeProductFromOrder(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable("id") int productId,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestHeader(value = "referer", required = false) String referer) {
        if (userDetails != null) {
            // Если пользователь аутентифицирован
            User user = userService.getByUsername(userDetails.getUsername());
            Product productInOrder = productService.findByProgramNumber(productId);
            orderService.removeProductFromOrder(user, productInOrder);
            // Если пользователь не аутентифицирован
        }else{
            String userId = getAnonymousUserId(request);
            setCookieUserId(userId, response);
            Product productInOrder = productService.findByProgramNumber(productId);
            anonymousOrderService.removeProductFromOrder(userId, productInOrder);
        }
        return "redirect:" + referer;
    }

    /**
     * Обновление продукта в заказе.
     * @return
     */

    @PatchMapping("/edit_product/{id}")
    public String updateProductFromOrder(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable("id") int productId,
                                         @RequestParam(value = "amount", required = false) Integer amount,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestHeader(value = "referer", required = false) String referer){
        if (userDetails != null) {
            // Если пользователь аутентифицирован
            User user = userService.getByUsername(userDetails.getUsername());
            Product productInOrder = productService.findByProgramNumber(productId);
            orderService.updateProductToOrder(user, productInOrder, amount);
        }else{
            // Если пользователь не аутентифицирован
            String userId = getAnonymousUserId(request);
            setCookieUserId(userId, response);
            Product productInOrder = productService.findByProgramNumber(productId);
            anonymousOrderService.updateProductToOrder(userId, productInOrder, amount);
        }
        return "redirect:" + referer;
    }

    /**
     * Подтверждение заказа.
     * @return
     */
    @PatchMapping("/{id}")
    public String confirmationOrder(@PathVariable("id") long orderId,
                                    Authentication authentication,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes){
        // Если пользователь аутентифицирован
        if (authentication != null){
            // Подтверждаем заказ
            orderService.changeStatusOrderById(orderId, Status.CONFIRMED);
            redirectAttributes.addFlashAttribute("confirm", true);
            return "redirect:/users/personal";
        }
        // Если пользователь не аутентифицирован
        else{
            // Получаем сессию и вносим в нее id анонимного заказа
            HttpSession session = request.getSession();
            session.setAttribute("id_anonymous_order", orderId);
            // Перенаправляем запрос на метод объединения анонимного и аут. заказов
            return "redirect:/auth/login";
        }
    }

    /**
     * Удаление заказа.
     * @return перенаправление на ту же страницу, где был пользователь.
     */
    @DeleteMapping("/{id}")
    public String removeOrder(@PathVariable("id") long orderId,
                              Authentication authentication,
                              @RequestHeader(value = "referer", required = false) String referer){
        // Если пользователь аутентифицирован
        if (authentication != null){
            orderService.removeOrderById(orderId);
        }
        // Если пользователь не аутентифицирован
        else{
            anonymousOrderService.removeOrderById(orderId);
        }
        return "redirect:" + referer;
    }

    /**
     * Создание id анонимного пользователя.
     * @param request запрос для добавления куки.
     * @return id анонимного пользователя.
     */
    private String getAnonymousUserId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("anonymousUserId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Если уникальный идентификатор отсутствует, создаем его.
        return UUID.randomUUID().toString();
    }

    /**
     * Установка id анонимного пользователя в куки
     * @param anonymousUserId id анонимного пользователя
     * @param response ответ с добавленными куки
     */
    private void setCookieUserId(String anonymousUserId ,HttpServletResponse response){
        Cookie cookie = new Cookie("anonymousUserId", anonymousUserId);
        cookie.setMaxAge(30 * 24 * 60 * 60); // Например, на месяц
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Слияние заказов анонимной и аутентифицированных корзин
     * @param session
     * @return
     */
    @GetMapping("/merge")
    public String mergingOrders(HttpSession session, @AuthenticationPrincipal UserDetails userDetails){
        // Проверяем есть ли данные в сессии о id анонимного заказа
        if (session.getAttribute("id_anonymous_order") != null) {
            User user = userService.getByUsername(userDetails.getUsername());
            orderService.mergeOrders(user, Long.valueOf(session.getAttribute("id_anonymous_order").toString()));
        }
        return "redirect:/orders";
    }
}
