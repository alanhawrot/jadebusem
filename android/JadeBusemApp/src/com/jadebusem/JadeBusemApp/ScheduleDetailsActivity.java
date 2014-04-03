package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by alanhawrot on 19.03.14.
 */
public class ScheduleDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent intent = getIntent();
        Schedule schedule = (Schedule) intent.getSerializableExtra(MainActivity.SCHEDULE_DETAILS);

        TextView view = (TextView) findViewById(R.id.DetailsActivityTextView);

        view.setText(schedule.getName());
    }
}
