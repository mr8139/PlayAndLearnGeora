package daniel.mutzafi.playandlearngeora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

public class RegLogActivity extends BaseActivity {

    Button bRegister, bLogin, bGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_log);

        setupMenu(); // Inherited from BaseActivity

        bRegister = findViewById(R.id.bRegister);
        bLogin = findViewById(R.id.bLogin);
        bGuest = findViewById(R.id.bGuest);

        bGuest.setOnClickListener(v -> {
            new AlertDialog.Builder(RegLogActivity.this)
                    .setTitle(R.string.confirmation)
                    .setMessage(R.string.guest_mode_confirmation)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        SharedPreferences settings = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("user_type", "guest");
                        editor.apply();

                        startActivity(new Intent(RegLogActivity.this, ModulesChoiceActivity.class));
                        finish();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });

        loadInitialFragment();

        bRegister.setOnClickListener(v -> loadFragment(new RegisterFragment()));
        bLogin.setOnClickListener(v -> loadFragment(new LoginFragment()));
    }

    private void loadInitialFragment() {
        loadFragment(new RegisterFragment());
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        if (!isNetworkAvailable()) {
            showInternetConnectionDialog();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
