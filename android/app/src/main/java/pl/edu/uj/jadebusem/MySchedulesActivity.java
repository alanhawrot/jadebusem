package pl.edu.uj.jadebusem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uj.jadebusem.db.SchedulesDbManager;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.util.SchedulesAdapter;
import pl.edu.uj.jadebusem.util.UtilityClass;


public class MySchedulesActivity extends SchedulesActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        setContentView(R.layout.activity_my_schedules);

        context = this;

        username = getIntent().getStringExtra(LoginActivity.USERNAME);

        ListView mySchedulesListView = (ListView) findViewById(R.id.mySchedulesListView);
        schedulesArrayAdapter = new SchedulesAdapter(this, android.R.layout.simple_list_item_1);
        mySchedulesListView.setAdapter(schedulesArrayAdapter);

        mySchedulesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule schedule = schedulesArrayAdapter.getItem(position);
                Intent intent = new Intent(MySchedulesActivity.this, DetailsActivity.class);
                intent.putExtra(SCHEDULE, schedule);
                intent.putExtra(LoginActivity.USERNAME, username);
                startActivity(intent);
            }
        });

        EditText searchEditText = (EditText) findViewById(R.id.searchMySchedulesEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                schedulesArrayAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        UtilityClass.setupUI(this, findViewById(R.id.mySchedulesParent));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void clearSearchText() {
        EditText searchEditText = (EditText) findViewById(R.id.searchMySchedulesEditText);
        searchEditText.setText("");
    }

    @Override
    protected void getSchedulesFromDb() {
        SchedulesDbManager dbManager = SchedulesDbManager.getInstance(this);
        dbManager.openReadableDatabase();
        List<Schedule> schedules = dbManager.getSchedules();
        dbManager.closeDatabase();
        List<Schedule> helperList = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getAuthor().compareTo(username) == 0) {
                helperList.add(schedule);
            }
        }
        schedulesArrayAdapter.addAll(helperList);
        schedulesArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void downloadSchedules() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String[] params = { username };
            DownloadSchedulesTask downloadSchedulesTask = new DownloadSchedulesTask();
            downloadSchedulesTask.execute(params);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_schedules, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_schedule) {
            Intent intent = new Intent(this, EditScheduleActivity.class);
            intent.putExtra(LoginActivity.USERNAME, username);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh_my_schedules) {
            schedulesArrayAdapter.clear(true);
            getSchedulesFromDb();
            downloadSchedules();
            EditText searchEditText = (EditText) findViewById(R.id.searchMySchedulesEditText);
            searchEditText.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
