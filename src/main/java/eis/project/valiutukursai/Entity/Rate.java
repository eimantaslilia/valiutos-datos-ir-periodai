package eis.project.valiutukursai.Entity;

public class Rate {

    private String date;
    private String currencyTypeAbbr;
    private Double amount;

    public Rate(String date, String currencyTypeAbbr, Double amount) {
        this.date = date;
        this.currencyTypeAbbr = currencyTypeAbbr;
        this.amount = amount;
    }

    public Rate() {
    }

    public String getDate() {
        return date;
    }

    public String getCurrencyTypeAbbr() {
        return currencyTypeAbbr;
    }

    public Double getAmount() {
        return amount;
    }


}
