package daniel.mutzafi.playandlearngeora;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HelperDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "geologin.db";
    public static final int DATABASE_VERSION = 2;

    // School Works Table
    public static final String TABLE_SCHOOL_WORKS = "School_works";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_CLASS = "class";
    public static final String COLUMN_LOGIN_TIMESTAMP = "login_timestamp";

    private static final String SCHOOL_WORKS_TABLE_CREATE =
            "CREATE TABLE " + TABLE_SCHOOL_WORKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_CLASS + " TEXT, " +
                    COLUMN_LOGIN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    // Test Statistics Table
    public static final String TABLE_TEST_STATISTICS = "Test_Statistics";
    public static final String STATS_COLUMN_ID = "_id";
    public static final String STATS_COLUMN_SCHOOL_WORK_ID = "school_work_id";
    public static final String STATS_COLUMN_TEST_NAME = "test_name";
    public static final String STATS_COLUMN_CORRECT_ANSWERS = "correct_answers";
    public static final String STATS_COLUMN_TOTAL_QUESTIONS = "total_questions";
    public static final String STATS_COLUMN_DATE = "date";

    private static final String TEST_STATISTICS_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TEST_STATISTICS + " (" +
                    STATS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STATS_COLUMN_SCHOOL_WORK_ID + " INTEGER, " +
                    STATS_COLUMN_TEST_NAME + " TEXT, " +
                    STATS_COLUMN_CORRECT_ANSWERS + " INTEGER, " +
                    STATS_COLUMN_TOTAL_QUESTIONS + " INTEGER, " +
                    STATS_COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + STATS_COLUMN_SCHOOL_WORK_ID + ") REFERENCES " + TABLE_SCHOOL_WORKS + "(" + COLUMN_ID + ")" +
                    ");";

    public HelperDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCHOOL_WORKS_TABLE_CREATE);
        db.execSQL(TEST_STATISTICS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_STATISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHOOL_WORKS);
        onCreate(db);
    }

    public static void addTestResult(Context context, long schoolWorkId, String testName, int correctAnswers, int totalQuestions) {
        if (schoolWorkId == -1) return;
        HelperDB helperDB = new HelperDB(context);
        SQLiteDatabase db = helperDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATS_COLUMN_SCHOOL_WORK_ID, schoolWorkId);
        values.put(STATS_COLUMN_TEST_NAME, testName);
        values.put(STATS_COLUMN_CORRECT_ANSWERS, correctAnswers);
        values.put(STATS_COLUMN_TOTAL_QUESTIONS, totalQuestions);
        values.put(STATS_COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        db.insert(TABLE_TEST_STATISTICS, null, values);
        db.close();
    }

    public List<TestStatistic> getStatisticsForSession(long schoolWorkId) {
        List<TestStatistic> statistics = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TEST_STATISTICS,
                null, // all columns
                STATS_COLUMN_SCHOOL_WORK_ID + "=?",
                new String[]{String.valueOf(schoolWorkId)},
                null, null, STATS_COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String testName = cursor.getString(cursor.getColumnIndexOrThrow(STATS_COLUMN_TEST_NAME));
                int correct = cursor.getInt(cursor.getColumnIndexOrThrow(STATS_COLUMN_CORRECT_ANSWERS));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow(STATS_COLUMN_TOTAL_QUESTIONS));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(STATS_COLUMN_DATE));
                statistics.add(new TestStatistic(testName, correct, total, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return statistics;
    }
}
