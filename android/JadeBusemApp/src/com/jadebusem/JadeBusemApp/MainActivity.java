package com.jadebusem.JadeBusemApp;

import DAO.*;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

public class MainActivity extends ListActivity {
    private ScheduleDAO datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        datasource = new ScheduleDAO(this);
        datasource.open();

        List<Schedule> scheduleList = datasource.getAllSchedules();
        ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<Schedule>(this, android.R.layout.simple_list_item_1, scheduleList);
        setListAdapter(scheduleAdapter);
    }

    /***************************************************************************************************
 *          Uncoment to test ScheduleDate
 * ************************************************************************************************/
 /*      int schedule_id = 30;

     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.main);

         datasource = new ScheduleDAO(this);
         datasource.open();

         List<ScheduleDate> values = datasource.getAllScheduleDate(schedule_id);
         ArrayAdapter<ScheduleDate> adapter = new ArrayAdapter<ScheduleDate>(this,
            android.R.layout.simple_list_item_1, values);
         setListAdapter(adapter);
     }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<ScheduleDate> adapter = (ArrayAdapter<ScheduleDate>) getListAdapter();
        ScheduleDate date = null;
        switch (view.getId()) {
            case R.id.add:
                String[] hours = new String[] { "7:30", "7:45", "7:55", "9:36" };
                String[] days = new String[] { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" };
                int nextInt = new Random().nextInt(4);
                int nextIntDay = new Random().nextInt(7);
                // save the new comment to the database
                date = datasource.createScheduleDate(schedule_id,hours[nextInt],days[nextIntDay]);
                adapter.add(date);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    date = (ScheduleDate) getListAdapter().getItem(0);
                    datasource.deleteDate(date);
                    adapter.remove(date);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

/***************************************************************************************************
*       End of test ScheduleDate
***************************************************************************************************/

/***************************************************************************************************
*          Uncoment to test ScheduleTracePoint
* *************************************************************************************************/
/*
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    datasource = new ScheduleDAO(this);
    datasource.open();

    List<ScheduleTracePoint> values = datasource.getAllScheduleTracePoint();
    ArrayAdapter<ScheduleTracePoint> adapter = new ArrayAdapter<ScheduleTracePoint>(this,
            android.R.layout.simple_list_item_1, values);
    setListAdapter(adapter);
}

public void onClick(View view) {
    @SuppressWarnings("unchecked")
    ArrayAdapter<ScheduleTracePoint> adapter = (ArrayAdapter<ScheduleTracePoint>) getListAdapter();
    ScheduleTracePoint tracePoint = null;
    switch (view.getId()) {
        case R.id.add:
            String[] places = new String[] { "Kraków", "Myślenice", "Zakopane", "Nowy Targ" };
            int nextInt = new Random().nextInt(4);
            // save the new comment to the database
            tracePoint = datasource.createScheduleTracePoint(places[nextInt]);
            adapter.add(tracePoint);
            break;
        case R.id.delete:
            if (getListAdapter().getCount() > 0) {
            tracePoint = (ScheduleTracePoint) getListAdapter().getItem(0);
            datasource.deleteTracePoint(tracePoint);
            adapter.remove(tracePoint);
            }
            break;
    }
    adapter.notifyDataSetChanged();
    }

/***************************************************************************************************
*       End of test ScheduleTracePoint
***************************************************************************************************/
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
