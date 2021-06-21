package ddwu.mobile.final_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UpdateNoteActivity extends AppCompatActivity {

    EditText etNewReviewTitle;
    EditText etNewReviewDate;
    EditText etNewReviewPlace;
    EditText etNewReviewContent;

    NoteDBHelper helper;

    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        helper = new NoteDBHelper(this);

        id = getIntent().getLongExtra("id", 0);

        etNewReviewTitle = findViewById(R.id.etNewNoteTitle);
        etNewReviewDate = findViewById(R.id.etNewNoteDate);
        etNewReviewPlace = findViewById(R.id.etNewNotePlace);
        etNewReviewContent = findViewById(R.id.etNewNoteContent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from " + NoteDBHelper.TABLE_NAME + " where " + NoteDBHelper.COL_ID + "=?", new String[] { String.valueOf(id) });

        while (cursor.moveToNext()) {
            etNewReviewTitle.setText( cursor.getString( cursor.getColumnIndex(NoteDBHelper.COL_TITLE) ) );
            etNewReviewDate.setText( cursor.getString( cursor.getColumnIndex(NoteDBHelper.COL_DATE) ) );
            etNewReviewPlace.setText( cursor.getString( cursor.getColumnIndex(NoteDBHelper.COL_PLACE) ) );
            etNewReviewContent.setText( cursor.getString( cursor.getColumnIndex(NoteDBHelper.COL_CONTENT) ) );
        }

        cursor.close();
        helper.close();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdateNote:
//                DB 데이터 업데이트 작업 수행
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(NoteDBHelper.COL_TITLE, etNewReviewTitle.getText().toString());
                row.put(NoteDBHelper.COL_DATE, etNewReviewDate.getText().toString());
                row.put(NoteDBHelper.COL_PLACE, etNewReviewPlace.getText().toString());
                row.put(NoteDBHelper.COL_CONTENT, etNewReviewContent.getText().toString());

                String whereClause = "_id=?";
                String[] whereArgs = new String[]{String.valueOf(id)};

                long result = db.update(NoteDBHelper.TABLE_NAME, row, whereClause, whereArgs);

                helper.close();

                setResult(RESULT_OK);

                String msg = result > 0 ? "노트 수정 성공!" : "노트 수정 실패!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnShareTwitter:
                String strLink = null;

                String content = etNewReviewTitle.getText() + "\n" +
                        etNewReviewDate.getText() + ",  " + etNewReviewPlace.getText() + "에서\n\n" +
                        etNewReviewContent.getText();

                try {
                    strLink = String.format("http://twitter.com/intent/tweet?text=%s", URLEncoder.encode(content, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strLink));
                startActivity(intent);
                break;
            case R.id.btnUpdateNoteClose:
                helper.close();

                setResult(RESULT_CANCELED);

                finish();
                break;
        }
    }

}