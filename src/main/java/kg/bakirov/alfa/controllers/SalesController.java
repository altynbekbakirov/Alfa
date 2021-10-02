package kg.bakirov.alfa.controllers;

import kg.bakirov.alfa.models.Account;
import kg.bakirov.alfa.models.Firm;
import kg.bakirov.alfa.models.items.Items;
import kg.bakirov.alfa.models.Period;
import kg.bakirov.alfa.repositories.ItemsRepository;
import kg.bakirov.alfa.repositories.MainRepository;
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
@RequestMapping("/sales")
public class SalesController {

    public static String BEGDATE;
    public static String ENDDATE;
    public static String OPERATIONTYPE;
    public static String ACCOUNT1;
    public static String ACCOUNT2;
    public static String ITEM1_CODE;
    public static String ITEM2_CODE;
    public static String GROUP1;
    public static String YEAR_;

    MainRepository mainRepository;
    ItemsRepository itemsRepository;

    @Autowired
    public SalesController(MainRepository mainRepository, ItemsRepository itemsRepository) {
        this.mainRepository = mainRepository;
        this.itemsRepository = itemsRepository;
    }


    /* ------------------------------------------ Список документов ---------------------------------------------------- */

    @PostMapping("/fiche")
    public String saleFichePost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                @RequestParam("begdate") String begdate, @RequestParam("enddate") String enddate,
                                @RequestParam(required = false, name = "operation") String operation,
                                @RequestParam(required = false, name = "account1") String account1,
                                @RequestParam(required = false, name = "account2") String account2, Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;

