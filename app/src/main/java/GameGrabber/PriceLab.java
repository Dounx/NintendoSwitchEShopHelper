package GameGrabber;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dounx on 2018/3/18.
 */

public class PriceLab {

    private static PriceLab sPriceLab;
    private Context mContext;
    public List<Price[]> mPriceList;
    public HashMap<String, Double> mRatesMap;

    public static PriceLab get(Context context) {
        if (sPriceLab == null) {
            sPriceLab = new PriceLab(context);
        }
        return sPriceLab;
    }

    private PriceLab(Context context) {
        mContext = context.getApplicationContext();
    }

    private void  queryPrice() {

    }

    ResponseListener mRatesListener = new ResponseListener() {
        @Override
        public void onDataReceivedSuccess(HashMap hashMap) {
            mRatesMap = hashMap;
        }

        @Override
        public void onDataReceivedFailed() {
            mRatesMap = null;
        }
    };

    private void queryRates(String base) {
        RatesQueryTask ratesQueryTask = new RatesQueryTask(mContext, mRatesListener);
        ratesQueryTask.execute(base);
    }

    private void initData() {
        queryPrice();
        queryRates("CNY");
    }
}
