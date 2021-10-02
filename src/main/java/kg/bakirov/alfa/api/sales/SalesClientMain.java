package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SalesClient;
import java.util.List;

public class SalesClientMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SalesClient> data = null;

    public SalesClientMain(Integer recordsTotal, Integer recordsFiltered, List<SalesClient> data) {
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

    public List<SalesClient> getData() {
        return data;
    }

    public void setData(List<SalesClient> data) {
        this.data = data;
    }

}
