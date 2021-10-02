package kg.bakirov.alfa.api.purchases;

import kg.bakirov.alfa.models.purchases.PurchasesClient;

import java.util.List;

public class PurchasesClientMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<PurchasesClient> data = null;

    public PurchasesClientMain(Integer recordsTotal, Integer recordsFiltered, List<PurchasesClient> data) {
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

    public List<PurchasesClient> getData() {
        return data;
    }

    public void setData(List<PurchasesClient> data) {
        this.data = data;
    }

}
