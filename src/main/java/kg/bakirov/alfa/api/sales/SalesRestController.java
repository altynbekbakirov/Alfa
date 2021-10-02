package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.Account;
import kg.bakirov.alfa.repositories.MainRepository;
import kg.bakirov.alfa.repositories.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesRestController {

    private final SalesRepository salesRepository;
    private final MainRepository mainRepository;

    @Autowired
    public SalesRestController(MainRepository mainRepository, SalesRepository salesRepository) {
        this.mainRepository = mainRepository;
        this.salesRepository = salesRepository;
    }

    @PostMapping("/fiche")
    public SalesFicheMain getSaleFicheMain() {
        return salesRepository.getSaleFicheMain();
    }

    @GetMapping("/accountsList/{firmNo}")
    public List<Account> getAccountsList(@PathVariable("firmNo") int id) {
        return mainRepository.getAccountsList(id);
    }

    @PostMapping("/total")
    public SalesTotalMain getSaleTotal() {
        return salesRepository.getSalesTotalMain();
    }

    @PostMapping("/month")
    public SalesMonthMain getSaleMonth() {
        return salesRepository.getSalesMonthMain();
    }

    @PostMapping("/client")
    public SalesClientMain getSaleClient() {
        return salesRepository.getSalesClientsMain();
    }

    @PostMapping("/detail")
    public SalesDetailMain getSaleDetail() {
        return salesRepository.getSalesDetailMain();
    }

    @PostMapping("/table")
    public SalesTableMain getSaleTable() {
        return salesRepository.getSalesTableMain();
    }

    @PostMapping("/manager")
    public SalesClientManagerMain getSaleManager() {
        return salesRepository.getSalesClientsManagerMain();
    }

}
