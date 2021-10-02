package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SalesMonth;
import java.util.List;

public class SalesMonthMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SalesMonth> data = null;

    public SalesMonthMain(Integer recordsTotal, Integer recordsFiltered, List<SalesMonth> data) {
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

    public List<SalesMonth> getData() {
        return data;
    }

    public void setData(List<SalesMonth> data) {
        this.data = data;
    }

}
