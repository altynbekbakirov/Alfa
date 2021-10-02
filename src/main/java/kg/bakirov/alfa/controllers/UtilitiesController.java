package kg.bakirov.alfa.controllers;

import kg.bakirov.alfa.models.Firm;
import kg.bakirov.alfa.repositories.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_FIRM_NO;


@Controller
@RequestMapping("/utilities")
public class UtilitiesController {

    MainRepository mainRepository;

    @Autowired
    public UtilitiesController(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    @RequestMapping("/calendar")
    public String calendarPage() {
        return "/utilities/calendar";
    }

    @GetMapping("/network")
    public String networkPage(Model model) {
        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        }

        model.addAttribute("firms", firmList);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);

        return "/utilities/network";
    }


}
