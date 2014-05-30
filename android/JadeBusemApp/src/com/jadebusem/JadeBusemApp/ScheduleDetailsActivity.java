package com.jadebusem.JadeBusemApp;

import DAO.Schedule;
import DAO.ScheduleDAO;
import DAO.days;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
		Schedule schedule = (Schedule) intent
				.getSerializableExtra(MainActivity.SCHEDULE_DETAILS);
		this.schedule = schedule;

		if (schedule != null) {
			TextView nameOfSchedule = (TextView) findViewById(R.id.detailsNameOfSchedule);

			String emptyName = "Empty name";
			String emptyRoute = "Empty route";

			nameOfSchedule.setText(schedule.getName().length() != 0 ? schedule
					.getName() : emptyName);

			TextView routeContent = (TextView) findViewById(R.id.detailsRouteContent);
			String route = schedule.toStringScheduleTracePoints();
			routeContent.setText(route.length() != 0 ? route : emptyRoute);

			setTextInDayContentTextView(schedule, days.MONDAY,
					R.id.detailsMondayContent);
			setTextInDayContentTextView(schedule, days.TUESDAY,
					R.id.detailsTuesdayContent);
			setTextInDayContentTextView(schedule, days.WEDNESDAY,
					R.id.detailsWednesdayContent);
			setTextInDayContentTextView(schedule, days.THURSDAY,
					R.id.detailsThursdayContent);
			setTextInDayContentTextView(schedule, days.FRIDAY,
					R.id.detailsFridayContent);
			setTextInDayContentTextView(schedule, days.SATURDAY,
					R.id.detailsSaturdayContent);
			setTextInDayContentTextView(schedule, days.SUNDAY,
					R.id.detailsSundayContent);
		}
	}

	private void setTextInDayContentTextView(Schedule schedule, days day,
			int viewID) {
		String none = "None";
		TextView content = (TextView) findViewById(viewID);
		String dayText = schedule.toStringScheduleDateTime(day);
		content.setText(dayText.length() != 0 ? dayText : none);
	}

	public void deleteSchedule(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.delete_alert_dialog_title);
		builder.setMessage(R.string.delete_alert_dialog_content);
		builder.setPositiveButton(R.string.delete_alert_dialog_positive,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ScheduleDAO scheduleDAO = ScheduleDAO
								.getInstance(ScheduleDetailsActivity.this);
						scheduleDAO.open();
						scheduleDAO.deleteSchedule(schedule);
						scheduleDAO.close();
						navigateUpToFromChild(ScheduleDetailsActivity.this,
								getParentActivityIntent());
					}
				});
		builder.setNegativeButton(R.string.delete_alert_dialog_negative,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void startModifyScheduleActivity(View view) {
		Intent intent = new Intent(this, ModifyScheduleActivity.class);
		intent.putExtra(SCHEDULE, schedule);
		startActivity(intent);
	}

}
