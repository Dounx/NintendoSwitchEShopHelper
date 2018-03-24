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
public class CountryGrabTask extends AsyncTask<String, Integer, Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;

    private final Context mContext;
    private final SupportedCountryLab mSupportedCountryLab;
    private final DownloadListener mListener;

    private List<SupportedCountry> mCountries;

    public CountryGrabTask(Context context, DownloadListener listener) {
        this.mContext = context;
        this.mSupportedCountryLab = SupportedCountryLab.get(mContext);
        mListener = listener;
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
            mSupportedCountryLab.mCountries = mCountries;
        } catch (Exception e) {
            e.printStackTrace();
            return TYPE_FAILED;
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