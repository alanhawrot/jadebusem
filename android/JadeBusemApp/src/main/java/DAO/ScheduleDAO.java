package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.*;

public class ScheduleDAO {

	private static volatile ScheduleDAO instance = null;
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumnsSchedule = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME };
	private String[] allColumnsDate = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_SCHEDULE_ID, MySQLiteHelper.COLUMN_TIME,
			MySQLiteHelper.COLUMN_DAY };
	private String[] allColumnsTracePoint = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_SCHEDULE_ID, MySQLiteHelper.COLUMN_ADDRESS, MySQLiteHelper.COLUMN_POSITION };

	private ScheduleDAO(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public static ScheduleDAO getInstance(Context context) {
		if (instance == null) {
			synchronized (ScheduleDAO.class) {
				if (instance == null) {
					instance = new ScheduleDAO(context);
				}
			}
		}
		return instance;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	private void buildSchedulesFromCursor(Collection<Schedule> schedules,
			Cursor cursor) {
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = cursorToSchedule(cursor);
			if (schedule != null) {
				schedules.add(schedule);
			}
			cursor.moveToNext();
		}
		cursor.close();
	}

	private void buildSchedulesFromCursorAndScheduleID(
			Collection<Schedule> schedules, Cursor cursor) {
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = getScheduleByID(cursor.getLong(1));
			if (schedule != null) {
				schedules.add(schedule);
			}
			cursor.moveToNext();
		}
		cursor.close();
	}

	private ScheduleTracePoint cursorToTracePoint(Cursor cursor) {
		ScheduleTracePoint tracePoint = new ScheduleTracePoint();
		tracePoint.setId(cursor.getLong(0));
		tracePoint.setSchedule(cursor.getLong(1));
		tracePoint.setAddress(cursor.getString(2));
        tracePoint.setPosition(cursor.getInt(3));
		return tracePoint;
	}

	private ScheduleDate cursorToDate(Cursor cursor) {
		ScheduleDate date = new ScheduleDate();
		date.setId(cursor.getLong(0));
		date.setSchedule(cursor.getLong(1));
		date.setTime(cursor.getString(2));
		date.setDay(date.toEnum(cursor.getString(3)));
		return date;
	}

	private Schedule cursorToSchedule(Cursor cursor) {
		Schedule schedule = new Schedule();
		schedule.setId(cursor.getLong(0));
		schedule.setName("Local");
		schedule.setScheduleDates((ArrayList<ScheduleDate>) getAllScheduleDate(schedule
				.getId()));
		schedule.setScheduleTracePoints((ArrayList<ScheduleTracePoint>) getAllScheduleTracePoint(schedule
				.getId()));
		return schedule;
	}

	public Schedule createSchedule(String name,
			ArrayList<ScheduleTracePoint> scheduleTracePoints,
			ArrayList<ScheduleDate> scheduleDates) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		long insertID = database.insert(MySQLiteHelper.TABLE_SCHEDULE, null,
				values);
        int i = 1;
		for (ScheduleTracePoint scheduleTracePoint : scheduleTracePoints) {
			createScheduleTracePoint(insertID, scheduleTracePoint.getAddress(), i++);
		}
		for (ScheduleDate scheduleDate : scheduleDates) {
			createScheduleDate(insertID, scheduleDate.getTime(), scheduleDate
					.getDay().toString());
		}
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE,
				allColumnsSchedule,
				MySQLiteHelper.COLUMN_ID + " = " + insertID, null, null, null,
				null);
		cursor.moveToFirst();
		Schedule schedule = cursorToSchedule(cursor);
		cursor.close();
		return schedule;
	}

	public ScheduleTracePoint createScheduleTracePoint(long schedule_id,
			String address, int position) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SCHEDULE_ID, schedule_id);
		values.put(MySQLiteHelper.COLUMN_ADDRESS, address);
        values.put(MySQLiteHelper.COLUMN_POSITION, position);
		long insertId = database.insert(
				MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT, null, values);
		Cursor cursor = database.query(
				MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT,
				allColumnsTracePoint, MySQLiteHelper.COLUMN_ID + " = "
						+ insertId, null, null, null, null);
		cursor.moveToFirst();
		ScheduleTracePoint newTracePoint = cursorToTracePoint(cursor);
		cursor.close();
		return newTracePoint;
	}

	public ScheduleDate createScheduleDate(long schedule_id, String time,
			String day) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SCHEDULE_ID, schedule_id);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_DAY, day);
		long insertId = database.insert(MySQLiteHelper.TABLE_SCHEDULE_DATE,
				null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE_DATE,
				allColumnsDate, MySQLiteHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		ScheduleDate newDate = cursorToDate(cursor);
		cursor.close();
		return newDate;
	}

	public void deleteSchedule(Schedule schedule) {
		long id = schedule.getId();
		System.out.println("Removing schedule with id: " + id);
		for (ScheduleTracePoint scheduleTracePoint : schedule
				.getScheduleTracePoints()) {
			deleteTracePoint(scheduleTracePoint);
		}
		for (ScheduleDate scheduleDate : schedule.getScheduleDates()) {
			deleteDate(scheduleDate);
		}
		database.delete(MySQLiteHelper.TABLE_SCHEDULE, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public void deleteTracePoint(ScheduleTracePoint tracePoint) {
		long id = tracePoint.getId();
		System.out.println("Trace point deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public void deleteDate(ScheduleDate date) {
		long id = date.getId();
		System.out.println("Date deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_SCHEDULE_DATE,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public Schedule getScheduleByID(long scheduleID) {
		Schedule schedule = null;

		String selectionArgs[] = { String.valueOf(scheduleID) };
		String scheduleIDSelection = MySQLiteHelper.COLUMN_ID + "=?";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE,
				allColumnsSchedule, scheduleIDSelection, selectionArgs, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			schedule = cursorToSchedule(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return schedule;
	}

	public List<Schedule> getAllSchedules() {
		List<Schedule> schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(true, MySQLiteHelper.TABLE_SCHEDULE,
				allColumnsSchedule, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule schedule = cursorToSchedule(cursor);
			schedules.add(schedule);
			cursor.moveToNext();
		}
		cursor.close();

		return schedules;
	}

	public List<Schedule> getFilteredSchedules(String keywords) {
		Set<Schedule> schedules = new HashSet<Schedule>();
		String keywordsTable[] = keywords.split("\\s");

		Cursor cursorSchedule = null;
		Cursor cursorScheduleTracePoint = null;
		Cursor cursorScheduleDay = null;
		Cursor cursorScheduleTime = null;

		for (int i = 0; i < keywordsTable.length; i++) {
			String[] arg = { keywordsTable[i] };

			String scheduleNameSelection = MySQLiteHelper.COLUMN_NAME + "=?";
			String tracePointAddressSelection = MySQLiteHelper.COLUMN_ADDRESS
					+ "=?";
			String dateTimeSelection = MySQLiteHelper.COLUMN_TIME + "=?";
			String dateDaySelection = MySQLiteHelper.COLUMN_DAY + "=?";

			cursorSchedule = database.query(true,
					MySQLiteHelper.TABLE_SCHEDULE, allColumnsSchedule,
					scheduleNameSelection, arg, null, null, null, null);
			cursorScheduleTracePoint = database.query(true,
					MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT,
					allColumnsTracePoint, tracePointAddressSelection, arg,
					null, null, null, null);
			cursorScheduleDay = database.query(true,
					MySQLiteHelper.TABLE_SCHEDULE_DATE, allColumnsDate,
					dateDaySelection, arg, null, null, null, null);
			cursorScheduleTime = database.query(true,
					MySQLiteHelper.TABLE_SCHEDULE_DATE, allColumnsDate,
					dateTimeSelection, arg, null, null, null, null);

			buildSchedulesFromCursor(schedules, cursorSchedule);
			buildSchedulesFromCursorAndScheduleID(schedules,
					cursorScheduleTracePoint);
			buildSchedulesFromCursorAndScheduleID(schedules, cursorScheduleDay);
			buildSchedulesFromCursorAndScheduleID(schedules, cursorScheduleTime);
		}

		List<Schedule> schedulesList = new ArrayList<Schedule>(schedules);

		return schedulesList;
	}

	public List<ScheduleTracePoint> getAllScheduleTracePoint(long schedule_id) {
		List<ScheduleTracePoint> tracePoints = new ArrayList<ScheduleTracePoint>();

		Cursor cursor = database.query(
				MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT,
				allColumnsTracePoint, MySQLiteHelper.COLUMN_SCHEDULE_ID + "=?",
				new String[] { String.valueOf(schedule_id) }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ScheduleTracePoint tracePoint = cursorToTracePoint(cursor);
			tracePoints.add(tracePoint);
			cursor.moveToNext();
		}
		cursor.close();

		return tracePoints;
	}

	public List<ScheduleDate> getAllScheduleDate(long schedule_id) {
		List<ScheduleDate> dates = new ArrayList<ScheduleDate>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE_DATE,
				allColumnsDate, MySQLiteHelper.COLUMN_SCHEDULE_ID + "=?",
				new String[] { String.valueOf(schedule_id) }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ScheduleDate date = cursorToDate(cursor);
			dates.add(date);
			cursor.moveToNext();
		}
		cursor.close();

		return dates;
	}

}
