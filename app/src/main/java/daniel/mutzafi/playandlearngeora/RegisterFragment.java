package daniel.mutzafi.playandlearngeora;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.Marker;

public class RegisterFragment extends Fragment {

    private MapView map;
    private GeoPoint selectedPoint;
    private Button bSave;
    private Marker currentMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //load/initialize the osmdroid configuration.
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        map = view.findViewById(R.id.mapRegister);
        map.setMultiTouchControls(true);

        // Set map center to Israel and zoom level
        GeoPoint startPoint = new GeoPoint(31.5, 34.8);
        map.getController().setCenter(startPoint);
        map.getController().setZoom(7.5);

        bSave = view.findViewById(R.id.bSave);

        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                selectedPoint = p;

                // Remove previous marker if exists
                if (currentMarker != null) {
                    map.getOverlays().remove(currentMarker);
                }

                // Add new marker
                currentMarker = new Marker(map);
                currentMarker.setPosition(p);
                currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(currentMarker);
                map.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));


        bSave.setOnClickListener(v -> savePoint());
        return view;
    }

    private void savePoint() {
        if (selectedPoint != null) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("GeoLogin", Context.MODE_PRIVATE);
            prefs.edit()
                    .putFloat("lat", (float) selectedPoint.getLatitude())
                    .putFloat("lng", (float) selectedPoint.getLongitude())
                    .apply();
            Toast.makeText(getActivity(), R.string.point_saved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.select_point_on_map, Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume(){
        super.onResume();
        if (map != null)
            map.onResume();
    }

    public void onPause(){
        super.onPause();
        if (map != null)
            map.onPause();
    }
}
