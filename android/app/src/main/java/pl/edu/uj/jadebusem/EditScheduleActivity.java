package pl.edu.uj.jadebusem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.edu.uj.jadebusem.db.SchedulesDbManager;
import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;
import pl.edu.uj.jadebusem.util.UploadScheduleTask;
import pl.edu.uj.jadebusem.util.UtilityClass;


public class EditScheduleActivity extends ActionBarActivity {

    public static final String SCHEDULE = "SCHEDULE";
    private Schedule schedule;
    private String username;
    private ArrayAdapter<Tracepoint> tracepointsArrayAdapter;
    private ListView[] departuresListViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        initTabHost();

        username = getIntent().getStringExtra(LoginActivity.USERNAME);
        schedule = (Schedule) getIntent().getSerializableExtra(SCHEDULE);

        if (schedule != null) {
            EditText editNameEditText = (EditText) findViewById(R.id.editNameEditText);
            editNameEditText.setText(schedule.getCompanyName());
        }

        ListView tracepointsListView = (ListView) findViewById(R.id.editTracepointsListView);
        tracepointsArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        if (schedule != null) {
            Collections.sort(schedule.getTracepoints(), new Comparator<Tracepoint>() {
                @Override
                public int compare(Tracepoint lhs, Tracepoint rhs) {
                    if (lhs.getOrd() < rhs.getOrd()) {
                        return -1;
                    } else if (lhs.getOrd() == rhs.getOrd()) {
                        return 0;
                    }
                    return 1;
                }
            });
            tracepointsArrayAdapter.addAll(schedule.getTracepoints());
        }
        tracepointsListView.setAdapter(tracepointsArrayAdapter);
        tracepointsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Tracepoint tracepoint = tracepointsArrayAdapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditScheduleActivity.this);
                builder.setTitle(getString(R.string.dialog_delete_title));
                builder.setMessage(getString(R.string.dialog_delete_message));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTracepoint(tracepoint);
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();

                return true;
            }
        });

        departuresListViews = new ListView[7];
        departuresListViews[0] = (ListView) findViewById(R.id.editTimeMonListView);
        departuresListViews[1] = (ListView) findViewById(R.id.editTimeTueListView);
        departuresListViews[2] = (ListView) findViewById(R.id.editTimeWedListView);
        departuresListViews[3] = (ListView) findViewById(R.id.editTimeThuListView);
        departuresListViews[4] = (ListView) findViewById(R.id.editTimeFriListView);
        departuresListViews[5] = (ListView) findViewById(R.id.editTimeSatListView);
        departuresListViews[6] = (ListView) findViewById(R.id.editTimeSunListView);

        for (int i = 0; i < departuresListViews.length; i++) {
            ArrayAdapter<Departure> departuresArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1);

            if (schedule != null) {
                Collections.sort(schedule.getDepartures(), new Comparator<Departure>() {
                    @Override
                    public int compare(Departure lhs, Departure rhs) {
                        return lhs.getHour().compareTo(rhs.getHour());
                    }
                });

                List<Departure> departures = new ArrayList<>();

                for (Departure departure : schedule.getDepartures()) {
                    if (departure.getDayInt() == i) {
                        departures.add(departure);
                    }
                }

                departuresArrayAdapter.addAll(departures);
            }

            departuresListViews[i].setAdapter(departuresArrayAdapter);

            departuresListViews[i].setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayAdapter<Departure> departureArrayAdapter = null;
                    int j = 0;
                    for (; j < 7; j++) {
                        if (departuresListViews[j] == parent) {
                            departureArrayAdapter = (ArrayAdapter<Departure>) departuresListViews[j].getAdapter();
                            break;
                        }
                    }

                    final int index = j;
                    final Departure departure = departureArrayAdapter.getItem(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditScheduleActivity.this);
                    builder.setTitle(getString(R.string.dialog_delete_title));
                    builder.setMessage(getString(R.string.dialog_delete_message));
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDeparture(index, departure);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();

                    return true;
                }
            });
        }

        UtilityClass.setupUI(this, findViewById(R.id.editParent));
    }

    private void deleteTracepoint(Tracepoint tracepoint) {
        tracepointsArrayAdapter.remove(tracepoint);
        tracepointsArrayAdapter.notifyDataSetChanged();
        SchedulesDbManager schedulesDbManager = SchedulesDbManager.getInstance(this);
        schedulesDbManager.openWritableDatabase();
        schedulesDbManager.deleteTracepoint(tracepoint);
        schedulesDbManager.closeDatabase();
    }

    private void deleteDeparture(int index, Departure departure) {
        ArrayAdapter<Departure> departureArrayAdapter = (ArrayAdapter<Departure>) departuresListViews[index].getAdapter();
        departureArrayAdapter.remove(departure);
        departureArrayAdapter.notifyDataSetChanged();
        SchedulesDbManager schedulesDbManager = SchedulesDbManager.getInstance(this);
        schedulesDbManager.openWritableDatabase();
        schedulesDbManager.deleteDeparture(departure);
        schedulesDbManager.closeDatabase();
    }

    private void initTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.editTabHost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("M");
        spec1.setContent(R.id.editMON);
        spec1.setIndicator(getString(R.string.tab_mon));

        TabHost.TabSpec spec2 = tabHost.newTabSpec("T");
        spec2.setIndicator(getString(R.string.tab_tue));
        spec2.setContent(R.id.editTUE);

        TabHost.TabSpec spec3 = tabHost.newTabSpec("W");
        spec3.setIndicator(getString(R.string.tab_wed));
        spec3.setContent(R.id.editWED);

        TabHost.TabSpec spec4 = tabHost.newTabSpec("Th");
        spec4.setContent(R.id.editTHU);
        spec4.setIndicator(getString(R.string.tab_thu));

        TabHost.TabSpec spec5 = tabHost.newTabSpec("F");
        spec5.setIndicator(getString(R.string.tab_fri));
        spec5.setContent(R.id.editFRI);

        TabHost.TabSpec spec6 = tabHost.newTabSpec("Sat");
        spec6.setIndicator(getString(R.string.tab_sat));
        spec6.setContent(R.id.editSAT);

        TabHost.TabSpec spec7 = tabHost.newTabSpec("Sun");
        spec7.setIndicator(getString(R.string.tab_sun));
        spec7.setContent(R.id.editSUN);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);
        tabHost.addTab(spec5);
        tabHost.addTab(spec6);
        tabHost.addTab(spec7);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if (schedule != null) {
                if (createScheduleObject()) {
                    if (schedule.getWeb_schedule_id() != 0) {
                        upload();
                    } else {
                        updateInDb();
                    }
                }
            } else {
                if (createScheduleObject()) {
                    addToDb();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addToDb() {
        SchedulesDbManager schedulesDbManager = SchedulesDbManager.getInstance(this);
        schedulesDbManager.openWritableDatabase();
        schedulesDbManager.addSchedule(schedule);
        schedulesDbManager.closeDatabase();
        finish();
    }

    private void updateInDb() {
        SchedulesDbManager schedulesDbManager = SchedulesDbManager.getInstance(this);
        schedulesDbManager.openWritableDatabase();
        schedulesDbManager.updateSchedule(schedule);
        schedulesDbManager.closeDatabase();

        backToDetailsActivity();
    }

    private void upload() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Schedule[] params = {schedule};
            UploadScheduleTask uploadScheduleTask = new UploadScheduleTask(this, username, schedule);
            uploadScheduleTask.execute(params);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.network_problem_title));
            builder.setMessage(getString(R.string.network_problem_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    private void backToDetailsActivity() {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(SCHEDULE, schedule);
        intent.putExtra(LoginActivity.USERNAME, username);
        startActivity(intent);
    }

    private boolean createScheduleObject() {
        EditText editNameEditText = (EditText) findViewById(R.id.editNameEditText);
        String companyName = editNameEditText.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_error_title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        if (companyName.length() > 0) {
            List<Tracepoint> tracepoints = new ArrayList<>();
            for (int i = 0; i < tracepointsArrayAdapter.getCount(); i++) {
                tracepoints.add(tracepointsArrayAdapter.getItem(i));
            }
            if (tracepoints.size() > 1) {
                List<Departure> departures = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    ArrayAdapter<Departure> departuresAdapter = (ArrayAdapter<Departure>) departuresListViews[i].getAdapter();
                    for (int j = 0; j < departuresAdapter.getCount(); j++) {
                        departures.add(departuresAdapter.getItem(j));
                    }
                }
                if (departures.size() > 0) {
                    if (schedule == null) {
                        schedule = new Schedule();
                    }
                    schedule.setAuthor(username);
                    schedule.setCompanyName(companyName);
                    schedule.setTracepoints(tracepoints);
                    schedule.setDepartures(departures);
                } else {
                    builder.setMessage(getString(R.string.dialog_error_one_departure_message));
                    builder.create().show();
                    return false;
                }
            } else {
                builder.setMessage(getString(R.string.dialog_error_two_tracepoints_message));
                builder.create().show();
                return false;
            }
        } else {
            builder.setMessage(getString(R.string.dialog_error_schedule_name_message));
            builder.create().show();
            return false;
        }
        return true;
    }

    public void addTracepointButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_tracepoint, null));
        builder.setTitle(R.string.dialog_add_tracepoint_title);
        builder.setPositiveButton(R.string.action_add_schedule_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog d = (Dialog) dialog;

                EditText dialogAddTracepointEditText = (EditText) d.findViewById(R.id.dialogAddTracepointEditText);

                Tracepoint tracepoint = new Tracepoint();
                tracepoint.setName(dialogAddTracepointEditText.getText().toString());
                tracepoint.setOrd(tracepointsArrayAdapter.getCount() + 1);

                tracepointsArrayAdapter.add(tracepoint);
                tracepointsArrayAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    public void addDepartureButtonClicked(View view) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TabHost editTabHost = (TabHost) findViewById(R.id.editTabHost);
        final int selectedTab = editTabHost.getCurrentTab();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = formatTime(hourOfDay, minute);

                Departure departure = new Departure();
                departure.setHour(time);
                departure.setDayByInt(selectedTab);

                ArrayAdapter<Departure> adapter = (ArrayAdapter<Departure>) departuresListViews[selectedTab].getAdapter();

                List<Departure> departures = new ArrayList<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    departures.add(adapter.getItem(i));
                }
                departures.add(departure);
                Collections.sort(departures, new Comparator<Departure>() {
                    @Override
                    public int compare(Departure lhs, Departure rhs) {
                        return lhs.getHour().compareTo(rhs.getHour());
                    }
                });

                adapter.clear();
                adapter.addAll(departures);
                adapter.notifyDataSetChanged();
            }
        }, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    private String formatTime(int hour, int minute) {
        String hourString = Integer.toString(hour);
        String minuteString = Integer.toString(minute);

        if (hourString.length() < 2) {
            hourString = '0' + hourString;
        }
        if (minuteString.length() < 2) {
            minuteString = '0' + minuteString;
        }

        return hourString + ":" + minuteString;
    }
}