        if (operation == null || operation.isEmpty()) {
            OPERATIONTYPE = "02,03,04,07,08,09";
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));

        model.addAttribute("activePage", "saleFiche");
        model.addAttribute("pageTitle", "Перечень документов продажи");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("operation", OPERATIONTYPE);
        model.addAttribute("operationList", operationList);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);

        return "sales/saleFiche";
    }

    @GetMapping("/fiche")
    public String saleFiche(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;
        OPERATIONTYPE = "02,03,04,07,08,09";

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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));

        model.addAttribute("activePage", "saleFiche");
        model.addAttribute("pageTitle", "Перечень документов продажи");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("operation", OPERATIONTYPE);
        model.addAttribute("operationList", operationList);
        model.addAttribute("accountList", accountList);

        return "sales/saleFiche";
    }



    /* ------------------------------------------ Распределение закупок по менеджерам ---------------------------------------------------- */

    @PostMapping("/manager")
    public String saleClientsManagerPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                  @RequestParam("begdate") String begdate, @RequestParam("enddate") String enddate,
                                  @RequestParam(required = false, name = "account1") String account1,
                                  @RequestParam(required = false, name = "account2") String account2,
                                  @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                                  @RequestParam("group1") String group1, Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));
        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleClientManager");
        model.addAttribute("pageTitle", "Распределение продаж по менеджерам");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleClientsManager";
    }

    @GetMapping("/manager")
    public String saleClientsManager(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ITEM1_CODE = null;
        ITEM2_CODE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));
        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleClientManager");
        model.addAttribute("pageTitle", "Распределение продаж по менеджерам");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleClientsManager";
    }



    /* ------------------------------------------ Распределение закупок по контрагентам ---------------------------------------------------- */

    @PostMapping("/clients")
    public String saleClientsPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                  @RequestParam("begdate") String begdate, @RequestParam("enddate") String enddate,
                                  @RequestParam(required = false, name = "account1") String account1,
                                  @RequestParam(required = false, name = "account2") String account2,
                                  @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                                  @RequestParam("group1") String group1, Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));
        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleClient");
        model.addAttribute("pageTitle", "Распределение продаж по клиентам");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleClients";
    }

    @GetMapping("/clients")
    public String saleClients(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ITEM1_CODE = null;
        ITEM2_CODE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));
        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleClient");
        model.addAttribute("pageTitle", "Распределение продаж по клиентам");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleClients";
    }



    /* ------------------------------------------ Итоговые цифры закупок ---------------------------------------------------- */

    @PostMapping("/total")
    public String getTotalPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                               @RequestParam("begdate") String begdate, @RequestParam("enddate") String enddate,
                               @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                               @RequestParam("group1") String group1, Model model) {

        ITEM1_CODE = item1;
        ITEM2_CODE = item2;
        GROUP1 = group1;
        BEGDATE = begdate;
        ENDDATE = enddate;

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

        model.addAttribute("activePage", "saleTotal");
        model.addAttribute("pageTitle", "Итоговые цифры продаж");
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
        model.addAttribute("enddate", ENDDATE);

        return "sales/saleTotal";
    }


    @GetMapping("/total")
    public String getTotalSale(Model model) {

        ITEM1_CODE = null;
        ITEM2_CODE = null;
        GROUP1 = null;
        BEGDATE = null;
        ENDDATE = null;

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

        model.addAttribute("activePage", "saleTotal");
        model.addAttribute("pageTitle", "Итоговые цифры продаж");
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
        model.addAttribute("enddate", ENDDATE);

        return "sales/saleTotal";
    }



    /* ------------------------------------------ Распределение закупок по месяцам ---------------------------------------------------- */

    @PostMapping("/month")
    public String getMonthPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
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
            String[] dateValue = period.getPeriod_begdate().split("\\.");
            YEAR_ = dateValue[2];
        } else {
            GLOBAL_PERIOD = String.format("%02d", periodno);
            for (Period period : periods) {
                if (period.getPeriod_nr() == periodno) {
                    String[] dateValue = period.getPeriod_begdate().split("\\.");
                    YEAR_ = dateValue[2];
                    break;
                }
            }
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleMonth");
        model.addAttribute("pageTitle", "Распределение продаж по месяцам");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleMonth";
    }

    @GetMapping("/month")
    public String saleMonth(Model model) {

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
            String[] dateValue = period.getPeriod_begdate().split("\\.");
            YEAR_ = dateValue[2];
        } else {
            for (Period period : periods) {
                if (period.getPeriod_nr() == Integer.parseInt(GLOBAL_PERIOD)) {
                    String[] dateValue = period.getPeriod_begdate().split("\\.");
                    YEAR_ = dateValue[2];
                    break;
                }
            }
        }

        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleMonth");
        model.addAttribute("pageTitle", "Распределение продаж по месяцам");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleMonth";
    }



    /* ------------------------------------------ Сводная таблица продаж ---------------------------------------------------- */

    @PostMapping("/table")
    public String saleTablePost(@RequestParam("firmno") int firmno,
                                @RequestParam("periodno") int periodno,
                                @RequestParam(required = false, name = "enddate") String enddate,
                                @RequestParam(required = false, name = "account1") String account1,
                                @RequestParam(required = false, name = "account2") String account2,
                                Model model) {


        ACCOUNT1 = account1;
        ACCOUNT2 = account2;
        ENDDATE = enddate;

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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));

        model.addAttribute("activePage", "saleTable");
        model.addAttribute("pageTitle", "Сводная таблица продаж");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);
        model.addAttribute("enddate", ENDDATE);

        return "sales/saleTable";
    }

    @GetMapping("/table")
    public String saleTable(Model model) {

        ACCOUNT1 = null;
        ACCOUNT2 = null;
        ENDDATE = null;

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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));


        model.addAttribute("activePage", "saleTable");
        model.addAttribute("pageTitle", "Сводная таблица продаж");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("accountList", accountList);
        model.addAttribute("enddate", ENDDATE);

        return "sales/saleTable";
    }



    /* ------------------------------------------ Подробный отчет продаж ---------------------------------------------------- */

    @PostMapping("/detail")
    public String saleDetailPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                 @RequestParam("begdate") String begdate, @RequestParam("enddate") String enddate,
                                 @RequestParam(required = false, name = "account1") String account1,
                                 @RequestParam(required = false, name = "account2") String account2,
                                 @RequestParam("item1") String item1, @RequestParam("item2") String item2,
                                 @RequestParam("group1") String group1, Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));
        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleDetail");
        model.addAttribute("pageTitle", "Подробный отчет продаж");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleDetail";
    }

    @GetMapping("/detail")
    public String saleDetail(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ITEM1_CODE = null;
        ITEM2_CODE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;
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

        List<Account> accountList = mainRepository.getAccountsList(Integer.parseInt(GLOBAL_FIRM_NO));
        List<Items> itemsList = itemsRepository.getItemsList();
        List<String> groupList = new ArrayList<>();

        for (Items items : itemsList) {
            groupList.add(items.getItem_group());
        }

        groupList = groupList.stream().distinct().collect(Collectors.toList());

        model.addAttribute("activePage", "saleDetail");
        model.addAttribute("pageTitle", "Подробный отчет продаж");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("itemsList", itemsList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("item1", ITEM1_CODE);
        model.addAttribute("item2", ITEM2_CODE);
        model.addAttribute("group1", GROUP1);

        return "sales/saleDetail";
    }

}
