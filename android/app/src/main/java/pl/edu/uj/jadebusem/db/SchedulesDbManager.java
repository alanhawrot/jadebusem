package pl.edu.uj.jadebusem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uj.jadebusem.models.Departure;
import pl.edu.uj.jadebusem.models.Schedule;
import pl.edu.uj.jadebusem.models.Tracepoint;

/**
 * Created by alanhawrot on 07.02.15.
 */
public class SchedulesDbManager {

    private static volatile SchedulesDbManager instance = null;
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;

    private SchedulesDbManager(Context context) {
        dbHelper = new SchedulesDbHelper(context);
    }

    public static SchedulesDbManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SchedulesDbManager.class) {
                if (instance == null) {
                    instance = new SchedulesDbManager(context);
                }
            }
        }
        return instance;
    }

    public void openReadableDatabase() {
        db = dbHelper.getReadableDatabase();
    }

    public void openWritableDatabase() {
        db = dbHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        dbHelper.close();
    }

    public void addSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(SchedulesDbHelper.ScheduleTable.COLUMN_NAME_WEB_SCHEDULE_ID, schedule.getWeb_schedule_id());
        values.put(SchedulesDbHelper.ScheduleTable.COLUMN_NAME_AUTHOR, schedule.getAuthor());
        values.put(SchedulesDbHelper.ScheduleTable.COLUMN_NAME_COMPANY_NAME, schedule.getCompanyName());

        long id = db.insert(SchedulesDbHelper.ScheduleTable.TABLE_NAME, null, values);

        for (Tracepoint tracepoint : schedule.getTracepoints()) {
            tracepoint.setSchedule_id(id);
            addTracepoint(tracepoint);
        }

        for (Departure departure : schedule.getDepartures()) {
            departure.setSchedule_id(id);
            addDeparture(departure);
        }
    }

    public void addTracepoint(Tracepoint tracepoint) {
        ContentValues values = new ContentValues();
        values.put(SchedulesDbHelper.TracepointTable.COLUMN_NAME_NAME, tracepoint.getName());
        values.put(SchedulesDbHelper.TracepointTable.COLUMN_NAME_ORD, tracepoint.getOrd());
        values.put(SchedulesDbHelper.TracepointTable.COLUMN_NAME_SCHEDULE_ID, tracepoint.getSchedule_id());

        db.insert(SchedulesDbHelper.TracepointTable.TABLE_NAME, null, values);
    }

    public void addDeparture(Departure departure) {
        ContentValues values = new ContentValues();
        values.put(SchedulesDbHelper.DepartureTable.COLUMN_NAME_DAY, departure.getDayInt());
        values.put(SchedulesDbHelper.DepartureTable.COLUMN_NAME_HOUR, departure.getHour());
        values.put(SchedulesDbHelper.DepartureTable.COLUMN_NAME_SCHEDULE_ID, departure.getSchedule_id());

        db.insert(SchedulesDbHelper.DepartureTable.TABLE_NAME, null, values);
    }

    public void updateSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(SchedulesDbHelper.ScheduleTable.COLUMN_NAME_WEB_SCHEDULE_ID, schedule.getWeb_schedule_id());
        values.put(SchedulesDbHelper.ScheduleTable.COLUMN_NAME_AUTHOR, schedule.getAuthor());
        values.put(SchedulesDbHelper.ScheduleTable.COLUMN_NAME_COMPANY_NAME, schedule.getCompanyName());

        String selection = SchedulesDbHelper.ScheduleTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(schedule.getId()) };

        db.update(SchedulesDbHelper.ScheduleTable.TABLE_NAME, values, selection, selectionArgs);

        for (Tracepoint tracepoint : schedule.getTracepoints()) {
            deleteTracepoint(tracepoint);
            tracepoint.setSchedule_id(schedule.getId());
            addTracepoint(tracepoint);
        }

        for (Departure departure : schedule.getDepartures()) {
            deleteDeparture(departure);
            departure.setSchedule_id(schedule.getId());
            addDeparture(departure);
        }
    }

    public void updateTracepoint(Tracepoint tracepoint) {
        ContentValues values = new ContentValues();
        values.put(SchedulesDbHelper.TracepointTable.COLUMN_NAME_NAME, tracepoint.getName());
        values.put(SchedulesDbHelper.TracepointTable.COLUMN_NAME_ORD, tracepoint.getOrd());
        values.put(SchedulesDbHelper.TracepointTable.COLUMN_NAME_SCHEDULE_ID, tracepoint.getSchedule_id());

        String selection = SchedulesDbHelper.TracepointTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(tracepoint.getId()) };

        db.update(SchedulesDbHelper.TracepointTable.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateDeparture(Departure departure) {
        ContentValues values = new ContentValues();
        values.put(SchedulesDbHelper.DepartureTable.COLUMN_NAME_DAY, departure.getDayInt());
        values.put(SchedulesDbHelper.DepartureTable.COLUMN_NAME_HOUR, departure.getHour());
        values.put(SchedulesDbHelper.DepartureTable.COLUMN_NAME_SCHEDULE_ID, departure.getSchedule_id());

        String selection = SchedulesDbHelper.DepartureTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(departure.getId()) };

        db.update(SchedulesDbHelper.DepartureTable.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteSchedule(Schedule schedule) {
        for (Tracepoint tracepoint : schedule.getTracepoints()) {
            deleteTracepoint(tracepoint);
        }

        for (Departure departure : schedule.getDepartures()) {
            deleteDeparture(departure);
        }

        String selection = SchedulesDbHelper.ScheduleTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(schedule.getId()) };

        db.delete(SchedulesDbHelper.ScheduleTable.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteTracepoint(Tracepoint tracepoint) {
        String selection = SchedulesDbHelper.TracepointTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(tracepoint.getId()) };

        db.delete(SchedulesDbHelper.TracepointTable.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteDeparture(Departure departure) {
        String selection = SchedulesDbHelper.DepartureTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(departure.getId()) };

        db.delete(SchedulesDbHelper.DepartureTable.TABLE_NAME, selection, selectionArgs);
    }

    public List<Schedule> getSchedules() {
        List<Schedule> schedules = new ArrayList<>();

        Cursor cursor = db.query(SchedulesDbHelper.ScheduleTable.TABLE_NAME, null, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Schedule schedule = new Schedule();
            schedule.setId(cursor.getLong(0));
            schedule.setWeb_schedule_id(cursor.getLong(1));
            schedule.setAuthor(cursor.getString(2));
            schedule.setCompanyName(cursor.getString(3));

            schedule.setTracepoints(getTracepointsByScheduleId(schedule.getId()));
            schedule.setDepartures(getDeparturesByScheduleId(schedule.getId()));

            schedules.add(schedule);

            cursor.moveToNext();
        }
        cursor.close();

        return schedules;
    }

    public List<Tracepoint> getTracepoints() {
        List<Tracepoint> tracepoints = new ArrayList<>();

        Cursor cursor = db.query(SchedulesDbHelper.TracepointTable.TABLE_NAME, null, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tracepoint tracepoint = new Tracepoint();
            tracepoint.setId(cursor.getLong(0));
            tracepoint.setOrd(cursor.getInt(1));
            tracepoint.setName(cursor.getString(2));
            tracepoint.setSchedule_id(cursor.getLong(3));

            tracepoints.add(tracepoint);

            cursor.moveToNext();
        }
        cursor.close();

        return tracepoints;
    }

    public List<Departure> getDepartures() {
        List<Departure> departures = new ArrayList<>();

        Cursor cursor = db.query(SchedulesDbHelper.DepartureTable.TABLE_NAME, null, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Departure departure = new Departure();
            departure.setId(cursor.getLong(0));
            departure.setDayByInt(cursor.getInt(1));
            departure.setHour(cursor.getString(2));
            departure.setSchedule_id(cursor.getLong(3));

            departures.add(departure);

            cursor.moveToNext();
        }
        cursor.close();

        return departures;
    }

    public Schedule getScheduleById(long id) {
        Schedule schedule = new Schedule();

        String selection = SchedulesDbHelper.ScheduleTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(SchedulesDbHelper.ScheduleTable.TABLE_NAME, null, selection, selectionArgs, null, null, null,
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            schedule.setId(cursor.getLong(0));
            schedule.setWeb_schedule_id(cursor.getLong(1));
            schedule.setAuthor(cursor.getString(2));
            schedule.setCompanyName(cursor.getString(3));

            schedule.setTracepoints(getTracepointsByScheduleId(schedule.getId()));
            schedule.setDepartures(getDeparturesByScheduleId(schedule.getId()));
        }
        cursor.close();

        return schedule;
    }

    public Tracepoint getTracepointById(long id) {
        Tracepoint tracepoint = new Tracepoint();

        String selection = SchedulesDbHelper.TracepointTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(SchedulesDbHelper.TracepointTable.TABLE_NAME, null, selection, selectionArgs, null, null, null,
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            tracepoint.setId(cursor.getLong(0));
            tracepoint.setOrd(cursor.getInt(1));
            tracepoint.setName(cursor.getString(2));
            tracepoint.setSchedule_id(cursor.getLong(3));
        }
        cursor.close();

        return tracepoint;
    }

    public Departure getDepartureById(long id) {
        Departure departure = new Departure();

        String selection = SchedulesDbHelper.DepartureTable.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(SchedulesDbHelper.DepartureTable.TABLE_NAME, null, selection, selectionArgs, null, null, null,
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            departure.setId(cursor.getLong(0));
            departure.setDayByInt(cursor.getInt(1));
            departure.setHour(cursor.getString(2));
            departure.setSchedule_id(cursor.getLong(3));
        }
        cursor.close();

        return departure;
    }

    public List<Tracepoint> getTracepointsByScheduleId(long schedule_id) {
        List<Tracepoint> tracepoints = new ArrayList<>();

        String selection = SchedulesDbHelper.TracepointTable.COLUMN_NAME_SCHEDULE_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(schedule_id) };

        Cursor cursor = db.query(SchedulesDbHelper.TracepointTable.TABLE_NAME, null, selection, selectionArgs, null, null, null,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tracepoint tracepoint = new Tracepoint();
            tracepoint.setId(cursor.getLong(0));
            tracepoint.setOrd(cursor.getInt(1));
            tracepoint.setName(cursor.getString(2));
            tracepoint.setSchedule_id(cursor.getLong(3));

            tracepoints.add(tracepoint);

            cursor.moveToNext();
        }
        cursor.close();

        return tracepoints;
    }

    public List<Departure> getDeparturesByScheduleId(long schedule_id) {
        List<Departure> departures = new ArrayList<>();

        String selection = SchedulesDbHelper.DepartureTable.COLUMN_NAME_SCHEDULE_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(schedule_id) };

        Cursor cursor = db.query(SchedulesDbHelper.DepartureTable.TABLE_NAME, null, selection, selectionArgs, null, null, null,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Departure departure = new Departure();
            departure.setId(cursor.getLong(0));
            departure.setDayByInt(cursor.getInt(1));
            departure.setHour(cursor.getString(2));
            departure.setSchedule_id(cursor.getLong(3));

            departures.add(departure);

            cursor.moveToNext();
        }
        cursor.close();

        return departures;
    }

}
