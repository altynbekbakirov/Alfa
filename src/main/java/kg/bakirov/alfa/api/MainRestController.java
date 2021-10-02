package kg.bakirov.alfa.api;

import kg.bakirov.alfa.models.Period;
import kg.bakirov.alfa.repositories.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MainRestController {

    private final MainRepository mainRepository;

    @Autowired
    public MainRestController(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    @GetMapping("/{firmNo}")
    public List<Period> getPeriodsByFirmNo(@PathVariable("firmNo") int id) {
        return mainRepository.getPeriodList(id);
    }

    @GetMapping("/network/{firmNo}")
    public void deleteNetwork(@PathVariable("firmNo") int id) {
        mainRepository.deleteNetwork(id);
    }

}
