package DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

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
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String CREATE_TABLE_SCHEDULE = "create table "
			+ TABLE_SCHEDULE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text not null);";

	private static final String CREATE_TABLE_SCHEDULE_TRACE_POINT = "create table "
			+ TABLE_SCHEDULE_TRACE_POINT
			+ "("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_SCHEDULE_ID
			+ " integer, "
			+ COLUMN_ADDRESS
			+ " text not null, "
			+ " foreign key("
			+ COLUMN_SCHEDULE_ID
			+ ") references "
			+ TABLE_SCHEDULE + "(" + COLUMN_ID + "));";

	private static final String CREATE_TABLE_SCHEDULE_DATE = " create table "
			+ TABLE_SCHEDULE_DATE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_SCHEDULE_ID
			+ " integer, " + COLUMN_TIME + " text not null, " + COLUMN_DAY
			+ " text not null, " + " foreign key(" + COLUMN_SCHEDULE_ID
			+ ") references " + TABLE_SCHEDULE + "(" + COLUMN_ID + "));";

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