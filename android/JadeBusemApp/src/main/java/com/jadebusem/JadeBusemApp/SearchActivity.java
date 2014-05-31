package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDate;
import DAO.ScheduleTracePoint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends ListActivity {

    private ArrayList<Schedule> schedules;
    private ArrayList<Schedule> scheduleList;
    private ArrayAdapter<Schedule> scheduleArrayAdapter;
    public final static String SCHEDULE_DETAILS = "com.jadebusem.JadeBusemApp.SCHEDULE_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Intent intent = getIntent();
        String query = intent.getStringExtra(MainListActivity.SEARCH_QUERY);
        schedules = (ArrayList<Schedule>) intent.getSerializableExtra(MainListActivity.SCHEDULES);

        scheduleList = new ArrayList<Schedule>();

        scheduleArrayAdapter = new ArrayAdapter<Schedule>(
                this, android.R.layout.simple_list_item_1, scheduleList);
        setListAdapter(scheduleArrayAdapter);

        new FilterSchedulesTask().execute(query);
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

    class FilterSchedulesTask extends AsyncTask<String, Void, ArrayList<Schedule>> {

        @Override
        protected ArrayList<Schedule> doInBackground(String... params) {
            String query = params[0];
            String keywords[] = query.split("\\s");
            ArrayList<Schedule> filteredSchedules = new ArrayList<>();
            for (Schedule schedule : schedules) {
                for (String keyword : keywords) {
                    if (keyword.compareTo(":") == 0) {
                        break;
                    }
                    boolean flag = false;
                    flag = filterScheduleTracePoints(schedule, keyword);
                    if (breakIfFoundedAndTryToAddToResults(filteredSchedules, schedule, flag)) break;
                    flag = filterScheduleDates(schedule, keyword);
                    if (breakIfFoundedAndTryToAddToResults(filteredSchedules, schedule, flag)) break;
                    if (filterScheduleName(filteredSchedules, schedule, keyword)) break;
                }
            }
            return filteredSchedules;
        }

        private boolean breakIfFoundedAndTryToAddToResults(ArrayList<Schedule> filteredSchedules, Schedule schedule, boolean flag) {
            if (flag) {
                if (!filteredSchedules.contains(schedule)) {
                    filteredSchedules.add(schedule);
                    return true;
                }
            }
            return false;
        }

        private boolean filterScheduleName(ArrayList<Schedule> filteredSchedules, Schedule schedule, String keyword) {
            if (schedule.getName().contains(keyword)) {
                if (!filteredSchedules.contains(schedule)) {
                    filteredSchedules.add(schedule);
                    return true;
                }
            }
            return false;
        }

        private boolean filterScheduleDates(Schedule schedule, String keyword) {
            for (ScheduleDate scheduleDate : schedule.getScheduleDates()) {
                if (scheduleDate.getTime().contains(keyword)) {
                    return true;
                }
            }
            return false;
        }

        private boolean filterScheduleTracePoints(Schedule schedule, String keyword) {
            for (ScheduleTracePoint scheduleTracePoint : schedule.getScheduleTracePoints()) {
                if (scheduleTracePoint.getAddress().contains(keyword)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(ArrayList<Schedule> filteredSchedules) {
            super.onPostExecute(filteredSchedules);
            if (filteredSchedules != null) {
                scheduleList.clear();
                scheduleList.addAll(filteredSchedules);
                scheduleArrayAdapter.notifyDataSetChanged();
            }
        }
    }

}
