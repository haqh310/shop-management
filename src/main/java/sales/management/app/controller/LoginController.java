package sales.management.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping({"/login", "/login/"})
    public String login() {
        return "login";
    }

}
