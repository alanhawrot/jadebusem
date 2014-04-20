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

public class AddScheduleActivity extends Activity {
    private List<String> tracePointList;
    private ListView list;

/*####################################################################################################
# onCreate()
# Inputs: Bundle savedInstanceState
# Return: None
# Method that initialize all needed stuff to add TracePoint
####################################################################################################*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule);

        tracePointList = new ArrayList<String>();

        list = (ListView) findViewById(R.id.tracePointList);

        ArrayAdapter<String> tracePointAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tracePointList);
        list.setAdapter(tracePointAdapter);

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
# Action of saveTracePointButton -> create scheduleTracePoint and start AddScheduleDatesActivity
####################################################################################################*/
    public void saveTracePointButton(View view) {
        String schedule_name = tracePointList.get(0)+"->"+tracePointList.get(tracePointList.size()-1);
        Schedule schedule = new Schedule();
        schedule.setName(schedule_name);
        for(int i=0;i<tracePointList.size();i++)
        {
            ScheduleTracePoint scheduleTracePoint = new ScheduleTracePoint();
            scheduleTracePoint.setAddress(tracePointList.get(i));
            schedule.getScheduleTracePoints().add(scheduleTracePoint);
        }

        Intent intent = new Intent(this, AddScheduleDatesActivity.class);
        intent.putExtra("schedule", schedule);
        startActivity(intent);
        finish();
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
