package kg.bakirov.alfa.api.materials;

import kg.bakirov.alfa.models.items.Items;
import kg.bakirov.alfa.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class ItemsRestController {

    private final ItemsRepository itemsRepository;

    @Autowired
    public ItemsRestController(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }


    @PostMapping()
    public ItemsMain getMainItemsPost() {
        return itemsRepository.getItemsMain();
    }

    @PostMapping("/inventory")
    public ItemsInventoryMain getInventoryPost() {
        return itemsRepository.getItemsInventoryMain();
    }


    @PostMapping("/fiche")
    public ItemsFicheMain getFichePost() {
        return itemsRepository.getItemsFicheMain();
    }

    @PostMapping("/price")
    public ItemsPriceMain getPricePost() {
        return itemsRepository.getItemsPriceMain();
    }

    @GetMapping("/itemsList/{firmNo}")
    public List<Items> getItemsList(@PathVariable("firmNo") int id) {
        return itemsRepository.getItemsListByFirm(id);
    }


}
