package pl.edu.uj.jadebusem.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.edu.uj.jadebusem.R;
import pl.edu.uj.jadebusem.models.Schedule;

/**
 * Created by alanhawrot on 08.02.15.
 */

enum DeleteScheduleResults {
    SUCCESS, NETWORK_PROBLEM, FAILURE
}

public class DeleteScheduleTask extends AsyncTask<Void, Void, DeleteScheduleResults> {

    private Context context;
    private Schedule schedule;

    public static final String DELETE_URL = "http://jadebusem1.herokuapp.com/schedules/delete_schedule/";

    public DeleteScheduleTask(Context context, Schedule schedule) {
        this.context = context;
        this.schedule = schedule;
    }

    @Override
    protected DeleteScheduleResults doInBackground(Void... params) {
        try {
            URL url = new URL(DELETE_URL + schedule.getWeb_schedule_id());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return DeleteScheduleResults.FAILURE;
            }
        } catch (IOException e) {
            return DeleteScheduleResults.NETWORK_PROBLEM;
        }
        return DeleteScheduleResults.SUCCESS;
    }

    @Override
    protected void onPostExecute(DeleteScheduleResults deleteScheduleResults) {
        super.onPostExecute(deleteScheduleResults);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        switch (deleteScheduleResults) {
            case FAILURE:
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
                Toast.makeText(context, context.getString(R.string.toast_schedule_deleted), Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                break;
            default:
                break;
        }
    }
}
