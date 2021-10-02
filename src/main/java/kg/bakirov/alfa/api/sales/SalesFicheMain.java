package kg.bakirov.alfa.api.sales;

import kg.bakirov.alfa.models.sales.SalesFiche;
import java.util.List;

public class SalesFicheMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<SalesFiche> data = null;

    public SalesFicheMain(Integer recordsTotal, Integer recordsFiltered, List<SalesFiche> data) {
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

    public List<SalesFiche> getData() {
        return data;
    }

    public void setData(List<SalesFiche> data) {
        this.data = data;
    }

}
