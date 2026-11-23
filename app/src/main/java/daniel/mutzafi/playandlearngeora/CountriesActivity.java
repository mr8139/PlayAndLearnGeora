package daniel.mutzafi.playandlearngeora;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountriesActivity extends BaseActivity implements CountriesAdapter.OnItemClickListener {

    private RecyclerView rvCountries;
    private CountriesAdapter adapter;
    private List<Country> allCountries = new ArrayList<>();
    private ProgressBar progressBar;
    private EditText etSearch;
    private TextView tvDataSource;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String API_URL = "https://api.apilayer.com/geo/country/region/Europe";// "https://api.apilayer.com/restcountries/v3.1/all";
    // PASTE YOUR API KEY HERE:
    private static final String API_KEY = "zC4mwmu46NrQ6a8d32EG7EcEp1CJWDCL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);

        setupMenu(); // Inherited from BaseActivity

        rvCountries = findViewById(R.id.rvCountries);
        progressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.etSearch);
        tvDataSource = findViewById(R.id.tvDataSource);

        rvCountries.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CountriesAdapter(this, new ArrayList<>(), this);
        rvCountries.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCountries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadCountries();
    }

    @Override
    public void onItemClick(Country country) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        String populationFormatted = numberFormat.format(country.getPopulation());
        String areaFormatted = numberFormat.format(country.getArea());

        StringBuilder details = new StringBuilder();
        details.append(getString(R.string.capital_label)).append(": ").append(country.getCapital()).append("\n");
        details.append(getString(R.string.population_label)).append(" ").append(populationFormatted).append("\n");
        details.append(getString(R.string.region_label)).append(" ").append(country.getRegion()).append("\n");
        details.append(getString(R.string.area_label)).append(" ").append(areaFormatted).append(" km²\n");
        details.append(getString(R.string.currency_label)).append(" ").append(country.getCurrency()).append("\n");
        details.append(getString(R.string.languages_label)).append(" ").append(country.getLanguages()).append("\n");
        details.append(getString(R.string.borders_label)).append(" ").append(country.getBorders());

        new AlertDialog.Builder(this)
                .setTitle(country.getName())
                .setMessage(details.toString())
                .setPositiveButton(R.string.close, null)
                .show();
    }

    private void loadCountries() {
        progressBar.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            try {
                String jsonData = fetchJson(API_URL);
                if (jsonData != null && !jsonData.contains("no Route matched") && !jsonData.contains("error")) {
                    android.util.Log.d("API_DEBUG", "Response: " + jsonData);
                    List<Country> countries = parseCountries(jsonData);
                    runOnUiThread(() -> {
                        allCountries = countries;
                        adapter.updateList(allCountries);
                        progressBar.setVisibility(View.GONE);
                        tvDataSource.setText("API");
                        tvDataSource.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        tvDataSource.setVisibility(View.VISIBLE);
                    });
                } else {
                    loadLocalCountries("API not available, using offline data");
                }
            } catch (Exception e) {
                e.printStackTrace();
                loadLocalCountries("Error loading online data, switching to offline");
            }
        });
    }

    private void loadLocalCountries(String message) {
        try {
            InputStream is = getAssets().open("countries_data.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            List<Country> countries = parseCountries(sb.toString());
            runOnUiThread(() -> {
                allCountries = countries;
                adapter.updateList(allCountries);
                progressBar.setVisibility(View.GONE);
                tvDataSource.setText("Offline");
                tvDataSource.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                tvDataSource.setVisibility(View.VISIBLE);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(this, "Failed to load even local data!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    private String fetchJson(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("apikey", API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300) 
                ? conn.getInputStream() 
                : conn.getErrorStream();

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private List<Country> parseCountries(String json) throws Exception {
        List<Country> list = new ArrayList<>();
        JSONArray array = null;

        String trimmed = json.trim();
        if (trimmed.startsWith("{")) {
            JSONObject rootObj = new JSONObject(trimmed);
            // APILayer Geography API uses "results" or "data"
            if (rootObj.has("results")) {
                array = rootObj.getJSONArray("results");
            } else if (rootObj.has("data")) {
                array = rootObj.getJSONArray("data");
            } else if (rootObj.has("message")) {
                throw new Exception(rootObj.getString("message"));
            } else {
                // If it's a single object, wrap it
                array = new JSONArray();
                array.put(rootObj);
            }
        } else if (trimmed.startsWith("[")) {
            array = new JSONArray(trimmed);
        }

        if (array == null) {
            throw new Exception("Could not find data array in JSON response");
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            String name = obj.optString("name", "");
            // In some Geo APIs, the name is inside a "country" object
            if (name.isEmpty() && obj.has("country")) {
                Object countryObj = obj.get("country");
                if (countryObj instanceof JSONObject) {
                    name = ((JSONObject) countryObj).optString("name", "");
                } else {
                    name = countryObj.toString();
                }
            }

            String capital = obj.optString("capital", obj.optString("capital_city", "N/A"));
            
            String flagUrl = obj.optString("flag", "");
            if (flagUrl.isEmpty() && obj.has("flags")) {
                JSONObject flags = obj.optJSONObject("flags");
                if (flags != null) flagUrl = flags.optString("png", "");
            }

            long population = obj.optLong("population", 0);
            String region = obj.optString("region", "Unknown");

            // --- New Fields ---
            // 1. Currency
            String currencyStr = "N/A";
            if (obj.has("currencies")) {
                JSONArray curs = obj.optJSONArray("currencies");
                if (curs != null && curs.length() > 0) {
                    JSONObject c = curs.getJSONObject(0);
                    currencyStr = c.optString("name") + " (" + c.optString("symbol") + ")";
                }
            }

            // 2. Languages
            StringBuilder langs = new StringBuilder();
            if (obj.has("languages")) {
                JSONArray lArray = obj.optJSONArray("languages");
                if (lArray != null) {
                    for (int j = 0; j < lArray.length(); j++) {
                        JSONObject l = lArray.getJSONObject(j);
                        if (langs.length() > 0) langs.append(", ");
                        langs.append(l.optString("name"));
                    }
                }
            }
            String languagesStr = langs.length() > 0 ? langs.toString() : "N/A";

            // 3. Area
            double area = obj.optDouble("area", 0);

            // 4. Borders
            StringBuilder borders = new StringBuilder();
            if (obj.has("borders")) {
                JSONArray bArray = obj.optJSONArray("borders");
                if (bArray != null) {
                    for (int j = 0; j < bArray.length(); j++) {
                        if (borders.length() > 0) borders.append(", ");
                        borders.append(bArray.getString(j));
                    }
                }
            }
            String bordersStr = borders.length() > 0 ? borders.toString() : "None";

            if (!name.isEmpty()) {
                list.add(new Country(name, capital, flagUrl, population, region, 
                                     currencyStr, languagesStr, area, bordersStr));
            }
        }

        list.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
        return list;
    }

    private void filterCountries(String query) {
        List<Country> filtered = new ArrayList<>();
        for (Country c : allCountries) {
            if (c.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(c);
            }
        }
        adapter.updateList(filtered);
    }
}
