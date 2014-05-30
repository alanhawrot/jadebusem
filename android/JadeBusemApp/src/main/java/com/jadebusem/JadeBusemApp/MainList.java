package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
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

import java.util.List;

public class MainList extends ListActivity {

    private ScheduleDAO datasource;
    private List<Schedule> schedules;
    private ArrayAdapter<Schedule> scheduleAdapter;
    private int page;
    public final static String SCHEDULE_DETAILS = "com.jadebusem.JadeBusemApp.SCHEDULE_DETAILS";
    public static final String SEARCH_QUERY = "com.jadebusem.JadeBusemApp.SEARCH_QUERY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = ScheduleDAO.getInstance(this);
        datasource.open();

        page = 1;

        schedules = datasource.getAllSchedules();
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
            String hint = "Keywords separated by space";
            searchView.setQueryHint(hint);
            searchView
                    .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            String regularExpression = "[\\w :]+";
                            if (query.matches(regularExpression)) {
                                Intent intent = new Intent(MainList.this,
                                        SearchActivity.class);
                                intent.putExtra(SEARCH_QUERY, query);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new Builder(
                                        MainList.this);
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
    protected void onResume() {
        super.onResume();
        datasource.open();
        new GetSchedulesFromJadeBusemServerTask().execute(page);
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
                final String url = "http://jadebusem1.herokuapp.com/schedules/all_schedules/" + page;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseTypeFromJadeBusemServer response = restTemplate.getForObject(url, ResponseTypeFromJadeBusemServer.class);
                return response.getResults();
            } catch (Exception e) {
                Log.e("MainList", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Result> list) {
            super.onPostExecute(list);
            for (Result result : list) {
                schedules.add(new Schedule("Server", result.getDepartures(), result.getTrace_points()));
            }
            scheduleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetSchedulesFromJadeBusemServerTask().execute(page);
    }
}