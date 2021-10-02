package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SaleDetail;

import java.util.List;

public class SalesDetailMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SaleDetail> data = null;

    public SalesDetailMain(Integer recordsTotal, Integer recordsFiltered, List<SaleDetail> data) {
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

    public List<SaleDetail> getData() {
        return data;
    }

    public void setData(List<SaleDetail> data) {
        this.data = data;
    }

}
