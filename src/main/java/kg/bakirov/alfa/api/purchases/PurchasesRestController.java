package kg.bakirov.alfa.api.purchases;

import kg.bakirov.alfa.models.Account;
import kg.bakirov.alfa.repositories.MainRepository;
import kg.bakirov.alfa.repositories.PurchasesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchasesRestController {

    private final PurchasesRepository purchasesRepository;
    private final MainRepository mainRepository;

    @Autowired
    public PurchasesRestController(MainRepository mainRepository, PurchasesRepository purchasesRepository) {
        this.mainRepository = mainRepository;
        this.purchasesRepository = purchasesRepository;
    }

    @PostMapping("/fiche")
    public PurchasesFicheMain getPurchaseFicheMain() {
        return purchasesRepository.getPurchasesFicheMain();
    }

    @GetMapping("/accountsList/{firmNo}")
    public List<Account> getAccountsList(@PathVariable("firmNo") int id) {
        return mainRepository.getAccountsList(id);
    }

    @PostMapping("/total")
    public PurchasesTotalMain getPurchaseTotal() {
        return purchasesRepository.getPurchasesTotalMain();
    }

    @PostMapping("/month")
    public PurchasesMonthMain getPurchaseMonth() {
        return purchasesRepository.getPurchasesMonthMain();
    }

    @PostMapping("/client")
    public PurchasesClientMain getPurchaseClient() {
        return purchasesRepository.getPurchasesClientsMain();
    }


}
