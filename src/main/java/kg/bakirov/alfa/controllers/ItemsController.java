package kg.bakirov.alfa.controllers;

import kg.bakirov.alfa.models.items.Items;
import kg.bakirov.alfa.repositories.ItemsRepository;
import kg.bakirov.alfa.repositories.MainRepository;
import kg.bakirov.alfa.models.Firm;
import kg.bakirov.alfa.models.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_FIRM_NO;
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_PERIOD;

@Controller
@RequestMapping("/materials")
public class ItemsController {

    public static String ITEM1_CODE;
    public static String ITEM2_CODE;
    public static String GROUP1;
    public static String BEGDATE;
    public static String ENDDATE;
    public static String OPERATIONTYPE;

    MainRepository mainRepository;
    ItemsRepository itemsRepository;

    @Autowired
    public ItemsController(MainRepository mainRepository, ItemsRepository itemsRepository) {
        this.mainRepository = mainRepository;
        this.itemsRepository = itemsRepository;
    }


    /* ------------------------------------------ Остаток товаров ---------------------------------------------------- */

    @PostMapping()
    public String itemsPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                            @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                            @RequestParam("group1") String group1, @RequestParam("begdate") String begdate, Model model) {

        ITEM1_CODE = item1;
        ITEM2_CODE = item2;
        GROUP1 = group1;
        BEGDATE = begdate;

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        } else {
            GLOBAL_FIRM_NO = String.format("%03d", firmno);
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        } else {
            GLOBAL_PERIOD = String.format("%02d", periodno);
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "itemList");
        model.addAttribute("pageTitle", "Остатки товаров");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);
        model.addAttribute("begdate", BEGDATE);

        return "materials/index";
    }


    @GetMapping()
    public String itemsPage(Model model) {

        ITEM1_CODE = null;
        ITEM2_CODE = null;
        GROUP1 = null;
        BEGDATE = null;

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "itemList");
        model.addAttribute("pageTitle", "Остатки товаров");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);
        model.addAttribute("begdate", BEGDATE);

        return "materials/index";
    }



    /* ---------------------------------------- Инвентарный отчет ------------------------------------------------ */

    @PostMapping("/inventory")
    public String inventoryPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                                @RequestParam("group1") String group1, Model model) {

        ITEM1_CODE = item1;
        ITEM2_CODE = item2;
        GROUP1 = group1;

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        } else {
            GLOBAL_FIRM_NO = String.format("%03d", firmno);
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        } else {
            GLOBAL_PERIOD = String.format("%02d", periodno);
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "itemInventory");
        model.addAttribute("pageTitle", "Отчет инвентаризации");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "materials/inventory";
    }


    @GetMapping("/inventory")
    public String inventoryPage(Model model) {

        ITEM1_CODE = null;
        ITEM2_CODE = null;
        GROUP1 = null;

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "itemInventory");
        model.addAttribute("pageTitle", "Отчет инвентаризации");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "materials/inventory";
    }




    /* ---------------------------------------- Список документов ------------------------------------------------ */

    @PostMapping("/fiche")
    public String fichePost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                            @RequestParam("begdate") String begdate, @RequestParam("enddate") String enddate,
                            @RequestParam(required = false, name = "operation") String operation, Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;

        if (operation == null || operation.isEmpty()) {
            OPERATIONTYPE = "01,02,03,06,07,08,11,12,13,14,25,50,51";
        } else {
            OPERATIONTYPE = operation;
        }

        String[] operationList = OPERATIONTYPE.split(",");

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        } else {
            GLOBAL_FIRM_NO = String.format("%03d", firmno);
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        } else {
            GLOBAL_PERIOD = String.format("%02d", periodno);
        }

        model.addAttribute("activePage", "itemFiche");
        model.addAttribute("pageTitle", "Отчет инвентаризации");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("operation", OPERATIONTYPE);
        model.addAttribute("operationList", operationList);

        return "materials/fiche";
    }

    @GetMapping("/fiche")
    public String fichePage(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        OPERATIONTYPE = "01,02,03,06,07,08,11,12,13,14,25,50,51";

        String[] operationList = OPERATIONTYPE.split(",");

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));


        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        }

        model.addAttribute("activePage", "itemFiche");
        model.addAttribute("pageTitle", "Список документов");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("operation", OPERATIONTYPE);
        model.addAttribute("operationList", operationList);

        return "materials/fiche";
    }




    /* ---------------------------------------- Прайс лист ------------------------------------------------ */

    @PostMapping("/price")
    public String pricePost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                            @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                            @RequestParam("group1") String group1, Model model) {

        ITEM1_CODE = item1;
        ITEM2_CODE = item2;
        GROUP1 = group1;

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        } else {
            GLOBAL_FIRM_NO = String.format("%03d", firmno);
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        } else {
            GLOBAL_PERIOD = String.format("%02d", periodno);
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "itemPrice");
        model.addAttribute("pageTitle", "Прайс лист");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "materials/price";
    }


    @GetMapping("/price")
    public String pricePage(Model model) {

        ITEM1_CODE = null;
        ITEM2_CODE = null;
        GROUP1 = null;

        List<Firm> firmList = mainRepository.getFirmList();

        if (GLOBAL_FIRM_NO == null) {
            Firm firm = firmList.get(0);
            GLOBAL_FIRM_NO = String.format("%03d", firm.getFirm_nr());
        }

        List<Period> periods = mainRepository.getPeriodList(Integer.parseInt(GLOBAL_FIRM_NO));

        if (GLOBAL_PERIOD == null) {
            Period period = periods.get(0);
            GLOBAL_PERIOD = String.format("%02d", period.getPeriod_nr());
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "itemPrice");
        model.addAttribute("pageTitle", "Прайс лист");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "materials/price";
    }


}
