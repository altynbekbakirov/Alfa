package kg.bakirov.alfa.api.purchases;

import kg.bakirov.alfa.models.purchases.PurchasesTotal;

import java.util.List;

public class PurchasesTotalMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<PurchasesTotal> data = null;

    public PurchasesTotalMain(Integer recordsTotal, Integer recordsFiltered, List<PurchasesTotal> data) {
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

    public List<PurchasesTotal> getData() {
        return data;
    }

    public void setData(List<PurchasesTotal> data) {
        this.data = data;
    }

}
