package kg.bakirov.alfa.api.purchases;

import kg.bakirov.alfa.models.purchases.PurchasesFiche;

import java.util.List;

public class PurchasesFicheMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<PurchasesFiche> data = null;

    public PurchasesFicheMain(Integer recordsTotal, Integer recordsFiltered, List<PurchasesFiche> data) {
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

    public List<PurchasesFiche> getData() {
        return data;
    }

    public void setData(List<PurchasesFiche> data) {
        this.data = data;
    }

}
