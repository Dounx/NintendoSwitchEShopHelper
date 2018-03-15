package GameGrabber;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The most waste time task.
 * Request all the countries have e-shop
 * Firstly get all countries
 * Then use price API find the supported countries
 */
public class SupportedCountryGrabTask extends AsyncTask<String, Integer, Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;

    private final DownloadListener mListener;
    private final Context mContext;
    private final SupportedCountryLab mSupportedCountryLab;

    private List<SupportedCountry> mCountries;

    public SupportedCountryGrabTask(Context context, DownloadListener listener) {
        this.mListener = listener;
        this.mContext = context;
        this.mSupportedCountryLab = SupportedCountryLab.get(mContext);
    }

    @Override
    protected Integer doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://restcountries.eu/rest/v2/all")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            parseJsonWithJSONObjectAndAddToList(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return TYPE_FAILED;
        }

        for (int i = 0; i < mCountries.size(); i++) {
            HttpUrl httpUrl1 = new HttpUrl.Builder()
                    .scheme("https")
                    .host("api.ec.nintendo.com")
                    .addPathSegment("v1")
                    .addPathSegment("price")
                    .addQueryParameter("lang", "en")
                    .addQueryParameter("country", mCountries.get(i).getCode())
                    .addQueryParameter("ids", "70010000000141,70010000000024,70010000000027")  // 1-2 switch's nsuid, with US, EU and JP
                    .build();

            Request priceRequest1 = new Request.Builder()
                    .url(httpUrl1)
                    .build();

            try {
                Response response = client.newCall(priceRequest1).execute();
                String responseData = response.body().string();

                JSONObject jsonObject = new JSONObject(responseData);

                // If sales_status is onsale, represent the e-shop is exist, and also can get the country belong to which area
                if (jsonObject.has("prices")) {
                    if (jsonObject.getJSONArray("prices").getJSONObject(0).getString("sales_status").equals("onsale")) {
                        mCountries.get(i).setBelong("US");
                    } else if (jsonObject.getJSONArray("prices").getJSONObject(1).getString("sales_status").equals("onsale")) {
                        mCountries.get(i).setBelong("EU");
                    } else if (jsonObject.getJSONArray("prices").getJSONObject(2).getString("sales_status").equals("onsale")) {
                        mCountries.get(i).setBelong("JP");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return TYPE_FAILED;
            }
        }

        for (SupportedCountry country : mCountries) {
            if (country.getBelong() != null) {
                mSupportedCountryLab.addSupportedCountry(country);
            }
        }

        return TYPE_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                mListener.onSuccess();
                break;
            case TYPE_FAILED:
                mListener.onFailed();
                break;
            default:
        }
    }

    private void parseJsonWithJSONObjectAndAddToList(String jsonData) {

        mCountries = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                SupportedCountry country = new SupportedCountry();
                country.setName(object.getString("name"));
                country.setCode(object.getString("alpha2Code"));
                country.setCurrency(object.getJSONArray("currencies").getJSONObject(0).getString("code"));
                mCountries.add(country);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
