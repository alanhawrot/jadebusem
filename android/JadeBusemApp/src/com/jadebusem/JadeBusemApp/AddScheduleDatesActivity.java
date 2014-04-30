package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.ScheduleDate;

/*####################################################################################################
# AddScheduleDatesActivity()
# Main class in Activity
####################################################################################################*/
public class AddScheduleDatesActivity extends Activity {
    private ScheduleDAO datasource;
    private TabHost tabHost;
    private List<String>[] timeList = new List[7];
    private ListView[] list = new ListView[7];
    private AlertDialog alertDialog;
    private int iterator = 0;
    private Schedule schedule;

/*####################################################################################################
# onCreate()
# Inputs: savedInstanceState
# Return: None
# Method that initialize all needed stuff to set Time in new schedule
####################################################################################################*/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_dates);

        Intent iin= getIntent();
        Bundle schedule_temp = iin.getExtras();

        if(schedule_temp!=null)
        {
            schedule = (Schedule) schedule_temp.get("schedule");
        }

        /* Initialize lists Monday-Sunday */
        for(int i=0;i<7;i++)
        {
            timeList[i] = new ArrayList<String>();
        }
        list[0] = (ListView) findViewById(R.id.listMonday);
        list[1] = (ListView) findViewById(R.id.listTuesday);
        list[2] = (ListView) findViewById(R.id.listWednesday);
        list[3] = (ListView) findViewById(R.id.listThursday);
        list[4] = (ListView) findViewById(R.id.listFriday);
        list[5] = (ListView) findViewById(R.id.listSaturday);
        list[6] = (ListView) findViewById(R.id.listSunday);

        for(int i = 0;i < 7; i++)
        {
            ArrayAdapter<String> setTimeAdapterMonday = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timeList[i]);
            list[i].setAdapter(setTimeAdapterMonday);
        }

        /* Initialize onClicListener to all lists */
        init_onClickListener();

        tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        /* Initialize dialog interface */
        init_dialog();

        /* Initialize tabs interface */
        init_tabs();
    }


/*####################################################################################################
# setTimeButton()
# Inputs: view
# Return: None
# Action of setTimeButton -> show dialog with TimePicker and checkboxes
####################################################################################################*/
    public void setTimeButton(View view)
    {
        alertDialog.show();
    }

/*####################################################################################################
# saveTimetableButton()
# Inputs: View view
# Return: None
# Action of saveTimetableButton -> add schedule to database, return to MainActivity
####################################################################################################*/
    public void saveTimetableButton(View view)
    {

        for(int i=0;i<7;i++)
        {
            for(int j=0;j<timeList[i].size();j++) {
                ScheduleDate scheduleDate = new ScheduleDate();
                scheduleDate.setTime(timeList[i].get(j));
                scheduleDate.setDay(scheduleDate.toEnumFromInt(i));
                schedule.getScheduleDates().add(scheduleDate);
            }
        }

        datasource = new ScheduleDAO(this);
        datasource.open();

        datasource.createSchedule(schedule.getName(), schedule.getScheduleTracePoints(), schedule.getScheduleDates());

        datasource.close();

        Intent i=new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

/*####################################################################################################
# formatTime()
# Inputs: int hour, int minute
# Return: String - time to display
# Change format of time to 00:00
####################################################################################################*/
    private String formatTime(int hour, int minute)
    {
        String hourString = Integer.toString(hour);
        String minuteString = Integer.toString(minute);

        if(hourString.length() < 2)
        {
            hourString = '0' + hourString;
        }
        if(minuteString.length() < 2)
        {
            minuteString = '0' + minuteString;
        }

        return hourString + " : " + minuteString;
    }

/*####################################################################################################
# onCreate()
# Inputs: None
# Return: Activity
# Return address of AddScheduleDatesActivity
####################################################################################################*/
    public Activity getActivity()
    {
        return this;
    }

/*####################################################################################################
# init_onClickListener()
# Inputs: None
# Return: None
# Method that initialize onClickListener to all lists Monday-Sunday
####################################################################################################*/
    void init_onClickListener()
    {
        for(iterator = 0;iterator<7;iterator++) {
            list[iterator].setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    // Message
                    builder.setMessage("Are you sure you want delete " + timeList[tabHost.getCurrentTab()].get(position) + "?").setTitle(R.string.delete_title);

                    // Cancel Button
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /* User cancelled the dialog */
                        }
                    });

                    // OK Button
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /* User agreed to delete item */
                            timeList[tabHost.getCurrentTab()].remove(position);
                            list[tabHost.getCurrentTab()].invalidateViews();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }


/*####################################################################################################
# init_dialog()
# Inputs: None
# Return: None
# Method that initialize dialog to set time (new window with time, and days to chose)
####################################################################################################*/
    void init_dialog()
    {
        LayoutInflater adbInflater = this.getLayoutInflater();
        View checkboxLayout = adbInflater.inflate(R.layout.checkbox, null);
        final TimePicker timePicker = (TimePicker) checkboxLayout.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        final CheckBox[] checkBoxes = new CheckBox[7];

        checkBoxes[0] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxMonday);
        checkBoxes[1] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxTuesday);
        checkBoxes[2] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxWednesday);
        checkBoxes[3] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxThursday);
        checkBoxes[4] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxFriday);
        checkBoxes[5] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxSaturday);
        checkBoxes[6] = (CheckBox) checkboxLayout.findViewById(R.id.checkboxSunday);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(checkboxLayout);
        alertDialogBuilder
                // OK Button
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* User set time */
                        timePicker.clearFocus();
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();

                        // format time to 00:00
                        String time = formatTime(hour, minute);

                        // add time to all checked days, if those days didn't have this time before
                        for(iterator = 0; iterator<7;iterator++) {

                            if(checkBoxes[iterator].isChecked() == true) {

                                if (timeList[iterator].contains(time) == false) {

                                    timeList[iterator].add(time);

                                }
                            }
                        }
                        // Refresh list
                        list[tabHost.getCurrentTab()].invalidateViews();
                    }
                })
                // Cancle Button
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* User cancel setting time */
                    }
                });

        alertDialog = alertDialogBuilder.create();
    }

/*####################################################################################################
# init_tabs()
# Inputs: None
# Return: None
# Method that initialize Tabs View
####################################################################################################*/
    void init_tabs()
    {

        TabHost.TabSpec spec1=tabHost.newTabSpec("M");
        spec1.setContent(R.id.M);
        spec1.setIndicator("MO");

        TabHost.TabSpec spec2=tabHost.newTabSpec("T");
        spec2.setIndicator("TU");
        spec2.setContent(R.id.T);

        TabHost.TabSpec spec3=tabHost.newTabSpec("W");
        spec3.setIndicator("WE");
        spec3.setContent(R.id.W);

        TabHost.TabSpec spec4=tabHost.newTabSpec("Th");
        spec4.setContent(R.id.Th);
        spec4.setIndicator("TH");

        TabHost.TabSpec spec5=tabHost.newTabSpec("F");
        spec5.setIndicator("FR");
        spec5.setContent(R.id.F);

        TabHost.TabSpec spec6=tabHost.newTabSpec("Sat");
        spec6.setIndicator("SA");
        spec6.setContent(R.id.Sat);

        TabHost.TabSpec spec7=tabHost.newTabSpec("Sun");
        spec7.setIndicator("SU");
        spec7.setContent(R.id.Sun);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);
        tabHost.addTab(spec5);
        tabHost.addTab(spec6);
        tabHost.addTab(spec7);
    }
}
