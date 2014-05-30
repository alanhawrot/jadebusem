package com.jadebusem.JadeBusemApp;

import java.util.List;

import DAO.Schedule;
import DAO.ScheduleDAO;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchActivity extends ListActivity {
	
	private ScheduleDAO datasource;
	public final static String SCHEDULE_DETAILS = "com.jadebusem.JadeBusemApp.SCHEDULE_DETAILS";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		datasource = ScheduleDAO.getInstance(this);
		datasource.open();
		
		Intent intent = getIntent();
		String query = intent.getStringExtra(MainActivity.SEARCH_QUERY);

		List<Schedule> scheduleList = datasource.getFilteredSchedules(query);
		
		ArrayAdapter<Schedule> scheduleAdapter = new ArrayAdapter<Schedule>(
				this, android.R.layout.simple_list_item_1, scheduleList);
		setListAdapter(scheduleAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Schedule schedule = (Schedule) getListView()
				.getItemAtPosition(position);

		Intent intent = new Intent(this, ScheduleDetailsActivity.class);
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
