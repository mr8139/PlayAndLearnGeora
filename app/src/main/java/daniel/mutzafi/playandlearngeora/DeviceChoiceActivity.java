package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DeviceChoiceActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_choice);

        Button bPersonal = findViewById(R.id.bPersonal);
        Button bSchool = findViewById(R.id.bSchool);

        bPersonal.setOnClickListener(v -> {
            saveDeviceType("personal");
            startActivity(new Intent(DeviceChoiceActivity.this, RegLogActivity.class));
            finish();
        });

        bSchool.setOnClickListener(v -> {
            saveDeviceType("school");
            startActivity(new Intent(DeviceChoiceActivity.this, RegLogActivity.class));
            finish();
        });
        
        FloatingActionButton fab = findViewById(R.id.fab_menu);
        fab.setOnClickListener(this::showMenu);
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

    private void saveDeviceType(String type) {
        SharedPreferences settings = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("device_type", type);
        editor.apply();
    }
}
