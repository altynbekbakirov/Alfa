package kg.bakirov.alfa.api.finances;

import kg.bakirov.alfa.models.finances.Customer;

import java.util.List;

public class FinanceCustomerMain {

    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<Customer> data = null;

    public FinanceCustomerMain(Integer recordsTotal, Integer recordsFiltered, List<Customer> data) {
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

    public List<Customer> getData() {
        return data;
    }

    public void setData(List<Customer> data) {
        this.data = data;
    }

}
