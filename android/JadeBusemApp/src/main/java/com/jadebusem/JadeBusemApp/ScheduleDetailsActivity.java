package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.ScheduleDate;
import DAO.days;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import utils.Hour;
import utils.RequestTypeForSynchronizeScheduleMethod;
import utils.User;

/**
 * Created by alanhawrot on 19.03.14.
 */
public class ScheduleDetailsActivity extends Activity {

    private Schedule schedule;
    public final static String SCHEDULE = "com.jadebusem.JadeBusemApp.SCHEDULE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent intent = getIntent();
        Schedule schedule = (Schedule) intent
                .getSerializableExtra(MainListActivity.SCHEDULE_DETAILS);
        this.schedule = schedule;

        if (schedule != null) {
            TextView nameOfSchedule = (TextView) findViewById(R.id.detailsNameOfSchedule);

            String emptyName = "Empty name";
            String emptyRoute = "Empty route";

            nameOfSchedule.setText(schedule.getName().length() != 0 ? schedule
                    .getName() : emptyName);

            TextView routeContent = (TextView) findViewById(R.id.detailsRouteContent);
            String route = schedule.toStringScheduleTracePoints();
            routeContent.setText(route.length() != 0 ? route : emptyRoute);

            setTextInDayContentTextView(schedule, days.MONDAY,
                    R.id.detailsMondayContent);
            setTextInDayContentTextView(schedule, days.TUESDAY,
                    R.id.detailsTuesdayContent);
            setTextInDayContentTextView(schedule, days.WEDNESDAY,
                    R.id.detailsWednesdayContent);
            setTextInDayContentTextView(schedule, days.THURSDAY,
                    R.id.detailsThursdayContent);
            setTextInDayContentTextView(schedule, days.FRIDAY,
                    R.id.detailsFridayContent);
            setTextInDayContentTextView(schedule, days.SATURDAY,
                    R.id.detailsSaturdayContent);
            setTextInDayContentTextView(schedule, days.SUNDAY,
                    R.id.detailsSundayContent);
        }
    }

    private void setTextInDayContentTextView(Schedule schedule, days day, int viewID) {
        String none = "None";
        TextView content = (TextView) findViewById(viewID);
        String dayText = schedule.toStringScheduleDateTime(day);
        content.setText(dayText.length() != 0 ? dayText : none);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_details_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_synchronize_schedule) {
            if (schedule.getName().compareTo("Server") == 0) {
                saveScheduleToDatabase(schedule);
            } else {
                if (User.LOGGED) {
                    new SynchronizeScheduleTask().execute();
                } else {
                    showDenyOfflineSynchronizeDialog();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class SynchronizeScheduleTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                final String url = getString(R.string.url_rest_new_schedule);
                final String companySuffix = "-UserAndroidApp";
                RequestTypeForSynchronizeScheduleMethod requestObject =
                        new RequestTypeForSynchronizeScheduleMethod(User.EMAIL, User.EMAIL + companySuffix,
                                schedule.getScheduleTracePoints());
                for (ScheduleDate date : schedule.getScheduleDates()) {
                    int index = date.toIntFromEnum(date.getDay());
                    requestObject.getDays()[index].add(new Hour(date.getTime()));
                }
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application", "json"));
                HttpEntity<RequestTypeForSynchronizeScheduleMethod> requestEntity = new HttpEntity<>(requestObject, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            } catch (Exception e) {
                Log.e("ScheduleDetailsActivity", e.getMessage(), e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                showSuccessSynchronizeDialog();
            } else {
                showErrorConnectionDialog();
            }
        }

    }

    private void saveScheduleToDatabase(Schedule schedule) {
        ScheduleDAO scheduleDAO = ScheduleDAO
                .getInstance(this);
        scheduleDAO.open();
        scheduleDAO.createSchedule(schedule.getName(), schedule.getScheduleTracePoints(), schedule.getScheduleDates());
        scheduleDAO.close();
        showSuccessSynchronizeDialog();
    }

    private void deleteScheduleFromDatabase() {
        ScheduleDAO scheduleDAO = ScheduleDAO
                .getInstance(this);
        scheduleDAO.open();
        scheduleDAO.deleteSchedule(schedule);
        scheduleDAO.close();
        Intent parentIntent = getParentActivityIntent();
        if (parentIntent != null) {
            navigateUpToFromChild(this, parentIntent);
        }
    }

    public void deleteSchedule(View view) {
        if (schedule.getName().compareTo("Server") == 0) {
            showDenyModifyOrDeleteRemoteScheduleDialog();
        } else {
            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle(R.string.delete_alert_dialog_title);
            builder.setMessage(R.string.delete_alert_dialog_content);
            builder.setPositiveButton(R.string.delete_alert_dialog_positive,
                    new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteScheduleFromDatabase();
                        }
                    });
            builder.setNegativeButton(R.string.delete_alert_dialog_negative,
                    new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void startModifyScheduleActivity(View view) {
        if (schedule.getName().compareTo("Server") == 0) {
            showDenyModifyOrDeleteRemoteScheduleDialog();
        } else {
            Intent intent = new Intent(this, ModifyScheduleActivity.class);
            intent.putExtra(SCHEDULE, schedule);
            startActivity(intent);
        }
    }

    private void showSuccessSynchronizeDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.success_synchronize_dialog_title));
        builder.setMessage(getString(R.string.success_synchronize_dialog_message));
        builder.setPositiveButton(getString(R.string.success_synchronize_dialog_positive_button),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorConnectionDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.error_connection_dialog_title));
        builder.setMessage(getString(R.string.error_connection_dialog_message));
        builder.setPositiveButton(getString(R.string.error_connection_dialog_positive_button),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDenyModifyOrDeleteRemoteScheduleDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.deny_modify_or_delete_remote_schedule_dialog_title));
        builder.setMessage(getString(R.string.deny_modify_or_delete_remote_schedule_dialog_message));
        builder.setPositiveButton(getString(R.string.deny_modify_or_delete_remote_schedule_dialog_positive_button),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDenyOfflineSynchronizeDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(getString(R.string.deny_synchronize_schedule_title));
        builder.setMessage(getString(R.string.deny_synchronize_schedule_message));
        builder.setPositiveButton(getString(R.string.deny_synchronize_schedule_positive_button),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
