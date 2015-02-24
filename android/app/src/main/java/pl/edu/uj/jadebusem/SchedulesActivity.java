package pl.edu.uj.jadebusem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.edu.uj.jadebusem.db.SchedulesDbManager;
import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;
import pl.edu.uj.jadebusem.util.SchedulesAdapter;
import pl.edu.uj.jadebusem.util.UtilityClass;


public class SchedulesActivity extends ActionBarActivity {

    protected Context context;
    protected String username;
    protected SchedulesAdapter schedulesArrayAdapter;

    public static final String DOWNLOAD_SCHEDULES_URL = "http://jadebusem1.herokuapp.com/schedules/all_schedules/";
    public static final String SCHEDULE = "SCHEDULE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        setContentView(R.layout.activity_schedules);

        context = this;

        username = getIntent().getStringExtra(LoginActivity.USERNAME);

        ListView schedulesListView = (ListView) findViewById(R.id.schedulesListView);
        schedulesArrayAdapter = new SchedulesAdapter(this, android.R.layout.simple_list_item_1);
        schedulesListView.setAdapter(schedulesArrayAdapter);
        schedulesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule schedule = schedulesArrayAdapter.getItem(position);
                Intent intent = new Intent(SchedulesActivity.this, DetailsActivity.class);
                intent.putExtra(SCHEDULE, schedule);
                if (username != null) {
                    intent.putExtra(LoginActivity.USERNAME, username);
                }
                startActivity(intent);
            }
        });

        EditText searchEditText = (EditText) findViewById(R.id.searchSchedulesEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                schedulesArrayAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        UtilityClass.setupUI(this, findViewById(R.id.schedulesParent));
    }

    @Override
    protected void onStart() {
        super.onStart();
        schedulesArrayAdapter.clear(true);
        getSchedulesFromDb();
        downloadSchedules();
        clearSearchText();
    }

    protected void clearSearchText() {
        EditText searchEditText = (EditText) findViewById(R.id.searchSchedulesEditText);
        searchEditText.setText("");
    }

    protected void getSchedulesFromDb() {
        SchedulesDbManager dbManager = SchedulesDbManager.getInstance(this);
        dbManager.openReadableDatabase();
        List<Schedule> schedules = dbManager.getSchedules();
        dbManager.closeDatabase();
        schedulesArrayAdapter.addAll(schedules);
        schedulesArrayAdapter.notifyDataSetChanged();
    }

    protected void downloadSchedules() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String[] params = {};
            DownloadSchedulesTask downloadSchedulesTask = new DownloadSchedulesTask();
            downloadSchedulesTask.execute(params);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.network_problem_title));
            builder.setMessage(getString(R.string.network_problem_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    protected enum DownloadSchedulesResults {
        SUCCESS, NETWORK_PROBLEM, ERROR, JSON_ERROR
    }

    protected class DownloadSchedulesTask extends AsyncTask<String, Schedule, DownloadSchedulesResults> {

        public static final String TAG_NEXT = "next";
        public static final String TAG_RESULTS = "results";
        public static final String TAG_ID = "id";
        public static final String TAG_AUTHOR = "author";
        public static final String TAG_EMAIL = "email";
        public static final String TAG_COMPANY = "company";
        public static final String TAG_TRACE_POINTS = "trace_points";
        public static final String TAG_ADDRESS = "address";
        public static final String TAG_POSITION = "position";
        public static final String TAG_DEPARTURES = "departures";
        public static final String TAG_DAY = "day";
        public static final String TAG_TIME = "time";

        private int page;
        private boolean isNextPage;

        public DownloadSchedulesTask() {
            page = 1;
            isNextPage = true;
        }

        @Override
        protected DownloadSchedulesResults doInBackground(String... params) {
            try {
                while (isNextPage) {
                    URL url = new URL(DOWNLOAD_SCHEDULES_URL + page);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(50000);
                    httpURLConnection.setConnectTimeout(50000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoInput(true);

                    httpURLConnection.connect();

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String response;
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        response = stringBuilder.toString();
                        reader.close();

                        JSONObject root = new JSONObject(response);
                        if (root.isNull(TAG_NEXT)) {
                            isNextPage = false;
                        } else {
                            page++;
                        }

                        List<Schedule> downloadedSchedules = new ArrayList<>();
                        JSONArray results = root.getJSONArray(TAG_RESULTS);
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject jsonSchedule = results.getJSONObject(i);
                            Schedule downloadedSchedule = new Schedule();

                            downloadedSchedule.setWeb_schedule_id(jsonSchedule.getLong(TAG_ID));
                            downloadedSchedule.setAuthor(jsonSchedule.getJSONObject(TAG_AUTHOR).getString(TAG_EMAIL));
                            downloadedSchedule.setCompanyName(jsonSchedule.getString(TAG_COMPANY));

                            List<Tracepoint> downloadedTracepoints = new ArrayList<>();
                            JSONArray jsonTracepoints = jsonSchedule.getJSONArray(TAG_TRACE_POINTS);
                            for (int j = 0; j < jsonTracepoints.length(); j++) {
                                JSONObject jsonTracepoint = jsonTracepoints.getJSONObject(j);
                                Tracepoint downloadedTracepoint = new Tracepoint();

                                downloadedTracepoint.setName(jsonTracepoint.getString(TAG_ADDRESS));
                                downloadedTracepoint.setOrd(jsonTracepoint.getInt(TAG_POSITION));

                                downloadedTracepoints.add(downloadedTracepoint);
                            }
                            downloadedSchedule.setTracepoints(downloadedTracepoints);

                            List<Departure> downloadedDepartures = new ArrayList<>();
                            JSONArray jsonDepartures = jsonSchedule.getJSONArray(TAG_DEPARTURES);
                            for (int j = 0; j < jsonDepartures.length(); j++) {
                                JSONObject jsonDeparture = jsonDepartures.getJSONObject(j);
                                Departure downloadedDeparture = new Departure();

                                downloadedDeparture.setDayByInt(jsonDeparture.getInt(TAG_DAY));
                                downloadedDeparture.setHour(jsonDeparture.getString(TAG_TIME).substring(0, 5));

                                downloadedDepartures.add(downloadedDeparture);
                            }
                            downloadedSchedule.setDepartures(downloadedDepartures);

                            if (params.length > 0) {
                                if (downloadedSchedule.getAuthor().compareTo(username) == 0) {
                                    downloadedSchedules.add(downloadedSchedule);
                                }
                            } else {
                                downloadedSchedules.add(downloadedSchedule);
                            }
                        }

                        Schedule[] values = new Schedule[downloadedSchedules.size()];
                        downloadedSchedules.toArray(values);
                        publishProgress(values);
                    } else {
                        return DownloadSchedulesResults.ERROR;
                    }
                }
            } catch (IOException e) {
                return DownloadSchedulesResults.NETWORK_PROBLEM;
            } catch (JSONException e) {
                return DownloadSchedulesResults.JSON_ERROR;
            }
            return DownloadSchedulesResults.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Schedule... values) {
            super.onProgressUpdate(values);

            schedulesArrayAdapter.addAll(values);
            schedulesArrayAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(DownloadSchedulesResults downloadSchedulesResults) {
            super.onPostExecute(downloadSchedulesResults);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            switch (downloadSchedulesResults) {
                case NETWORK_PROBLEM:
                    builder.setTitle(getString(R.string.network_problem_title));
                    builder.setMessage(getString(R.string.network_problem_message));
                    builder.create().show();
                    break;
                case ERROR:
                case JSON_ERROR:
                    builder.setTitle(getString(R.string.server_error_title));
                    builder.setMessage(getString(R.string.server_error_message));
                    builder.create().show();
                    break;
                case SUCCESS:
                    Toast.makeText(context, R.string.refreshed, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedules, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_my_schedules) {
            if (username != null) {
                Intent intent = new Intent(this, MySchedulesActivity.class);
                intent.putExtra(LoginActivity.USERNAME, username);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.dialog_error_my_schedules_signed_in_title));
                builder.setMessage(getString(R.string.dialog_error_my_schedules_signed_in_message));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
            return true;
        } else if (id == R.id.action_refresh_schedules) {
            schedulesArrayAdapter.clear(true);
            getSchedulesFromDb();
            downloadSchedules();
            EditText searchEditText = (EditText) findViewById(R.id.searchSchedulesEditText);
            searchEditText.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
