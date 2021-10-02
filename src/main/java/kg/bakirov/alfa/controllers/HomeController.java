package kg.bakirov.alfa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    public static String GLOBAL_FIRM_NO;
    public static String GLOBAL_PERIOD;

    @GetMapping()
    public String homeMethod(Model model) {

        model.addAttribute("activePage", "homePage");
        model.addAttribute("pageTitle", "Alfa Reports");
        return "index";
    }

}
