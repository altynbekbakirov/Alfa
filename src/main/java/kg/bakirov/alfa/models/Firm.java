package kg.bakirov.alfa.models;

public class Firm {

    private int firm_nr;
    private String firm_name;
    private String firm_title;

    public Firm(int firm_nr, String firm_name, String firm_title) {
        this.firm_nr = firm_nr;
        this.firm_name = firm_name;
        this.firm_title = firm_title;
    }

    public int getFirm_nr() {
        return firm_nr;
    }

    public void setFirm_nr(int firm_nr) {
        this.firm_nr = firm_nr;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getFirm_title() {
        return firm_title;
    }

    public void setFirm_title(String firm_title) {
        this.firm_title = firm_title;
    }
}
