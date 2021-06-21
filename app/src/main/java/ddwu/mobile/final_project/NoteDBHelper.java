package ddwu.mobile.final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDBHelper extends SQLiteOpenHelper {

    private  final String TAG = "WorkDBHelper";

    private final static String DB_NAME = "work_db";
    final static String TABLE_NAME = "work_table";
    final static String COL_ID = "_id";
    final static String COL_TITLE = "title";
    final static String COL_DATE = "date";
    final static String COL_PLACE = "place";
    final static String COL_CONTENT = "content";

    public NoteDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "create table " + TABLE_NAME + " ( " +
                COL_ID + " integer primary key autoincrement," +
                COL_TITLE +" TEXT, " +
                COL_DATE + " TEXT, " +
                COL_PLACE + " TEXT, " +
                COL_CONTENT + " TEXT);";
        Log.d(TAG, createSql);
        db.execSQL(createSql);

        db.execSQL("insert into " + TABLE_NAME + " values (null, 'Merry Chrismas', '2019-12-25', 'Dongduk univ.', 'I want to decorate a chrismas tree :)');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}