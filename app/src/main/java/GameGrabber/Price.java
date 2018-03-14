package GameGrabber;

public class Price {
    private String mPrice;
    private String mCountryCode;
    private String mCountryName;
    private String mCurrency;

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String Price) {
        mPrice = Price;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String countryName) {
        mCountryName = countryName;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }
}
