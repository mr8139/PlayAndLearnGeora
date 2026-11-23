package daniel.mutzafi.playandlearngeora;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscoveryHistoryActivity extends BaseActivity {

    private RecyclerView rvDiscovery;
    private DiscoveryAdapter adapter;
    private ProgressBar progressBar;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_history);

        setupMenu(); // Inherited from BaseActivity

        rvDiscovery = findViewById(R.id.rvDiscovery);
        progressBar = findViewById(R.id.progressBar);

        rvDiscovery.setLayoutManager(new LinearLayoutManager(this));

        loadDiscoveryData();
    }

    private void loadDiscoveryData() {
        progressBar.setVisibility(View.VISIBLE);

        List<Discovery> discoveries = new ArrayList<>();
        discoveries.add(new Discovery("Marco Polo", "1271–1295", "", ""));
        discoveries.add(new Discovery("Christopher Columbus", "1492", "", ""));
        discoveries.add(new Discovery("Vasco da Gama", "1497–1499", "", ""));
        discoveries.add(new Discovery("Ferdinand Magellan", "1519–1522", "", ""));

        adapter = new DiscoveryAdapter(this, discoveries);
        rvDiscovery.setAdapter(adapter);

        for (int i = 0; i < discoveries.size(); i++) {
            final int index = i;
            executorService.execute(() -> {
                if (!isNetworkAvailable()) {
                    runOnUiThread(() -> showInternetConnectionDialog());
                    if (index == discoveries.size() - 1) {
                        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }
                    return; 
                }
                try {
                    String explorerName = discoveries.get(index).getExplorerName();
                    String apiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + URLEncoder.encode(explorerName.replace(" ", "_"), "UTF-8");

                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(result.toString());
                    String description = json.getString("extract");
                    String imageUrl = json.getJSONObject("thumbnail").getString("source");

                    discoveries.get(index).setDescription(description);
                    discoveries.get(index).setImageUrl(imageUrl);

                    runOnUiThread(() -> adapter.notifyItemChanged(index));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (index == discoveries.size() - 1) {
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                }
            });
        }
    }
}
