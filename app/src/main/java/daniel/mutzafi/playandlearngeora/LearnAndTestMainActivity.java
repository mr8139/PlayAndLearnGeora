package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class LearnAndTestMainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_and_test_main);

        setupMenu(); // Inherited from BaseActivity

        // Set up transitions
        ImageView ivWindRose = findViewById(R.id.ivGoWindRose);
        ImageView ivQuiz = findViewById(R.id.ivQuiz);
        ImageView ivCityComparison = findViewById(R.id.ivGoCitiesCompar);
        ImageView ivCityCoordinate = findViewById(R.id.ivGoCitiesCoord);

        // No internet needed
        ivWindRose.setOnClickListener(v -> startActivity(new Intent(this, WindRoseActivity.class)));
        ivCityCoordinate.setOnClickListener(v -> startActivity(new Intent(this, CityCoordinateActivity.class)));

        // Internet needed
        ivQuiz.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(this, GeoQuizActivity.class));
            } else {
                showInternetConnectionDialog();
            }
        });

        ivCityComparison.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(this, CityComparisonActivity.class));
            } else {
                showInternetConnectionDialog();
            }
        });
    }
}
