package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SalesTotal;
import java.util.List;

public class SalesTotalMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SalesTotal> data = null;

    public SalesTotalMain(Integer recordsTotal, Integer recordsFiltered, List<SalesTotal> data) {
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

    public List<SalesTotal> getData() {
        return data;
    }

    public void setData(List<SalesTotal> data) {
        this.data = data;
    }

}
