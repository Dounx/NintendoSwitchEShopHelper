package GameGrabber;

/**
 * It's just a class represent supported countries by nintendo e-shop, here is nothing worth to read
 */
public class SupportedCountry {
    private String mName;
    private String mCode;
    private String mCurrency;
    private String mBelong;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public String getBelong() {
        return mBelong;
    }

    public void setBelong(String belong) {
        mBelong = belong;
    }
}
