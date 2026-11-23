package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameMainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static class CountryData {
        String name;
        String capital;

        CountryData(String name, String capital) {
            this.name = name;
            this.capital = capital;
        }
    }

    private TextView tvCapitalName;
    private final Button[] btnOptions = new Button[4];
    private final List<CountryData> allCountries = new ArrayList<>();
    private List<CountryData> gameSequence = new ArrayList<>();
    private int currentIndex = 0;
    private CountryData currentCorrectCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        tvCapitalName = findViewById(R.id.tv_capital_name);
        btnOptions[0] = findViewById(R.id.btn_option1);
        btnOptions[1] = findViewById(R.id.btn_option2);
        btnOptions[2] = findViewById(R.id.btn_option3);
        btnOptions[3] = findViewById(R.id.btn_option4);

        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(this::showMenu);

        loadCountriesFromAssets();
        startGame();
    }

    private void loadCountriesFromAssets() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("europe_countries.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        allCountries.add(new CountryData(parts[0].trim(), parts[1].trim()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading country list", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame() {
        if (allCountries.isEmpty()) return;
        gameSequence = new ArrayList<>(allCountries);
        Collections.shuffle(gameSequence);
        currentIndex = 0;
        showNextQuestion();
    }

    private void showNextQuestion() {
        if (currentIndex >= gameSequence.size()) {
            Toast.makeText(this, R.string.game_completed, Toast.LENGTH_LONG).show();
            startGame();
            return;
        }

        currentCorrectCountry = gameSequence.get(currentIndex);
        tvCapitalName.setText(currentCorrectCountry.capital);
        
        List<String> options = new ArrayList<>();
        options.add(currentCorrectCountry.name);
        
        List<CountryData> wrongOptionsPool = new ArrayList<>(allCountries);
        wrongOptionsPool.remove(currentCorrectCountry);
        Collections.shuffle(wrongOptionsPool);
        
        for (int i = 0; i < 3 && i < wrongOptionsPool.size(); i++) {
            options.add(wrongOptionsPool.get(i).name);
        }
        
        Collections.shuffle(options);

        for (int i = 0; i < 4; i++) {
            final String selectedName = options.get(i);
            btnOptions[i].setText(selectedName);
            btnOptions[i].setOnClickListener(v -> checkAnswer(selectedName));
        }
    }

    private void checkAnswer(String selectedName) {
        if (selectedName.equals(currentCorrectCountry.name)) {
            currentIndex++;
            showNextQuestion();
        } else {
            Toast.makeText(this, R.string.try_again, Toast.LENGTH_SHORT).show();
        }
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
            startGame();
            return true;
        } else if (itemId == R.id.menu_settings) {
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
}
