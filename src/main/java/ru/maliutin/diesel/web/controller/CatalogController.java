package ru.maliutin.diesel.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.maliutin.diesel.domain.product.Product;
import ru.maliutin.diesel.service.ProductService;
import ru.maliutin.diesel.web.dto.product.ProductDto;
import ru.maliutin.diesel.web.dto.user.UserDto;
import ru.maliutin.diesel.web.mappers.ProductMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalog")
@AllArgsConstructor
public class CatalogController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @GetMapping()
    public String catalog(Model model){
        List<Product> products = productService.findAll();
        List<ProductDto> allProducts = new ArrayList<>();
        if (!products.isEmpty()){
            allProducts = products.stream().map(productMapper::toDto).collect(Collectors.toList());
        }
        model.addAttribute("products", allProducts);
        return "catalog/catalog";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable("id") int id, Model model){
        ProductDto product = productMapper.toDto(productService.findByProgramNumber(id));
        model.addAttribute("product", product);
        return "catalog/product";
    }

    @GetMapping("/find")
    public String find(HttpServletRequest request, HttpSession session, Model model){
        String find = request.getParameter("find");
        List<Product> products = productService.find(find);
        List<ProductDto> findProducts = new ArrayList<>();
        if (!products.isEmpty()){
            findProducts = products.stream().map(productMapper:: toDto).collect(Collectors.toList());
        }
        model.addAttribute("products", findProducts);
        session.setAttribute("find", find);
        return "catalog/catalog";
    }

    @GetMapping("/applying")
    public String findApplying(HttpServletRequest request,
                               HttpSession session, Model model){
        String find = request.getParameter("find");
        List <Product> products = productService.findByModelCar(find);
        List <ProductDto> findApplying = new ArrayList<>();
        if (!products.isEmpty()){
            findApplying = products.stream().map(productMapper::toDto).collect(Collectors.toList());
        }
        model.addAttribute("products", findApplying);
        session.setAttribute("find", find);
        return "catalog/catalog";
    }

    @ModelAttribute("user")
    public UserDto authUserDto(){
        return new UserDto();
    }

}
