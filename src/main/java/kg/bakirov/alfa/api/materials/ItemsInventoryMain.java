package kg.bakirov.alfa.api.materials;

import kg.bakirov.alfa.models.items.ItemsInventory;

import java.util.List;

public class ItemsInventoryMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<ItemsInventory> data = null;

    public ItemsInventoryMain(Integer recordsTotal, Integer recordsFiltered, List<ItemsInventory> data) {
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    public Integer getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Integer recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Integer getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Integer recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<ItemsInventory> getData() {
        return data;
    }

    public void setData(List<ItemsInventory> data) {
        this.data = data;
    }

}
