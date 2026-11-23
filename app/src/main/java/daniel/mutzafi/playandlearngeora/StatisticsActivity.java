package daniel.mutzafi.playandlearngeora;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatisticsActivity extends BaseActivity {

    private RecyclerView rvStatistics;
    private TextView tvTotalTests, tvAverageScore, tvNoStats;
    private StatisticsAdapter adapter;
    private HelperDB helperDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setupMenu();

        tvTotalTests = findViewById(R.id.tvTotalTests);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvNoStats = findViewById(R.id.tvNoStats);
        rvStatistics = findViewById(R.id.rvStatistics);
        rvStatistics.setLayoutManager(new LinearLayoutManager(this));

        helperDB = new HelperDB(this);

        loadStatistics();
    }

    private void loadStatistics() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        long schoolWorkId = prefs.getLong("current_school_work_id", -1);

        if (schoolWorkId == -1) {
            tvNoStats.setVisibility(View.VISIBLE);
            return;
        }

        List<TestStatistic> stats = helperDB.getStatisticsForSession(schoolWorkId);

        if (stats.isEmpty()) {
            tvNoStats.setVisibility(View.VISIBLE);
            rvStatistics.setVisibility(View.GONE);
        } else {
            tvNoStats.setVisibility(View.GONE);
            rvStatistics.setVisibility(View.VISIBLE);

            adapter = new StatisticsAdapter(stats);
            rvStatistics.setAdapter(adapter);

            // Calculate summary
            int totalTests = stats.size();
            double totalScore = 0;
            int totalQuestions = 0;
            for (TestStatistic stat : stats) {
                totalScore += stat.getCorrectAnswers();
                totalQuestions += stat.getTotalQuestions();
            }
            double averageScore = (totalQuestions > 0) ? (totalScore / totalQuestions) * 10 : 0;

            tvTotalTests.setText(getString(R.string.total_tests_completed, totalTests));
            tvAverageScore.setText(getString(R.string.average_score, averageScore));
        }
    }
}
