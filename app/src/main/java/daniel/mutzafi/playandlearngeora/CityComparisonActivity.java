package daniel.mutzafi.playandlearngeora;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CityComparisonActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private TextView tvCity1, tvCity2;
    private CheckBox cbOption1, cbOption2, cbOption3, cbOption4;
    private Button btnSubmit, btnNext, btnHelpMap;
    private MapView mapView;
    private Random random = new Random();
    private static final int STORAGE_PERMISSION_CODE = 1;

    // List of 10 Central European capitals
    private final List<City> cities = Arrays.asList(
            new City("Berlin", 52.52, 13.41),
            new City("Vienna", 48.21, 16.37),
            new City("Prague", 50.09, 14.42),
            new City("Warsaw", 52.23, 21.01),
            new City("Budapest", 47.50, 19.04),
            new City("Bratislava", 48.15, 17.11),
            new City("Ljubljana", 46.05, 14.51),
            new City("Zagreb", 45.81, 15.98),
            new City("Bern", 46.95, 7.45),
            new City("Luxembourg", 49.61, 6.13)
    );

    private City city1, city2;
    private String northSouthCorrect, eastWestCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_city_comparison);

        initViews();
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(true);

        // Permission request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
        
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(this::showMenu);

        generateNewQuestion();
    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_back) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.menu_restart) {
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            return true;
        } else if (itemId == R.id.menu_settings) {
            // TODO: Open settings activity when created
            return true;
        } else if (itemId == R.id.menu_about) {
            startActivity(new Intent(this, AboutAppActivity.class));
            return true;
        } else if (itemId == R.id.menu_exit) {
            finishAffinity();
            return true;
        }
        return false;
    }

    private void initViews() {
        tvCity1 = findViewById(R.id.tvCity1);
        tvCity2 = findViewById(R.id.tvCity2);
        cbOption1 = findViewById(R.id.cbOption1);
        cbOption2 = findViewById(R.id.cbOption2);
        cbOption3 = findViewById(R.id.cbOption3);
        cbOption4 = findViewById(R.id.cbOption4);
        btnSubmit = findViewById(R.id.bCheck);
        btnNext = findViewById(R.id.bNext);
        btnHelpMap = findViewById(R.id.bMap);

        btnSubmit.setOnClickListener(v -> checkAnswers());
        btnNext.setOnClickListener(v -> {
            resetCheckBoxes();
            generateNewQuestion();
            mapView.setVisibility(View.GONE);
        });
        btnHelpMap.setOnClickListener(v -> {
            mapView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(this::showMap, 200);
        });
    }

    private void generateNewQuestion() {
        int index1 = random.nextInt(cities.size());
        int index2 = random.nextInt(cities.size());
        while (index2 == index1) {
            index2 = random.nextInt(cities.size());
        }
        city1 = cities.get(index1);
        city2 = cities.get(index2);

        tvCity1.setText(String.format("%s: %.2f° N, %.2f° E", city1.name, city1.lat, city1.lng));
        tvCity2.setText(String.format("%s: %.2f° N, %.2f° E", city2.name, city2.lat, city2.lng));

        String city1Name = city1.name;
        String city2Name = city2.name;
        
        northSouthCorrect = (city1.lat > city2.lat) ?
                city1Name + " " + getString(R.string.north_of) + " " + city2Name :
                city1Name + " " + getString(R.string.south_of) + " " + city2Name;
                
        eastWestCorrect = (city1.lng > city2.lng) ?
                city1Name + " " + getString(R.string.east_of) + " " + city2Name :
                city1Name + " " + getString(R.string.west_of) + " " + city2Name;

        cbOption1.setText(city1Name + " " + getString(R.string.north_of) + " " + city2Name);
        cbOption2.setText(city1Name + " " + getString(R.string.south_of) + " " + city2Name);
        cbOption3.setText(city1Name + " " + getString(R.string.east_of) + " " + city2Name);
        cbOption4.setText(city1Name + " " + getString(R.string.west_of) + " " + city2Name);
    }

    private void checkAnswers() {
        Set<String> selected = new HashSet<>();
        if (cbOption1.isChecked()) selected.add(cbOption1.getText().toString());
        if (cbOption2.isChecked()) selected.add(cbOption2.getText().toString());
        if (cbOption3.isChecked()) selected.add(cbOption3.getText().toString());
        if (cbOption4.isChecked()) selected.add(cbOption4.getText().toString());

        Set<String> correctSet = new HashSet<>();
        correctSet.add(northSouthCorrect);
        correctSet.add(eastWestCorrect);

        if (selected.equals(correctSet)) {
            Toast.makeText(this, getString(R.string.correct), Toast.LENGTH_SHORT).show();
        } else {
            String correctMsg = getString(R.string.incorrect_comparison, northSouthCorrect, eastWestCorrect);
            Toast.makeText(this, correctMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void resetCheckBoxes() {
        cbOption1.setChecked(false);
        cbOption2.setChecked(false);
        cbOption3.setChecked(false);
        cbOption4.setChecked(false);
    }

    private void showMap() {
        if (mapView == null) return;

        try {
            mapView.getOverlays().clear();

            // Coordinates
            GeoPoint point1 = new GeoPoint(city1.lat, city1.lng);
            GeoPoint point2 = new GeoPoint(city2.lat, city2.lng);

            // Calculate center and span
            double minLat = Math.min(city1.lat, city2.lat);
            double maxLat = Math.max(city1.lat, city2.lat);
            double minLng = Math.min(city1.lng, city2.lng);
            double maxLng = Math.max(city1.lng, city2.lng);

            // Padding 10% or min 0.1 deg
            double latSpan = maxLat - minLat;
            double lngSpan = maxLng - minLng;
            double latPadding = Math.max(latSpan * 0.1, 0.1);
            double lngPadding = Math.max(lngSpan * 0.1, 0.1);

            // Center
            GeoPoint center = new GeoPoint(
                    (minLat + maxLat) / 2,
                    (minLng + maxLng) / 2
            );

            // Add labels
            mapView.getOverlays().add(new CityLabelOverlay(point1, city1.name, 0xFFFF0000)); // Red for city1
            mapView.getOverlays().add(new CityLabelOverlay(point2, city2.name, 0xFF0000FF)); // Blue for city2

            // Set zoom and center
            IMapController controller = mapView.getController();
            controller.setCenter(center);
            controller.zoomToSpan(latSpan + 2 * latPadding, lngSpan + 2 * lngPadding);
            mapView.invalidate();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.map_error, e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    // Custom Overlay
    private class CityLabelOverlay extends Overlay {
        private GeoPoint point;
        private String label;
        private Paint circlePaint;
        private Paint textPaint;

        public CityLabelOverlay(GeoPoint point, String label, int color) {
            this.point = point;
            this.label = label;

            // Dot settings
            circlePaint = new Paint();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(color);
            circlePaint.setStyle(Paint.Style.FILL);

            // Text settings
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(color);
            textPaint.setTextSize(30f);
            textPaint.setTextAlign(Paint.Align.LEFT);
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            if (shadow) return;

            // Projection
            org.osmdroid.views.Projection projection = mapView.getProjection();
            android.graphics.Point screenPoint = new android.graphics.Point();
            projection.toPixels(point, screenPoint);

            // Draw circle
            canvas.drawCircle(screenPoint.x, screenPoint.y, 5f, circlePaint);

            // Draw text
            canvas.drawText(label, screenPoint.x + 10, screenPoint.y - 10, textPaint);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
