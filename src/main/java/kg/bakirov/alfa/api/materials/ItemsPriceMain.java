package kg.bakirov.alfa.api.materials;

import kg.bakirov.alfa.models.items.ItemsPrice;

import java.util.List;

public class ItemsPriceMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<ItemsPrice> data = null;

    public ItemsPriceMain(Integer recordsTotal, Integer recordsFiltered, List<ItemsPrice> data) {
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

    public List<ItemsPrice> getData() {
        return data;
    }

    public void setData(List<ItemsPrice> data) {
        this.data = data;
    }

}
