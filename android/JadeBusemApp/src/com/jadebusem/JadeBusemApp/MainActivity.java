package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.ScheduleDate;
import DAO.ScheduleTracePoint;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

class Mock {
    public static Schedule testSchedule() {
        Schedule schedule = new Schedule();
        schedule.setName("Test");
        ScheduleTracePoint scheduleTracePoint1 = new ScheduleTracePoint();
        scheduleTracePoint1.setAddress("Kraków");
        ScheduleTracePoint scheduleTracePoint2 = new ScheduleTracePoint();
        scheduleTracePoint2.setAddress("Zakopane");
        schedule.getScheduleTracePoints().add(scheduleTracePoint1);
        schedule.getScheduleTracePoints().add(scheduleTracePoint2);
        ScheduleDate scheduleDate = new ScheduleDate();
        scheduleDate.setTime("7:30");
        scheduleDate.setDay(scheduleDate.toEnum("MONDAY"));
        schedule.getScheduleDates().add(scheduleDate);
        return schedule;
    }
}

public class MainActivity extends ListActivity {

    private ScheduleDAO datasource;
    public final static String SCHEDULE_DETAILS = "com.jadebusem.JadeBusemApp.SCHEDULE_DETAILS";
    private List<Schedule> scheduleList;
    private ListView list;
    public final static String SCHEDULE_DAO = "com.jadebusem.JadeBusemApp.SCHEDULE_DAO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = ScheduleDAO.getInstance(this);
        datasource.open();

        scheduleList = datasource.getAllSchedules();

        ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<Schedule>(this, android.R.layout.simple_list_item_1, scheduleList);
        setListAdapter(scheduleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Schedule schedule = (Schedule) getListView().getItemAtPosition(position);

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
        }
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
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
        datasource.open();
        getListView().invalidate();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public void startAddScheduleActivity(View view) {
        Intent intent = new Intent(this, AddScheduleActivity.class);
        startActivity(intent);
    }

}
