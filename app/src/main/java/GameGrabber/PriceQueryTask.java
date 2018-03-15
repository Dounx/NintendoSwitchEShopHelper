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
import java.util.Iterator;
import java.util.List;

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
public class PriceQueryTask extends AsyncTask<String, Integer, Price> {

    private final DownloadListener mListener;
    private final Context mContext;
    private final SupportedCountryLab mSupportedCountryLab;
    private final GameLab mGameLab;
    private final Game mGame;

    public PriceQueryTask(Context context, DownloadListener listener, Game game) {
        this.mListener = listener;
        this.mContext = context;
        this.mSupportedCountryLab = SupportedCountryLab.get(mContext);
        this.mGameLab = GameLab.get(mContext);
        this.mGame = game;
    }

    @Override
    protected Price doInBackground(String... params) {
        HashMap<String, Double> ratesMap = mGameLab.RatesMap;
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

        List<Price> priceList = new ArrayList<>();

        if (usNsuid != null) {
            for (SupportedCountry country : usCountryList) {
                Price price = queryPrice(country.getCode(), usNsuid);
                if (price == null) {
                    return null;
                }
                priceList.add(price);
            }
        }
        if (euNsuid != null) {
            for (SupportedCountry country : euCountryList) {
                Price price = queryPrice(country.getCode(), euNsuid);
                if (price == null) {
                    return null;
                }
                priceList.add(price);
            }
        }
        if (jpNsuid != null) {
            for (SupportedCountry country : jpCountryList) {
                Price price = queryPrice(country.getCode(), jpNsuid);
                if (price == null) {
                    return null;
                }
                priceList.add(price);
            }
        }

        //// Get the current rates, and save it until app is destroy
        if (ratesMap == null) {
           ratesMap = queryRates("CNY");
            mGameLab.RatesMap = ratesMap;
        }

        for (Price price : priceList) {
            double rates = ratesMap.get(price.getCurrency());

            // calculate price
            double basePrice = Double.parseDouble(price.getPrice()) / rates;
            price.setPrice(String.valueOf(basePrice));
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

        return price;
    }

    private Price queryPrice(String countryCode, String nsuid) {
        OkHttpClient client = new OkHttpClient();
        client.retryOnConnectionFailure();

        Price parseData = null;

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.ec.nintendo.com")
                .addPathSegment("v1")
                .addPathSegment("price")
                .addQueryParameter("country",countryCode)
                .addQueryParameter("lang", "en")
                .addQueryParameter("ids", nsuid)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            parseData = parsePriceJsonData(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parseData;
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

        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            ratesMap = parseRatesJsonData(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ratesMap;
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
