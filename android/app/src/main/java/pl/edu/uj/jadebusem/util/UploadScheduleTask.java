package pl.edu.uj.jadebusem.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.edu.uj.jadebusem.DetailsActivity;
import pl.edu.uj.jadebusem.LoginActivity;
import pl.edu.uj.jadebusem.R;
import pl.edu.uj.jadebusem.db.SchedulesDbManager;
import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;

/**
 * Created by alanhawrot on 08.02.15.
 */

enum UploadScheduleResults {
    SUCCESS, NETWORK_PROBLEM, FAILURE, JSON_ERROR
}

public class UploadScheduleTask extends AsyncTask<Schedule, Void, UploadScheduleResults> {

    private Context context;
    private String username;
    private Schedule _schedule;

    public static final String SCHEDULE = "SCHEDULE";
    public static final String UPLOAD_URL = "http://jadebusem1.herokuapp.com/schedules/schedule/";
    public static final String TAG_SCHEDULE_ID = "schedule_id";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_COMPANY_NAME = "company_name";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_ID = "id";
    public static final String TAG_TRACE_POINTS = "trace_points";
    public static final String TAG_HOUR = "hour";
    public static final String TAG_DAYS = "days";

    public UploadScheduleTask(Context context, String username, Schedule schedule) {
        this.context = context;
        this.username = username;
        this._schedule = schedule;
    }

    @Override
    protected UploadScheduleResults doInBackground(Schedule... params) {
        for (Schedule schedule : params) {
            try {
                URL url = new URL(UPLOAD_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonSchedule = new JSONObject();

                if (schedule.getWeb_schedule_id() != 0) {
                    jsonSchedule.put(TAG_SCHEDULE_ID, schedule.getWeb_schedule_id());
                } else {
                    jsonSchedule.put(TAG_SCHEDULE_ID, JSONObject.NULL);
                }

                jsonSchedule.put(TAG_EMAIL, schedule.getAuthor());
                jsonSchedule.put(TAG_COMPANY_NAME, schedule.getCompanyName());

                JSONArray jsonTracepointsArray = new JSONArray();
                for (Tracepoint tracepoint : schedule.getTracepoints()) {
                    JSONObject jsonTracepoint = new JSONObject();
                    jsonTracepoint.put(TAG_ADDRESS, tracepoint.getName());
                    jsonTracepoint.put(TAG_ID, tracepoint.getOrd());
                    jsonTracepointsArray.put(jsonTracepoint);
                }
                jsonSchedule.put(TAG_TRACE_POINTS, jsonTracepointsArray);

                JSONArray jsonDeparturesRootArray = new JSONArray();
                for (int i = 0; i < 7; i++) {
                    JSONArray jsonDeparturesArray = new JSONArray();
                    for (Departure departure : schedule.getDepartures()) {
                        if (departure.getDayInt() == i) {
                            JSONObject jsonDeparture = new JSONObject();
                            jsonDeparture.put(TAG_HOUR, departure.getHour());
                            jsonDeparturesArray.put(jsonDeparture);
                        }
                    }
                    jsonDeparturesRootArray.put(jsonDeparturesArray);
                }
                jsonSchedule.put(TAG_DAYS, jsonDeparturesRootArray);

                String body = jsonSchedule.toString();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                outputStream.close();

                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return UploadScheduleResults.FAILURE;
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String response;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    response = stringBuilder.toString();
                    reader.close();

                    JSONObject responseId = new JSONObject(response);
                    _schedule.setWeb_schedule_id(responseId.getLong("schedule_id"));
                }
            } catch (IOException e) {
                return UploadScheduleResults.NETWORK_PROBLEM;
            } catch (JSONException e) {
                return UploadScheduleResults.JSON_ERROR;
            }
        }
        return UploadScheduleResults.SUCCESS;
    }

    @Override
    protected void onPostExecute(UploadScheduleResults uploadScheduleResults) {
        super.onPostExecute(uploadScheduleResults);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        switch (uploadScheduleResults) {
            case FAILURE:
            case JSON_ERROR:
                builder.setTitle(context.getString(R.string.server_error_title));
                builder.setMessage(context.getString(R.string.server_error_message));
                builder.create().show();
                break;
            case NETWORK_PROBLEM:
                builder.setTitle(context.getString(R.string.network_problem_title));
                builder.setMessage(context.getString(R.string.network_problem_message));
                builder.create().show();
                break;
            case SUCCESS:
                Toast.makeText(context, R.string.uploaded, Toast.LENGTH_SHORT).show();
                deleteScheduleFromDb();
                backToDetailsActivity();
                break;
            default:
                break;
        }
    }

    private void backToDetailsActivity() {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(SCHEDULE, _schedule);
        intent.putExtra(LoginActivity.USERNAME, username);
        context.startActivity(intent);
    }

    private void deleteScheduleFromDb() {
        SchedulesDbManager schedulesDbManager = SchedulesDbManager.getInstance(context);
        schedulesDbManager.openWritableDatabase();
        schedulesDbManager.deleteSchedule(_schedule);
        schedulesDbManager.closeDatabase();
    }

}
