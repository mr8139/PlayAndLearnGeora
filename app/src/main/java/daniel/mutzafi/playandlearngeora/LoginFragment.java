package daniel.mutzafi.playandlearngeora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class LoginFragment extends Fragment {

    private MapView map;
    private GeoPoint selectedPoint;
    private Button bCheck;
    private Marker currentMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        map = view.findViewById(R.id.mapLogin);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(31.5, 34.8);
        map.getController().setCenter(startPoint);
        map.getController().setZoom(7.5);

        bCheck = view.findViewById(R.id.bCheck);

        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                selectedPoint = p;
                if (currentMarker != null) {
                    map.getOverlays().remove(currentMarker);
                }
                currentMarker = new Marker(map);
                currentMarker.setPosition(p);
                currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                currentMarker.setTitle(getString(R.string.you_selected));
                map.getOverlays().add(currentMarker);
                map.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));

        bCheck.setOnClickListener(v -> checkPoint());
        return view;
    }

    private void checkPoint() {
        if (selectedPoint == null) {
            Toast.makeText(getActivity(), R.string.select_point_first, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("GeoLogin", Context.MODE_PRIVATE);
        float savedLat = prefs.getFloat("lat", 0);
        float savedLng = prefs.getFloat("lng", 0);

        if (savedLat == 0 && savedLng == 0) {
            Toast.makeText(getActivity(), R.string.registration_not_completed, Toast.LENGTH_SHORT).show();
            return;
        }

        Location saved = new Location("saved");
        saved.setLatitude(savedLat);
        saved.setLongitude(savedLng);

        Location current = new Location("current");
        current.setLatitude(selectedPoint.getLatitude());
        current.setLongitude(selectedPoint.getLongitude());

        float distance = saved.distanceTo(current);

        if (distance <= 50000) {
            Toast.makeText(getActivity(), R.string.welcome, Toast.LENGTH_LONG).show();
            handleSuccessfulLogin();
        } else {
            Toast.makeText(getActivity(), R.string.wrong_point_try_again, Toast.LENGTH_LONG).show();
        }
    }

    private void handleSuccessfulLogin() {
        SharedPreferences settings = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String deviceType = settings.getString("device_type", "personal");

        if ("school".equals(deviceType)) {
            Intent intent = new Intent(getActivity(), StudentDetailsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), ModulesChoiceActivity.class);
            startActivity(intent);
        }
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
