package com.jadebusem.JadeBusemApp;

import java.util.List;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.ScheduleDate;
import DAO.ScheduleTracePoint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

class Mock {
	
	public static Schedule testSchedule1() {
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

	public static Schedule testSchedule2() {
		Schedule schedule = new Schedule();
		schedule.setName("Test2");
		ScheduleTracePoint scheduleTracePoint1 = new ScheduleTracePoint();
		scheduleTracePoint1.setAddress("Gdynia");
		ScheduleTracePoint scheduleTracePoint2 = new ScheduleTracePoint();
		scheduleTracePoint2.setAddress("Zakopane");
		schedule.getScheduleTracePoints().add(scheduleTracePoint1);
		schedule.getScheduleTracePoints().add(scheduleTracePoint2);
		ScheduleDate scheduleDate = new ScheduleDate();
		scheduleDate.setTime("8:30");
		scheduleDate.setDay(scheduleDate.toEnum("TUESDAY"));
		schedule.getScheduleDates().add(scheduleDate);
		return schedule;
	}

}

public class MainActivity extends ListActivity {

	private ScheduleDAO datasource;
	public final static String SCHEDULE_DETAILS = "com.jadebusem.JadeBusemApp.SCHEDULE_DETAILS";
	public final static String SCHEDULE_DAO = "com.jadebusem.JadeBusemApp.SCHEDULE_DAO";
	public static final String SEARCH_QUERY = "com.jadebusem.JadeBusemApp.SEARCH_QUERY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		datasource = ScheduleDAO.getInstance(this);
		datasource.open();

		/*
		 * Schedule schedule = Mock.testSchedule1();
		 * datasource.createSchedule(schedule.getName(),
		 * schedule.getScheduleTracePoints(), schedule.getScheduleDates());
		 * Schedule schedule2 = Mock.testSchedule2();
		 * datasource.createSchedule(schedule2.getName(),
		 * schedule2.getScheduleTracePoints(), schedule2.getScheduleDates());
		 */

		List<Schedule> scheduleList = datasource.getAllSchedules();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = null;
		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
			String hint = "Keywords separated by space";
			searchView.setQueryHint(hint);
			searchView
					.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
						@Override
						public boolean onQueryTextSubmit(String query) {
							String regularExpression = "[\\w :]+";
							if (query.matches(regularExpression)) {
								Intent intent = new Intent(MainActivity.this,
										SearchActivity.class);
								intent.putExtra(SEARCH_QUERY, query);
								startActivity(intent);
							} else {
								AlertDialog.Builder builder = new Builder(
										MainActivity.this);
								builder.setTitle(R.string.search_alert_dialog_title);
								builder.setMessage(R.string.search_alert_dialog_content);
								builder.setPositiveButton(
										R.string.search_alert_dialog_positive,
										new OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										});
								AlertDialog dialog = builder.create();
								dialog.show();
							}
							return true;
						}

						@Override
						public boolean onQueryTextChange(String newText) {
							return false;
						}
					});
		}
		return super.onCreateOptionsMenu(menu);
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

	public void startAddScheduleActivity(View view) {
		Intent intent = new Intent(this, AddScheduleActivity.class);
		startActivity(intent);
	}

}
