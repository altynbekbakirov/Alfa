package kg.bakirov.alfa.api.finances;

import kg.bakirov.alfa.models.finances.CustomerDebit;
import kg.bakirov.alfa.models.finances.CustomerExtract;

import java.util.List;

public class FinanceDebitMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<CustomerDebit> data = null;

    public FinanceDebitMain(Integer recordsTotal, Integer recordsFiltered, List<CustomerDebit> data) {
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

    public List<CustomerDebit> getData() {
        return data;
    }

    public void setData(List<CustomerDebit> data) {
        this.data = data;
    }

}
