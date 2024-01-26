package ru.maliutin.diesel.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import ru.maliutin.diesel.domain.MailType;
import ru.maliutin.diesel.service.MailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/help")
@RequiredArgsConstructor
public class HelpController {

    private final MailService mailService;

    @GetMapping("/about")
    public String about() {
        return "help/about";
    }

    @GetMapping("/delivery")
    public String delivery() {
        return "help/delivery";
    }

    @GetMapping("/payment")
    public String payment() {
        return "help/payment";
    }

    @GetMapping("/return")
    public String refund() {
        return "help/return";
    }

    @GetMapping("/call_me")
    public String callMe(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone) {
        if (name != null && phone != null) {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("phone", phone);
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
            context.setVariable("time_request", localDateTime.format(formatter));
            mailService.sendMail("company_mail", MailType.CALL_BACK, context);
        }
        return "redirect:/home";
    }
}
