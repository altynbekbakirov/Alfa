package kg.bakirov.alfa.api.finances;

import kg.bakirov.alfa.models.finances.Customer;
import kg.bakirov.alfa.models.finances.CustomerExtract;

import java.util.List;

public class FinanceExtractMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<CustomerExtract> data = null;

    public FinanceExtractMain(Integer recordsTotal, Integer recordsFiltered, List<CustomerExtract> data) {
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

    public List<CustomerExtract> getData() {
        return data;
    }

    public void setData(List<CustomerExtract> data) {
        this.data = data;
    }

}
