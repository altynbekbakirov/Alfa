package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SalesClient;
import kg.bakirov.alfa.models.sales.SalesClientManager;

import java.util.List;

public class SalesClientManagerMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SalesClientManager> data = null;

    public SalesClientManagerMain(Integer recordsTotal, Integer recordsFiltered, List<SalesClientManager> data) {
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

    public List<SalesClientManager> getData() {
        return data;
    }

    public void setData(List<SalesClientManager> data) {
        this.data = data;
    }

}
