package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SaleTable;

import java.util.List;

public class SalesTableMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SaleTable> data = null;

    public SalesTableMain(Integer recordsTotal, Integer recordsFiltered, List<SaleTable> data) {
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

    public List<SaleTable> getData() {
        return data;
    }

    public void setData(List<SaleTable> data) {
        this.data = data;
    }

}
