package daniel.mutzafi.playandlearngeora;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ModulesChoiceActivity extends BaseActivity {

    private TextView tvTitle, tvDescription;
    private RelativeLayout rootLayout;
    private Button bGo, bMyProgress;
    private CompassView compassView;
    private int currentSelection = -1;

    private final int[] bgColors = {
            Color.parseColor("#E8F5E9"),
            Color.parseColor("#FFFDE7"),
            Color.parseColor("#FFEBEE")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_choice);

        setupMenu(); // Inherited from BaseActivity

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        rootLayout = findViewById(R.id.rootLayout);
        bGo = findViewById(R.id.bGo);
        compassView = findViewById(R.id.compassView);
        bMyProgress = findViewById(R.id.bMyProgress);

        compassView.setOnSelectionChangedListener(this::updateUI);

        bGo.setOnClickListener(v -> {
            if (currentSelection != -1) {
                performTransition();
            }
        });

        bMyProgress.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            if (prefs.contains("current_school_work_id")) {
                startActivity(new Intent(this, StatisticsActivity.class));
            } else {
                Toast.makeText(this, "Only available for logged-in students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (compassView != null) {
            compassView.setScaleX(1f);
            compassView.setScaleY(1f);
            compassView.setAlpha(1f);
        }
    }

    private void updateUI(int sectorIndex) {
        if (currentSelection == sectorIndex) return;
        currentSelection = sectorIndex;

        rootLayout.setBackgroundColor(bgColors[sectorIndex]);

        switch (sectorIndex) {
            case 0:
                tvTitle.setText(R.string.module_learn_title);
                tvDescription.setText(R.string.module_learn_desc);
                break;
            case 1:
                tvTitle.setText(R.string.module_info_title);
                tvDescription.setText(R.string.module_info_desc);
                break;
            case 2:
                tvTitle.setText(R.string.module_game_title);
                tvDescription.setText(R.string.module_game_desc);
                break;
        }
    }

    private void performTransition() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(compassView, "scaleX", 1f, 5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(compassView, "scaleY", 1f, 5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(compassView, "alpha", 1f, 0f);

        scaleX.setDuration(500);
        scaleY.setDuration(500);
        alpha.setDuration(500);

        scaleX.setInterpolator(new AccelerateInterpolator());
        scaleY.setInterpolator(new AccelerateInterpolator());

        scaleX.start();
        scaleY.start();
        alpha.start();

        bGo.postDelayed(() -> {
            Intent intent = null;
            switch (currentSelection) {
                case 0:
                    intent = new Intent(ModulesChoiceActivity.this, LearnAndTestMainActivity.class);
                    break;
                case 1:
                    intent = new Intent(ModulesChoiceActivity.this, InformationMainActivity.class);
                    break;
                case 2:
                    intent = new Intent(ModulesChoiceActivity.this, GameMainActivity.class);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
        }, 400);
    }
}
