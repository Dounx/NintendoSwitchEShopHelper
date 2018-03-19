package GameGrabber;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import me.dounx.nintendoeshophelper.GameAdapter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The most complex task
 * Request with a game and the respond is a json style
 * Nintendo's API can only return a game with a country one time ( Or a country with 50 games )
 * Game's nsuid represent a game
 */
public class PriceQueryTask extends AsyncTask<Game, Integer, Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;

    private final SupportedCountryLab mSupportedCountryLab;
    private final Context mContext;
    private final GameLab mGameLab;
    private Game mGame;
    private DownloadListener mListener;

    public PriceQueryTask(Context context, DownloadListener listener) {
        this.mContext = context;
        this.mGameLab = GameLab.get(mContext);
        this.mSupportedCountryLab = SupportedCountryLab.get(mContext);
        mListener = listener;
    }

    @Override
    protected Integer doInBackground(Game... params) {
        mGame = params[0];
        HashMap<String, Double> ratesMap = mGameLab.mRatesMap;
        List<SupportedCountry> supportedCountryList = mSupportedCountryLab.getSupportedCountries();

        List<SupportedCountry> usCountryList = new ArrayList<>();
        List<SupportedCountry> euCountryList = new ArrayList<>();
        List<SupportedCountry> jpCountryList = new ArrayList<>();

        for (SupportedCountry supportedCountry : supportedCountryList) {
            switch (supportedCountry.getBelong()) {
                case "US":
                    usCountryList.add(supportedCountry);
                    break;
                case "EU":
                    euCountryList.add(supportedCountry);
                    break;
                default:
                    jpCountryList.add(supportedCountry);
                    break;
            }
        }

        String usNsuid = mGame.getUsNsUid();
        String euNsuid = mGame.getEuNsUid();
        String jpNsuid = mGame.getJpNsUid();

        List<HttpUrl> httpUrls = new ArrayList<>();

        if (usNsuid != null) {
            for (SupportedCountry country : usCountryList) {
                HttpUrl url = buildHttpUrl(country.getCode(), usNsuid);
                httpUrls.add(url);
            }
        }
        if (euNsuid != null) {
            for (SupportedCountry country : euCountryList) {
                HttpUrl url = buildHttpUrl(country.getCode(), euNsuid);
                httpUrls.add(url);
            }
        }
        if (jpNsuid != null) {
            for (SupportedCountry country : jpCountryList) {
                HttpUrl url = buildHttpUrl(country.getCode(), jpNsuid);
                httpUrls.add(url);
            }
        }

        List<Price> priceList = queryPrice(httpUrls);

        for (Price price : priceList) {
            double rates = ratesMap.get(price.getCurrency());

            // calculate price
            double basePrice = Double.parseDouble(price.getPrice()) / rates;
            price.setPrice(String.valueOf(String.format("%.2f", basePrice)));  // Round it to 2 decimal places
        }

        // Sort list
        Collections.sort(priceList,new Comparator<Price>(){
            public int compare(Price arg0, Price arg1) {
                return arg0.getPrice().compareTo(arg1.getPrice());
            }
        });

        // Just for debug
        Log.d("Price", "Lowest Price: " + priceList.get(0).getPrice());
        Log.d("Price", "Lowest Country: " + mSupportedCountryLab.getCountryName(priceList.get(0).getCountryCode()));

        Price price = new Price();
        price.setPrice(priceList.get(0).getPrice());
        price.setCountryCode(priceList.get(0).getCountryCode());
        price.setCountryName(mSupportedCountryLab.getCountryName(priceList.get(0).getCountryCode()));
        price.setCurrency(priceList.get(0).getCurrency());
        mGame.setPrice(price);
        return TYPE_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (integer == TYPE_SUCCESS) {
            mListener.onSuccess();
        } else {
            mListener.onFailed();
        }
    }
    private List<Price> queryPrice(List<HttpUrl> httpUrls) {
        OkHttpClient client = new OkHttpClient();
        List<Price> priceList = new ArrayList<>();

        for (HttpUrl url : httpUrls) {
            Price parseData;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()){
                String responseData = response.body().string();
                parseData = parsePriceJsonData(responseData);
                priceList.add(parseData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return priceList;
    }

    private HttpUrl buildHttpUrl(String countryCode, String nsuid) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.ec.nintendo.com")
                .addPathSegment("v1")
                .addPathSegment("price")
                .addQueryParameter("country",countryCode)
                .addQueryParameter("lang", "en")
                .addQueryParameter("ids", nsuid)
                .build();
        return httpUrl;
    }

    private Price parsePriceJsonData(String jsonData) {
        Price price = new Price();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray pricesArray = jsonObject.getJSONArray("prices");
            JSONObject priceObject = pricesArray.getJSONObject(0);

            if (priceObject.getString("sales_status").equals("onsale")) {
                JSONObject regularPriceObject = priceObject.getJSONObject("regular_price");

                price.setPrice(regularPriceObject.getString("raw_value"));
                price.setCountryCode(jsonObject.getString("country"));
                price.setCurrency(regularPriceObject.getString("currency"));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Wrong data", jsonData);
        }
        return price;
    }
}
