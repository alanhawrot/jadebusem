package pl.edu.uj.jadebusem;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String SCHEDULE = "SCHEDULE";
    public static final String KEY = "AIzaSyCByGygZtEMlgWlWqRUffg6CSbBZrHiRT8";

    private GoogleMap googleMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Schedule schedule = (Schedule) getActivity().getIntent().getSerializableExtra(SCHEDULE);
        List<Tracepoint> tracepoints = schedule.getTracepoints();

        String origin = null;
        String destination = null;
        try {
            origin = URLEncoder.encode(tracepoints.get(0).getName(), "UTF-8");
            destination = URLEncoder.encode(tracepoints.get(tracepoints.size() - 1).getName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            showError();
            return;
        }

        String region = "pl";

        String url = "https://maps.googleapis.com/maps/api/directions/json?key=" + KEY + "&origin=" + origin +
                "&destination=" + destination + "&region=" + region;

        StringBuilder waypointsBuilder = new StringBuilder();
        if (tracepoints.size() > 2) {
            for (int i = 1; i < tracepoints.size() - 1; i++) {
                try {
                    waypointsBuilder.append(URLEncoder.encode(tracepoints.get(i).getName(), "UTF-8")).append("|");
                } catch (UnsupportedEncodingException e) {
                    showError();
                    return;
                }
            }
            url += "&waypoints=" + waypointsBuilder.deleteCharAt(waypointsBuilder.lastIndexOf("|")).toString();
        }

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String[] params = { url };
            DrawRouteTask drawRouteTask = new DrawRouteTask(getActivity());
            drawRouteTask.execute(params);
        } else {
            showError();
        }
    }

    private void showError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.network_problem_title));
        builder.setMessage(getString(R.string.network_problem_message));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    enum DrawRouteResults {
        SUCCESS, NETWORK_PROBLEM, FAILURE
    }

    class DrawRouteTask extends AsyncTask<String, Void, DrawRouteResults> {

        private Context context;
        private String response;

        DrawRouteTask(Context context) {
            this.context = context;
        }

        @Override
        protected DrawRouteResults doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);

                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return DrawRouteResults.FAILURE;
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    response = stringBuilder.toString();
                    reader.close();
                }
            } catch (IOException e) {
                return DrawRouteResults.NETWORK_PROBLEM;
            }
            return DrawRouteResults.SUCCESS;
        }

        @Override
        protected void onPostExecute(DrawRouteResults drawRouteResults) {
            super.onPostExecute(drawRouteResults);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            switch (drawRouteResults) {
                case FAILURE:
                    builder.setTitle(context.getString(R.string.server_error_title));
                    builder.setMessage(context.getString(R.string.server_error_message));
                    builder.create().show();
                    break;
                case NETWORK_PROBLEM:
                    builder.setTitle(getString(R.string.network_problem_title));
                    builder.setMessage(getString(R.string.network_problem_message));
                    builder.create().show();
                    break;
                case SUCCESS:
                    drawRoute(response);
                    break;
                default:
                    break;
            }
        }

        private void drawRoute(String response) {
            try {
                JSONObject root = new JSONObject(response);
                JSONArray routes = root.getJSONArray("routes");
                JSONObject firstRoute = (JSONObject) routes.get(0);
                JSONObject route = firstRoute.getJSONObject("overview_polyline");
                String overview_polyline = route.getString("points");

                List<LatLng> points = PolyUtil.decode(overview_polyline);
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(points);
                googleMap.addPolyline(polylineOptions);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng point : points) {
                    builder.include(point);
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
            } catch (JSONException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setTitle(context.getString(R.string.server_error_title));
                builder.setMessage(context.getString(R.string.server_error_message));
                builder.create().show();
            }
        }
    }

}
