package DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SCHEDULE = "Schedule";
    public static final String TABLE_SCHEDULE_TRACE_POINT = "ScheduleTracePoint";
    public static final String TABLE_SCHEDULE_DATE = "ScheduleDate";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SCHEDULE_ID = "schedule_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DAY = "day";

    private static final String DATABASE_NAME = "jb.db";
    private static final int DATABASE_VERSION = 4;

    // Database creation sql statement
    private static final String CREATE_TABLE_SCHEDULE = "create table "
            + TABLE_SCHEDULE + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null);";

    private static final String CREATE_TABLE_SCHEDULE_TRACE_POINT = "create table "
            + TABLE_SCHEDULE_TRACE_POINT + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SCHEDULE_ID + " integer, "
            + COLUMN_ADDRESS + " text not null, "
            + " foreign key(" + COLUMN_SCHEDULE_ID + ") references " + TABLE_SCHEDULE + "(" + COLUMN_ID + "));";

    private static final String CREATE_TABLE_SCHEDULE_DATE = " create table "
            +  TABLE_SCHEDULE_DATE + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SCHEDULE_ID + " integer, "
            + COLUMN_TIME + " text not null, "
            + COLUMN_DAY + " text not null, "
            + " foreign key(" + COLUMN_SCHEDULE_ID + ") references " + TABLE_SCHEDULE + "(" + COLUMN_ID + "));";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SCHEDULE);
        database.execSQL(CREATE_TABLE_SCHEDULE_TRACE_POINT);
        database.execSQL(CREATE_TABLE_SCHEDULE_DATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE_TRACE_POINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE_DATE);
        onCreate(db);
    }

}

public class ScheduleDAO {

    private static volatile ScheduleDAO instance = null;
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumnsSchedule = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME };
    private String[] allColumnsDate = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_SCHEDULE_ID,
            MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_DAY };
    private String[] allColumnsTracePoint = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_SCHEDULE_ID,
            MySQLiteHelper.COLUMN_ADDRESS};

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

    public Schedule createSchedule(String name, ArrayList<ScheduleTracePoint> scheduleTracePoints,
                                   ArrayList<ScheduleDate> scheduleDates) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        long insertID = database.insert(MySQLiteHelper.TABLE_SCHEDULE, null, values);
        for (ScheduleTracePoint scheduleTracePoint : scheduleTracePoints) {
            createScheduleTracePoint(insertID, scheduleTracePoint.getAddress());
        }
        for (ScheduleDate scheduleDate : scheduleDates) {
            createScheduleDate(insertID, scheduleDate.getTime(), scheduleDate.getDay().toString());
        }
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE,
                allColumnsSchedule, MySQLiteHelper.COLUMN_ID + " = " + insertID, null,
                null, null, null);
        cursor.moveToFirst();
        Schedule schedule = cursorToSchedule(cursor);
        cursor.close();
        return schedule;
    }

    public ScheduleTracePoint createScheduleTracePoint(long schedule_id, String address){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SCHEDULE_ID, schedule_id);
        values.put(MySQLiteHelper.COLUMN_ADDRESS, address);
        long insertId = database.insert(MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT, null,values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT,
                allColumnsTracePoint, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ScheduleTracePoint newTracePoint = cursorToTracePoint(cursor);
        cursor.close();
        return newTracePoint;
    }

    public ScheduleDate createScheduleDate(long schedule_id, String time, String day){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SCHEDULE_ID, schedule_id);
        values.put(MySQLiteHelper.COLUMN_TIME, time);
        values.put(MySQLiteHelper.COLUMN_DAY, day);
        long insertId = database.insert(MySQLiteHelper.TABLE_SCHEDULE_DATE, null,values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE_DATE,
                allColumnsDate, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ScheduleDate newDate = cursorToDate(cursor);
        cursor.close();
        return newDate;
    }

    public void deleteSchedule(Schedule schedule) {
        long id = schedule.getId();
        System.out.println("Removing schedule with id: " + id);
        for (ScheduleTracePoint scheduleTracePoint : schedule.getScheduleTracePoints()) {
            deleteTracePoint(scheduleTracePoint);
        }
        for (ScheduleDate scheduleDate : schedule.getScheduleDates()) {
            deleteDate(scheduleDate);
        }
        database.delete(MySQLiteHelper.TABLE_SCHEDULE, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public void deleteTracePoint(ScheduleTracePoint tracePoint) {
        long id = tracePoint.getId();
        System.out.println("Trace point deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteDate(ScheduleDate date) {
        long id = date.getId();
        System.out.println("Date deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_SCHEDULE_DATE, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<Schedule>();

        Cursor cursor = database.query(true, MySQLiteHelper.TABLE_SCHEDULE, allColumnsSchedule, null, null, null,
                null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Schedule schedule = cursorToSchedule(cursor);
            schedules.add(schedule);
            cursor.moveToNext();
        }
        cursor.close();
        return schedules;
    }

    public List<ScheduleTracePoint> getAllScheduleTracePoint(long schedule_id) {
        List<ScheduleTracePoint> tracePoints = new ArrayList<ScheduleTracePoint>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SCHEDULE_TRACE_POINT,
                allColumnsTracePoint, MySQLiteHelper.COLUMN_SCHEDULE_ID+"=?",
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
                allColumnsDate,  MySQLiteHelper.COLUMN_SCHEDULE_ID+"=?",  new String[] { String.valueOf(schedule_id) }, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ScheduleDate date = cursorToDate(cursor);
            dates.add(date);
            cursor.moveToNext();
        }
        cursor.close();
        return dates;
    }

    private ScheduleTracePoint cursorToTracePoint(Cursor cursor) {
        ScheduleTracePoint tracePoint = new ScheduleTracePoint();
        tracePoint.setId(cursor.getLong(0));
        tracePoint.setSchedule_id(cursor.getLong(1));
        tracePoint.setAddress(cursor.getString(2));
        return tracePoint;
    }

    private ScheduleDate cursorToDate(Cursor cursor) {
        ScheduleDate date = new ScheduleDate();
        date.setId(cursor.getLong(0));
        date.setSchedule_id(cursor.getLong(1));
        date.setTime(cursor.getString(2));
        date.setDay(date.toEnum(cursor.getString(3)));
        return date;
    }

    private Schedule cursorToSchedule(Cursor cursor) {
        Schedule schedule = new Schedule();
        schedule.setId(cursor.getLong(0));
        schedule.setName(cursor.getString(1));
        schedule.setScheduleDates((ArrayList<ScheduleDate>) getAllScheduleDate(schedule.getId()));
        schedule.setScheduleTracePoints((ArrayList<ScheduleTracePoint>) getAllScheduleTracePoint(schedule.getId()));
        return schedule;
    }

}
