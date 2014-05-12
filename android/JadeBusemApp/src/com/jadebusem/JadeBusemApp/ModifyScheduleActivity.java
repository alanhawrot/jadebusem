package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import DAO.Schedule;
import DAO.ScheduleTracePoint;

/*####################################################################################################
# ModifyScheduleActivity()
# Main class in Activity
####################################################################################################*/
public class ModifyScheduleActivity extends Activity {
    public final static String SCHEDULE = "com.jadebusem.JadeBusemApp.SCHEDULE";
    private List<String> tracePointList;
    private ListView list;
    private Schedule schedule;

/*####################################################################################################
# onCreate()
# Inputs: Bundle savedInstanceState
# Return: None
# Method that initialize all needed stuff to modify TracePoint
####################################################################################################*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent iin= getIntent();
        Bundle schedule_temp = iin.getExtras();

        if(schedule_temp!=null)
        {
            schedule = (Schedule) schedule_temp.get(SCHEDULE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule);

        tracePointList = new ArrayList<String>();

        list = (ListView) findViewById(R.id.tracePointList);

        ArrayAdapter<String> tracePointAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tracePointList);
        list.setAdapter(tracePointAdapter);

        if(schedule != null)
        {
            ArrayList<ScheduleTracePoint> trace = schedule.getScheduleTracePoints();
            for(int i=0;i<trace.size();i++)
            {
                tracePointList.add(trace.get(i).toString());
            }
        }

        // Initialize onClickListener to list
        init_onClickListener();
    }

/*####################################################################################################
# init_onClickListener()
# Inputs: None
# Return: None
# Method that initialize onClickListener to all lists Monday-Sunday
####################################################################################################*/
    private void init_onClickListener()
    {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Show message
                builder.setMessage("Are you sure you want delete "+tracePointList.get(position)+" trace point?").setTitle(R.string.delete_title_trace_point);

                // Cancel Button
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                // OK Button
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tracePointList.remove(position);
                        list.invalidateViews();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });
    }

/*####################################################################################################
# saveTracePointButton()
# Inputs: View view
# Return: None
# Action of saveTracePointButton -> create scheduleTracePoint and start ModifyScheduleDatesActivity
####################################################################################################*/
    public void saveTracePointButton(View view) {
        if(tracePointList.size() > 1) {
            String schedule_name = tracePointList.get(0) + "->" + tracePointList.get(tracePointList.size() - 1);
            Schedule new_schedule = new Schedule();
            new_schedule.setName(schedule_name);
            for (int i = 0; i < tracePointList.size(); i++) {
                ScheduleTracePoint scheduleTracePoint = new ScheduleTracePoint();
                scheduleTracePoint.setAddress(tracePointList.get(i));
                new_schedule.getScheduleTracePoints().add(scheduleTracePoint);
            }

            Intent intent = new Intent(this, ModifyScheduleDatesActivity.class);
            intent.putExtra("schedule", schedule);
            intent.putExtra("new_schedule", new_schedule);
            startActivity(intent);
            finish();
        }
        else
        {
            TextView text = (TextView) findViewById(R.id.tracePointText);
            text.setError("You need at least two stops added");
        }
    }

/*####################################################################################################
# addTracePointButton()
# Inputs: View view
# Return: None
# Action of addTracePointButton -> new TracePoint
####################################################################################################*/
    public void addTracePointButton(View view) {
        TextView text = (TextView) findViewById(R.id.tracePointText);

        // TracePoint must have 2 characters at least
        if(text.getText().length() > 1)
        {
            // If list contain this TracePoint, than show message "Trace point already exist"
            Boolean isContain = false;
            for(int i=0;i<tracePointList.size();i++)
            {
                if(tracePointList.get(i).equalsIgnoreCase(text.getText().toString()))
                {
                    isContain = true;
                    break;
                }
            }
            if(isContain == true)
            {
                text.setError("Trace point already exist");
            }
            else {
                text.setError(null);
                tracePointList.add(text.getText().toString());
                text.setText("");
                list.invalidateViews();
            }
        }
        else {
            text.setError("Trace point must have 2 characters at least");
        }
    }

/*####################################################################################################
# getActivity()
# Inputs: None
# Return: Activity
# Return address of this activity
####################################################################################################*/
    public Activity getActivity()
    {
        return this;
    }

}

