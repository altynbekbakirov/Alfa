package kg.bakirov.alfa.api.finances;

import kg.bakirov.alfa.repositories.FinanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
public class FinanceRestController {

    private final FinanceRepository financeRepository;

    @Autowired
    public FinanceRestController(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;
    }


    @PostMapping()
    public FinanceCustomerMain getMainFinancePost() {
        return financeRepository.getCustomerMain();
    }

    @PostMapping("/extract")
    public FinanceExtractMain getFinanceExtractMain() {
        return financeRepository.getFinanceExtractMain();
    }

    @PostMapping("/debit")
    public FinanceDebitMain getFinanceDebitMain() {
        return financeRepository.getFinanceDebitMain();
    }

}
