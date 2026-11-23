package daniel.mutzafi.playandlearngeora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryAdapter.ViewHolder> {

    private List<Discovery> discoveries;
    private Context context;

    public DiscoveryAdapter(Context context, List<Discovery> discoveries) {
        this.context = context;
        this.discoveries = discoveries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discovery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discovery discovery = discoveries.get(position);
        holder.tvExplorerName.setText(discovery.getExplorerName());
        holder.tvYear.setText(discovery.getYear());
        holder.tvDescription.setText(discovery.getDescription());

        Glide.with(context)
                .load(discovery.getImageUrl())
                .centerCrop()
                .into(holder.ivExplorer);
    }

    @Override
    public int getItemCount() {
        return discoveries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExplorer;
        TextView tvExplorerName, tvYear, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExplorer = itemView.findViewById(R.id.ivExplorer);
            tvExplorerName = itemView.findViewById(R.id.tvExplorerName);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
