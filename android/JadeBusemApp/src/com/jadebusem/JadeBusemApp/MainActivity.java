package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.ScheduleDate;
import DAO.ScheduleTracePoint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = new ScheduleDAO(this);
        datasource.open();

//        Schedule schedule = Mock.testSchedule();
//        datasource.createSchedule(schedule.getName(), schedule.getScheduleTracePoints(), schedule.getScheduleDates());

        List<Schedule> scheduleList = datasource.getAllSchedules();
        ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<Schedule>(this, android.R.layout.simple_list_item_1, scheduleList);
        setListAdapter(scheduleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Schedule schedule = (Schedule) getListView().getItemAtPosition(position);

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(SCHEDULE_DETAILS, schedule);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

}
