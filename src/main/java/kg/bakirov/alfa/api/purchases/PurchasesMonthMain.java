package kg.bakirov.alfa.api.purchases;

import kg.bakirov.alfa.models.purchases.PurchasesMonth;

import java.util.List;

public class PurchasesMonthMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<PurchasesMonth> data = null;

    public PurchasesMonthMain(Integer recordsTotal, Integer recordsFiltered, List<PurchasesMonth> data) {
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

    public List<PurchasesMonth> getData() {
        return data;
    }

    public void setData(List<PurchasesMonth> data) {
        this.data = data;
    }

}
