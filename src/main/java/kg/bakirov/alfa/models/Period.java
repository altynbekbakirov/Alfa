package kg.bakirov.alfa.models;

import java.util.Date;

public class Period {

    private int period_nr;
    private int period_firm_nr;
    private String period_begdate;
    private String period_enddate;
    private int period_active;

    public Period(int period_nr, int period_firm_nr, String period_begdate, String period_enddate, int period_active) {
        this.period_nr = period_nr;
        this.period_firm_nr = period_firm_nr;
        this.period_begdate = period_begdate;
        this.period_enddate = period_enddate;
        this.period_active = period_active;
    }

    public int getPeriod_nr() {
        return period_nr;
    }

    public void setPeriod_nr(int period_nr) {
        this.period_nr = period_nr;
    }

    public int getPeriod_firm_nr() {
        return period_firm_nr;
    }

    public void setPeriod_firm_nr(int period_firm_nr) {
        this.period_firm_nr = period_firm_nr;
    }

    public String getPeriod_begdate() {
        return period_begdate;
    }

    public void setPeriod_begdate(String period_begdate) {
        this.period_begdate = period_begdate;
    }

    public String getPeriod_enddate() {
        return period_enddate;
    }

    public void setPeriod_enddate(String period_enddate) {
        this.period_enddate = period_enddate;
    }

    public int getPeriod_active() {
        return period_active;
    }

    public void setPeriod_active(int period_active) {
        this.period_active = period_active;
    }

    @Override
    public String toString() {
        return "Period{" +
                "period_nr=" + period_nr +
                ", period_firm_nr=" + period_firm_nr +
                ", period_begdate='" + period_begdate + '\'' +
                ", period_enddate='" + period_enddate + '\'' +
                ", period_active=" + period_active +
                "}\n";
    }
}
