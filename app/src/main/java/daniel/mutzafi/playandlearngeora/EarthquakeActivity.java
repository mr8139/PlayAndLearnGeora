package daniel.mutzafi.playandlearngeora;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EarthquakeActivity extends BaseActivity {

    private MapView mapView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String USGS_API_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_earthquake);

        setupMenu(); // Inherited from BaseActivity

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController controller = mapView.getController();
        controller.setZoom(3.0);
        controller.setCenter(new GeoPoint(0.0, 160.0));

        ExtendedFloatingActionButton fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> loadEarthquakeData());

        loadEarthquakeData();
    }

    private void loadEarthquakeData() {
        if (!isNetworkAvailable()) {
            showInternetConnectionDialog();
            return;
        }

        Toast.makeText(this, R.string.loading_earthquakes, Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                String jsonData = fetchJson(USGS_API_URL);
                if (jsonData != null) {
                    runOnUiThread(() -> parseAndShowEarthquakes(jsonData));
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String fetchJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        return result.toString();
    }

    private void parseAndShowEarthquakes(String jsonData) {
        try {
            mapView.getOverlays().clear();

            JSONObject root = new JSONObject(jsonData);
            JSONArray features = root.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                double lng = coordinates.getDouble(0);
                double lat = coordinates.getDouble(1);
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");

                addEarthquakeMarker(lat, lng, mag, place, time);
            }

            mapView.invalidate();
            Toast.makeText(this, getString(R.string.earthquakes_loaded, features.length()), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
        }
    }

    private void addEarthquakeMarker(double lat, double lng, double mag, String place, long time) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lng));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        marker.setTitle(getString(R.string.magnitude, String.valueOf(mag)));
        marker.setSnippet(place);

        int color;
        if (mag < 4.0) {
            color = Color.parseColor("#4CAF50"); // Green
        } else if (mag < 5.5) {
            color = Color.parseColor("#FFC107"); // Yellow/Orange
        } else {
            color = Color.parseColor("#F44336"); // Red
        }

        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.setIntrinsicHeight(40);
        drawable.setIntrinsicWidth(40);
        drawable.getPaint().setColor(color);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        drawable.getPaint().setAntiAlias(true);

        marker.setIcon(drawable);

        mapView.getOverlays().add(marker);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }
}
