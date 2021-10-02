package kg.bakirov.alfa.api.materials;

import kg.bakirov.alfa.models.items.ItemsFiche;

import java.util.List;

public class ItemsFicheMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<ItemsFiche> data = null;

    public ItemsFicheMain(Integer recordsTotal, Integer recordsFiltered, List<ItemsFiche> data) {
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

    public List<ItemsFiche> getData() {
        return data;
    }

    public void setData(List<ItemsFiche> data) {
        this.data = data;
    }

}
