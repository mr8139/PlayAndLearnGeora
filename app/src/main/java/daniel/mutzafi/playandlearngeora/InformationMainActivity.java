package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InformationMainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_main);

        setupMenu(); // Inherited from BaseActivity

        View cardEarthquakes = findViewById(R.id.cardEarthquakes);
        cardEarthquakes.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(this, EarthquakeActivity.class));
            } else {
                showInternetConnectionDialog();
            }
        });

        View cardCountries = findViewById(R.id.cardCountries);
        cardCountries.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(this, CountriesActivity.class));
            } else {
                showInternetConnectionDialog();
            }
        });

        View cardDiscovery = findViewById(R.id.cardDiscovery);
        cardDiscovery.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(this, DiscoveryHistoryActivity.class));
            } else {
                showInternetConnectionDialog();
            }
        });
    }
}
