package daniel.mutzafi.playandlearngeora;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private List<TestStatistic> statistics;

    public StatisticsAdapter(List<TestStatistic> statistics) {
        this.statistics = statistics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestStatistic stat = statistics.get(position);
        holder.bind(stat);
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestName, tvScore, tvDate;
        View itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvDate = itemView.findViewById(R.id.tvDate);
            itemLayout = itemView.findViewById(R.id.itemLayout);
        }

        public void bind(TestStatistic stat) {
            tvTestName.setText(stat.getTestName());
            tvScore.setText(String.format("%d/%d", stat.getCorrectAnswers(), stat.getTotalQuestions()));
            tvDate.setText(stat.getDate().substring(0, 10)); // Show only date part

            // Color coding based on score
            double scorePercentage = (double) stat.getCorrectAnswers() / stat.getTotalQuestions();
            if (scorePercentage < 0.4) {
                itemLayout.setBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red
            } else if (scorePercentage < 0.7) {
                itemLayout.setBackgroundColor(Color.parseColor("#FFFDE7")); // Light Yellow
            } else {
                itemLayout.setBackgroundColor(Color.parseColor("#E8F5E9")); // Light Green
            }
        }
    }
}
