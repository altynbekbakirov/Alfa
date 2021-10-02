package kg.bakirov.alfa.models.items;

public class Items {

    private String item_code;
    private String item_name;
    private String item_group;
    private double item_purAmount;
    private double item_purCurr;
    private double item_salAmount;
    private double item_salCurr;
    private double item_onHand;
    private String item_lastTrDate;

    public Items(String item_code, String item_name, String item_group) {
        this.item_code = item_code;
        this.item_name = item_name;
        this.item_group = item_group;
    }

    public Items(String item_code, String item_name, String item_group, double item_purAmount, double item_purCurr, double item_salAmount, double item_salCurr, double item_onHand, String item_lastTrDate) {
        this.item_code = item_code;
        this.item_name = item_name;
        this.item_group = item_group;
        this.item_purAmount = item_purAmount;
        this.item_purCurr = item_purCurr;
        this.item_salAmount = item_salAmount;
        this.item_salCurr = item_salCurr;
        this.item_onHand = item_onHand;
        this.item_lastTrDate = item_lastTrDate;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_group() {
        return item_group;
    }

    public void setItem_group(String item_group) {
        this.item_group = item_group;
    }

    public double getItem_purAmount() {
        return item_purAmount;
    }

    public void setItem_purAmount(double item_purAmount) {
        this.item_purAmount = item_purAmount;
    }

    public double getItem_purCurr() {
        return item_purCurr;
    }

    public void setItem_purCurr(double item_purCurr) {
        this.item_purCurr = item_purCurr;
    }

    public double getItem_salAmount() {
        return item_salAmount;
    }

    public void setItem_salAmount(double item_salAmount) {
        this.item_salAmount = item_salAmount;
    }

    public double getItem_salCurr() {
        return item_salCurr;
    }

    public void setItem_salCurr(double item_salCurr) {
        this.item_salCurr = item_salCurr;
    }

    public double getItem_onHand() {
        return item_onHand;
    }

    public void setItem_onHand(double item_onHand) {
        this.item_onHand = item_onHand;
    }

    public String getItem_lastTrDate() {
        return item_lastTrDate;
    }

    public void setItem_lastTrDate(String item_lastTrDate) {
        this.item_lastTrDate = item_lastTrDate;
    }

}
