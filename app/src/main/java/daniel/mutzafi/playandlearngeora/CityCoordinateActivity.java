package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CityCoordinateActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private TextView tvCityName, tvShownCoordinate;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnSubmit, btnNext;
    private Random random = new Random();

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

    private City currentCity;
    private boolean showLatitude;
    private String correctOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_coordinate);
        
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(this::showMenu);

        initViews();
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
        tvCityName = findViewById(R.id.tvCityName);
        tvShownCoordinate = findViewById(R.id.tvShownCoordinate);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnSubmit = findViewById(R.id.bCheck);
        btnNext = findViewById(R.id.bNext);

        btnSubmit.setOnClickListener(v -> checkAnswer());

        btnNext.setOnClickListener(v -> {
            rgOptions.clearCheck();
            generateNewQuestion();
        });
    }

    private void generateNewQuestion() {
        currentCity = cities.get(random.nextInt(cities.size()));
        showLatitude = random.nextBoolean();

        tvCityName.setText(getString(R.string.city_label) + " " + currentCity.name);
        if (showLatitude) {
            String latStr = String.format("%.2f° %s", Math.abs(currentCity.lat), currentCity.lat >= 0 ? "N" : "S");
            tvShownCoordinate.setText(getString(R.string.latitude_label) + " " + latStr);
            generateOptionsForLongitude();
        } else {
            String lngStr = String.format("%.2f° %s", Math.abs(currentCity.lng), currentCity.lng >= 0 ? "E" : "W");
            tvShownCoordinate.setText(getString(R.string.longitude_label) + " " + lngStr);
            generateOptionsForLatitude();
        }
    }

    private void generateOptionsForLatitude() {
        double correctLat = currentCity.lat;
        String correctDir = correctLat >= 0 ? "N" : "S";
        correctOption = String.format("%.2f° %s", Math.abs(correctLat), correctDir);

        List<String> options = new ArrayList<>();
        options.add(correctOption);
        double minDiff = 30.0;
        double oppositeSign = correctLat >= 0 ? -1 : 1;
        for (int i = 0; i < 3; i++) {
            double wrongValue;
            do {
                wrongValue = oppositeSign * (minDiff + random.nextDouble() * (90 - minDiff));
            } while (Math.abs(Math.abs(wrongValue) - Math.abs(correctLat)) < minDiff);
            String wrongDir = wrongValue >= 0 ? "N" : "S";
            options.add(String.format("%.2f° %s", Math.abs(wrongValue), wrongDir));
        }

        Collections.shuffle(options);
        rbOption1.setText(options.get(0));
        rbOption2.setText(options.get(1));
        rbOption3.setText(options.get(2));
        rbOption4.setText(options.get(3));
    }

    private void generateOptionsForLongitude() {
        double correctLng = currentCity.lng;
        String correctDir = correctLng >= 0 ? "E" : "W";
        correctOption = String.format("%.2f° %s", Math.abs(correctLng), correctDir);

        List<String> options = new ArrayList<>();
        options.add(correctOption);
        double minDiff = 30.0;
        double oppositeSign = correctLng >= 0 ? -1 : 1;
        for (int i = 0; i < 3; i++) {
            double wrongValue;
            do {
                wrongValue = oppositeSign * (minDiff + random.nextDouble() * (180 - minDiff));
            } while (Math.abs(Math.abs(wrongValue) - Math.abs(correctLng)) < minDiff);
            String wrongDir = wrongValue >= 0 ? "E" : "W";
            options.add(String.format("%.2f° %s", Math.abs(wrongValue), wrongDir));
        }

        Collections.shuffle(options);
        rbOption1.setText(options.get(0));
        rbOption2.setText(options.get(1));
        rbOption3.setText(options.get(2));
        rbOption4.setText(options.get(3));
    }

    private void checkAnswer() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRb = findViewById(selectedId);
        String selected = selectedRb.getText().toString();

        if (selected.equals(correctOption)) {
            Toast.makeText(this, getString(R.string.correct), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect! Correct: " + correctOption, Toast.LENGTH_SHORT).show();
        }
    }
}
