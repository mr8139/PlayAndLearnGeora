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

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Country country);
    }

    private List<Country> countries;
    private Context context;
    private OnItemClickListener listener;

    public CountriesAdapter(Context context, List<Country> countries, OnItemClickListener listener) {
        this.context = context;
        this.countries = countries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Country country = countries.get(position);
        holder.bind(country, listener);
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public void updateList(List<Country> newCountries) {
        this.countries = newCountries;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlag;
        TextView tvName, tvCapital;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlag = itemView.findViewById(R.id.ivFlag);
            tvName = itemView.findViewById(R.id.tvCountryName);
            tvCapital = itemView.findViewById(R.id.tvCapital);
        }

        public void bind(final Country country, final OnItemClickListener listener) {
            tvName.setText(country.getName());
            tvCapital.setText(country.getCapital());

            Glide.with(itemView.getContext())
                    .load(country.getFlagUrl())
                    .centerCrop()
                    .into(ivFlag);

            itemView.setOnClickListener(v -> listener.onItemClick(country));
        }
    }
}
