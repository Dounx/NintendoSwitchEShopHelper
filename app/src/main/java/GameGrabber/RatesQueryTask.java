package GameGrabber;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dounx on 2018/3/18.
 */

public class RatesQueryTask extends AsyncTask<String, Integer, Integer> {
    private Context mContext;
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;

    public RatesQueryTask(Context context) {
        mContext = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        HashMap ratesMap = queryRates(params[0]);    // Params[0] is base currency

        if (ratesMap != null) {
            GameLab.get(mContext).mRatesMap = ratesMap;
            return TYPE_SUCCESS;
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (integer == TYPE_SUCCESS) {

        } else {
        }
    }

    private HashMap<String, Double> queryRates(String base) {
        HashMap<String, Double> ratesMap = null;
        OkHttpClient client = new OkHttpClient();

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.fixer.io")
                .addPathSegment("latest")
                .addQueryParameter("base", base)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body().string();
            ratesMap = parseRatesJsonData(responseData);
            ratesMap.put(base, 1.0);    // The response data doesn't have the original currency
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ratesMap;
    }

    private HashMap<String, Double> parseRatesJsonData(String jsonData) {
        HashMap<String, Double> ratesMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");

            //// Parse JSONObject to map
            Iterator<String> keys = ratesObject.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Double value = (Double)ratesObject.get(key);
                ratesMap.put(key, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratesMap;
    }
}
