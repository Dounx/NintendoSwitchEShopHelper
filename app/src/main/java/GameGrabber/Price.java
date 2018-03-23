package GameGrabber;

import java.util.Date;

/**
 * It's a inner class for Game class
 */
public class Price {
    private String mPrice;
    private String mDiscountPrice;
    private String mPriceByCurrency;
    private String mDiscountPriceByCurrency;
    private String mDiscount;
    private Date mStartTime;
    private Date mEndTime;
    private String mCountryCode;
    private String mCountryName;
    private String mCurrency;

    public String getPriceByCurrency() {
        return mPriceByCurrency;
    }

    public void setPriceByCurrency(String priceByCurrency) {
        mPriceByCurrency = priceByCurrency;
    }

    public String getDiscountPriceByCurrency() {
        return mDiscountPriceByCurrency;
    }

    public void setDiscountPriceByCurrency(String discountPriceByCurrency) {
        mDiscountPriceByCurrency = discountPriceByCurrency;
    }

    public String getDiscount() {
        return mDiscount;
    }

    public void setDiscount(String discount) {
        mDiscount = discount;
    }

    public String getDiscountPrice() {
        return mDiscountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        mDiscountPrice = discountPrice;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Date startTime) {
        mStartTime = startTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Date endTiem) {
        mEndTime = endTiem;
    }

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
