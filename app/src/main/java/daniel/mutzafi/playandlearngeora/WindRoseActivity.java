package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class WindRoseActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "WindRoseActivity";
    private TextView tvResult;
    private WindRoseView windRoseView;
    private Button btnN, btnE, btnS, btnW, btnNE, btnSE, btnSW, btnNW;
    private Button btnNNE, btnENE, btnESE, btnSSE, btnSSW, btnWSW, btnWNW, btnNNW;
    private Button btnStart, btnStop;
    private Random random = new Random();
    private int currentDirection = -1;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind_rose);
        
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(this::showMenu);

        initViews();
        setupDirectionButtons();
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
        tvResult = findViewById(R.id.tv_result);
        windRoseView = findViewById(R.id.wind_rose_view);
        btnN = findViewById(R.id.btn_n);
        btnE = findViewById(R.id.btn_e);
        btnS = findViewById(R.id.btn_s);
        btnW = findViewById(R.id.btn_w);
        btnNE = findViewById(R.id.btn_ne);
        btnSE = findViewById(R.id.btn_se);
        btnSW = findViewById(R.id.btn_sw);
        btnNW = findViewById(R.id.btn_nw);
        btnNNE = findViewById(R.id.btn_nne);
        btnENE = findViewById(R.id.btn_ene);
        btnESE = findViewById(R.id.btn_ese);
        btnSSE = findViewById(R.id.btn_sse);
        btnSSW = findViewById(R.id.btn_ssw);
        btnWSW = findViewById(R.id.btn_wsw);
        btnWNW = findViewById(R.id.btn_wnw);
        btnNNW = findViewById(R.id.btn_nnw);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);

        btnStart.setOnClickListener(v -> startGame());
        btnStop.setOnClickListener(v -> stopGame());
    }

    private void setupDirectionButtons() {
        Button[] buttons = {btnN, btnNNE, btnNE, btnENE, btnE, btnESE, btnSE, btnSSE,
                btnS, btnSSW, btnSW, btnWSW, btnW, btnWNW, btnNW, btnNNW};
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnClickListener(v -> checkDirection(index));
        }
    }

    private void startGame() {
        if (!isRunning) {
            isRunning = true;
            selectNewDirection();
        }
    }

    private void stopGame() {
        isRunning = false;
        currentDirection = -1;
        windRoseView.setSelectedDirection(-1);
        tvResult.setText("");
    }

    private void selectNewDirection() {
        if (isRunning) {
            currentDirection = random.nextInt(16); // 0-15
            windRoseView.setSelectedDirection(currentDirection);
        }
    }

    private void checkDirection(int selectedIndex) {
        if (!isRunning || currentDirection == -1) return;

        String selectedDirection = windRoseView.getDirection(selectedIndex);
        String correctDirection = windRoseView.getDirection(currentDirection);

        if (selectedDirection.equals(correctDirection)) {
            tvResult.setText("Correct");
        } else {
            tvResult.setText("Error");
        }

        // Renew direction after a delay
        windRoseView.setSelectedDirection(-1);
        selectNewDirection();
    }
}
