package kg.bakirov.alfa.controllers;

import kg.bakirov.alfa.models.Account;
import kg.bakirov.alfa.models.Firm;
import kg.bakirov.alfa.models.Period;
import kg.bakirov.alfa.repositories.FinanceRepository;
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
import static kg.bakirov.alfa.controllers.HomeController.GLOBAL_PERIOD;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    public static String BEGDATE;
    public static String ENDDATE;
    public static String ACCOUNT1;
    public static String ACCOUNT2;
    public static String OPERATIONTYPE;

    MainRepository mainRepository;
    FinanceRepository financeRepository;

    @Autowired
    public FinanceController(MainRepository mainRepository, FinanceRepository financeRepository) {
        this.mainRepository = mainRepository;
        this.financeRepository = financeRepository;
    }


    /* ------------------------------------------ Выписка контрагентов ---------------------------------------------------- */

    @PostMapping("/extract")
    public String financeExtractPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                @RequestParam("begdate") String begdate,
                                @RequestParam("enddate") String enddate,
                                @RequestParam(required = false, name = "operation") String operation,
                                @RequestParam(required = false, name = "account1") String account1,
                                @RequestParam(required = false, name = "account2") String account2, Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;

        if (operation == null || operation.isEmpty()) {
            OPERATIONTYPE = "31,32,33,34,36,37,38,39,01,02,03,04,05,06,12,14";
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

        model.addAttribute("activePage", "financeExtract");
        model.addAttribute("pageTitle", "Выписка контрагентов");
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

        return "finance/financeExtract";
    }

    @GetMapping("/extract")
    public String financeExtract(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;
        OPERATIONTYPE = "31,32,33,34,36,37,38,39,01,02,03,04,05,06,12,14";

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

        model.addAttribute("activePage", "financeExtract");
        model.addAttribute("pageTitle", "Выписка контрагентов");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("operation", OPERATIONTYPE);
        model.addAttribute("operationList", operationList);
        model.addAttribute("accountList", accountList);

        return "finance/financeExtract";
    }


    /* ------------------------------------------ Акт сверки взаиморасчетов ---------------------------------------------------- */

    @PostMapping("/")
    public String financeIndexPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                   @RequestParam(required = false, name = "begdate") String begdate,
                                   @RequestParam(required = false, name = "enddate") String enddate,
                                   @RequestParam(required = false, name = "account1") String account1,
                                   @RequestParam(required = false, name = "account2") String account2,
                                   Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;

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

        model.addAttribute("activePage", "financeIndex");
        model.addAttribute("pageTitle", "Акт сверки взаиморасчетов");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);

        return "finance/finance";
    }

    @GetMapping("/")
    public String financeIndex(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;

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

        model.addAttribute("activePage", "financeIndex");
        model.addAttribute("pageTitle", "Акт сверки взаиморасчетов");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);

        return "finance/finance";
    }



    /* ------------------------------------------ Акт сверки взаиморасчетов ---------------------------------------------------- */

    @PostMapping("/debit")
    public String financeDebitPost(@RequestParam("firmno") int firmno, @RequestParam("periodno") int periodno,
                                   @RequestParam(required = false, name = "begdate") String begdate,
                                   @RequestParam(required = false, name = "enddate") String enddate,
                                   @RequestParam(required = false, name = "account1") String account1,
                                   @RequestParam(required = false, name = "account2") String account2,
                                   Model model) {

        BEGDATE = begdate;
        ENDDATE = enddate;
        ACCOUNT1 = account1;
        ACCOUNT2 = account2;

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

        model.addAttribute("activePage", "financeDebit");
        model.addAttribute("pageTitle", "Отчет по задолжностям");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);
        model.addAttribute("account1", account1);
        model.addAttribute("account2", account2);

        return "finance/financeDebit";
    }

    @GetMapping("/debit")
    public String financeDebit(Model model) {

        BEGDATE = null;
        ENDDATE = null;
        ACCOUNT1 = null;
        ACCOUNT2 = null;

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

        model.addAttribute("activePage", "financeDebit");
        model.addAttribute("pageTitle", "Отчет по задолжностям");
        model.addAttribute("firms", firmList);
        model.addAttribute("periods", periods);
        model.addAttribute("firmno", GLOBAL_FIRM_NO);
        model.addAttribute("periodno", GLOBAL_PERIOD);
        model.addAttribute("begdate", BEGDATE);
        model.addAttribute("enddate", ENDDATE);
        model.addAttribute("accountList", accountList);

        return "finance/financeDebit";
    }

}
