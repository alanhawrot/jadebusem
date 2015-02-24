package pl.edu.uj.jadebusem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alanhawrot on 07.02.15.
 */
public class SchedulesDbHelper extends SQLiteOpenHelper {

    public static class ScheduleTable {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_WEB_SCHEDULE_ID = "web_schedule_id";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_COMPANY_NAME = "companyName";
    }

    public static class TracepointTable {
        public static final String TABLE_NAME = "tracepoint";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ORD = "ord";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SCHEDULE_ID = "schedule_id";
    }

    public static class DepartureTable {
        public static final String TABLE_NAME = "departure";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_SCHEDULE_ID = "schedule_id";
    }

    private static final String SQL_CREATE_TABLE_SCHEDULES = "create table "
            + ScheduleTable.TABLE_NAME
            + "("
            + ScheduleTable.COLUMN_NAME_ID
            + " integer primary key autoincrement, "
            + ScheduleTable.COLUMN_NAME_WEB_SCHEDULE_ID
            + " integer, "
            + ScheduleTable.COLUMN_NAME_AUTHOR
            + " text not null, "
            + ScheduleTable.COLUMN_NAME_COMPANY_NAME
            + " text not null);";

    private static final String SQL_CREATE_TABLE_TRACEPOINTS = "create table "
            + TracepointTable.TABLE_NAME
            + "("
            + TracepointTable.COLUMN_NAME_ID
            + " integer primary key autoincrement, "
            + TracepointTable.COLUMN_NAME_ORD
            + " integer, "
            + TracepointTable.COLUMN_NAME_NAME
            + " text not null, "
            + TracepointTable.COLUMN_NAME_SCHEDULE_ID
            + " integer, "
            + " foreign key("
            + TracepointTable.COLUMN_NAME_SCHEDULE_ID
            + ") references "
            + ScheduleTable.TABLE_NAME + "(" + ScheduleTable.COLUMN_NAME_ID + "));";

    private static final String SQL_CREATE_TABLE_DEPARTURES = "create table "
            + DepartureTable.TABLE_NAME
            + "("
            + DepartureTable.COLUMN_NAME_ID
            + " integer primary key autoincrement, "
            + DepartureTable.COLUMN_NAME_DAY
            + " integer, "
            + DepartureTable.COLUMN_NAME_HOUR
            + " text not null, "
            + DepartureTable.COLUMN_NAME_SCHEDULE_ID
            + " integer, "
            + " foreign key("
            + DepartureTable.COLUMN_NAME_SCHEDULE_ID
            + ") references "
            + ScheduleTable.TABLE_NAME + "(" + ScheduleTable.COLUMN_NAME_ID + "));";

    private static final String SQL_DELETE_TABLE_SCHEDULES =
            "DROP TABLE IF EXISTS " + ScheduleTable.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_TRACEPOINTS =
            "DROP TABLE IF EXISTS " + TracepointTable.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_DEPARTURES =
            "DROP TABLE IF EXISTS " + DepartureTable.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Schedules.db";

    public SchedulesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SCHEDULES);
        db.execSQL(SQL_CREATE_TABLE_TRACEPOINTS);
        db.execSQL(SQL_CREATE_TABLE_DEPARTURES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_DEPARTURES);
        db.execSQL(SQL_DELETE_TABLE_TRACEPOINTS);
        db.execSQL(SQL_DELETE_TABLE_SCHEDULES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }

}
