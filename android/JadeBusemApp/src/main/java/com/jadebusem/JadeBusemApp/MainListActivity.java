package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.ScheduleDate;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import utils.ResponseTypeFromJadeBusemServer;
import utils.Result;
import utils.User;

import java.util.ArrayList;
import java.util.List;

public class MainListActivity extends ListActivity {

    private ScheduleDAO datasource;
    private ArrayList<Schedule> schedules;
    private int page;
    private ArrayAdapter<Schedule> scheduleAdapter;
    public final static String SCHEDULE_DETAILS = "com.jadebusem.JadeBusemApp.SCHEDULE_DETAILS";
    public final static String SEARCH_QUERY = "com.jadebusem.JadeBusemApp.SEARCH_QUERY";
    public final static String SCHEDULES = "com.jadebusem.JadeBusemApp.SCHEDULES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = ScheduleDAO.getInstance(this);

        page = 1;

        schedules = new ArrayList<Schedule>();
        scheduleAdapter = new ArrayAdapter<Schedule>(
                this, android.R.layout.simple_list_item_1, schedules);
        setListAdapter(scheduleAdapter);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Schedule schedule = (Schedule) getListView()
                .getItemAtPosition(position);

        Intent intent = new Intent(this, ScheduleDetailsActivity.class);
        intent.putExtra(SCHEDULE_DETAILS, schedule);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            String hint = getString(R.string.query_hint);
            searchView.setQueryHint(hint);
            searchView
                    .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            String regularExpression = "[\\w :]+";
                            if (query.matches(regularExpression)) {
                                Intent intent = new Intent(MainListActivity.this,
                                        SearchActivity.class);
                                intent.putExtra(SEARCH_QUERY, query);
                                intent.putExtra(SCHEDULES, schedules);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new Builder(
                                        MainListActivity.this);
                                builder.setTitle(R.string.search_alert_dialog_title);
                                builder.setMessage(R.string.search_alert_dialog_content);
                                builder.setPositiveButton(
                                        R.string.search_alert_dialog_positive,
                                        new OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_fetch_more_schedules) {
            page++;
            new GetSchedulesFromJadeBusemServerTask().execute(page);
        } else if (item.getItemId() == R.id.action_refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        datasource.open();
        refresh();
    }

    private void refresh() {
        List<Schedule> scheduleList = datasource.getAllSchedules();
        schedules.clear();
        schedules.addAll(scheduleList);
        scheduleAdapter.notifyDataSetChanged();
        if (User.LOGGED) {
            for (int i = 1; i <= page; i++) {
                new GetSchedulesFromJadeBusemServerTask().execute(i);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        datasource.close();
    }

    public void startAddScheduleActivity(View view) {
        Intent intent = new Intent(this, AddScheduleActivity.class);
        startActivity(intent);
    }

    class GetSchedulesFromJadeBusemServerTask extends AsyncTask<Integer, Void, List<Result>> {

        @Override
        protected List<Result> doInBackground(Integer... params) {
            try {
                int page = params[0];
                final String url = getString(R.string.url_rest_all_schedules) + page;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseTypeFromJadeBusemServer response = restTemplate.getForObject(url, ResponseTypeFromJadeBusemServer.class);
                if (response.getNext() == null && page > 0) {
                    page--;
                }
                return response.getResults();
            } catch (Exception e) {
                Log.e("MainListActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Result> list) {
            super.onPostExecute(list);
            if (list != null) {
                for (Result result : list) {
                    for (ScheduleDate scheduleDate : result.getDepartures()) {
                        String time = scheduleDate.getTime();
                        int beginTimeFormat = 0;
                        int endTimeFormat = 5;
                        time = time.substring(beginTimeFormat, endTimeFormat);
                        scheduleDate.setTime(time);
                    }
                    Schedule schedule = new Schedule(result.getId(), "Server", result.getDepartures(), result.getTrace_points());
                    if (!schedules.contains(schedule)) {
                        schedules.add(schedule);
                    }
                }
                scheduleAdapter.notifyDataSetChanged();
            } else {
                Builder builder = new Builder(MainListActivity.this);
                builder.setTitle(R.string.error_connection_dialog_title);
                builder.setMessage(R.string.error_connection_dialog_message);
                builder.setPositiveButton(R.string.error_connection_dialog_positive_button,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

    }
}