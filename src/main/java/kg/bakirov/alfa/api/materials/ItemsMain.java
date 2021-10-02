package kg.bakirov.alfa.api.materials;

import kg.bakirov.alfa.models.items.Items;

import java.util.List;

public class ItemsMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<Items> data = null;

    public ItemsMain(Integer recordsTotal, Integer recordsFiltered, List<Items> data) {
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

    public List<Items> getData() {
        return data;
    }

    public void setData(List<Items> data) {
        this.data = data;
    }

}
