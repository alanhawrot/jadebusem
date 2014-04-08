package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.days;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
        Schedule schedule = (Schedule) intent.getSerializableExtra(MainActivity.SCHEDULE_DETAILS);
        this.schedule = schedule;

        if (schedule != null) {
            TextView nameOfSchedule = (TextView) findViewById(R.id.detailsNameOfSchedule);
            nameOfSchedule.setText(schedule.getName().length() != 0 ? schedule.getName() : "Empty name");

            TextView routeContent = (TextView) findViewById(R.id.detailsRouteContent);
            String route = schedule.toStringScheduleTracePoints();
            routeContent.setText(route.length() != 0 ? route : "Empty route");

            TextView mondayContent = (TextView) findViewById(R.id.detailsMondayContent);
            String monday = schedule.toStringScheduleDateTime(days.MONDAY);
            mondayContent.setText(monday.length() != 0 ? monday : "None");

            TextView tuesdayContent = (TextView) findViewById(R.id.detailsTuesdayContent);
            String tuesday = schedule.toStringScheduleDateTime(days.TUESDAY);
            tuesdayContent.setText(tuesday.length() != 0 ? tuesday : "None");

            TextView wednesdayContent = (TextView) findViewById(R.id.detailsWednesdayContent);
            String wednesday = schedule.toStringScheduleDateTime(days.WEDNESDAY);
            wednesdayContent.setText(wednesday.length() != 0 ? wednesday : "None");

            TextView thursdayContent = (TextView) findViewById(R.id.detailsThursdayContent);
            String thursday = schedule.toStringScheduleDateTime(days.THURSDAY);
            thursdayContent.setText(thursday.length() != 0 ? thursday : "None");

            TextView fridayContent = (TextView) findViewById(R.id.detailsFridayContent);
            String friday = schedule.toStringScheduleDateTime(days.FRIDAY);
            fridayContent.setText(friday.length() != 0 ? friday : "None");

            TextView saturdayContent = (TextView) findViewById(R.id.detailsSaturdayContent);
            String saturday = schedule.toStringScheduleDateTime(days.SATURDAY);
            saturdayContent.setText(saturday.length() != 0 ? saturday : "None");

            TextView sundayContent = (TextView) findViewById(R.id.detailsSundayContent);
            String sunday = schedule.toStringScheduleDateTime(days.SUNDAY);
            sundayContent.setText(sunday.length() != 0 ? sunday : "None");
        }
    }

    public void deleteSchedule(View view) {
        ScheduleDAO scheduleDAO = ScheduleDAO.getInstance(this);
        scheduleDAO.open();
        scheduleDAO.deleteSchedule(schedule);
        scheduleDAO.close();
        navigateUpToFromChild(this, getParentActivityIntent());
    }

    public void startModifyScheduleActivity(View view) {
        Intent intent = new Intent(this, ModifyScheduleActivity.class);
        intent.putExtra(SCHEDULE, schedule);
        startActivity(intent);
    }

}
