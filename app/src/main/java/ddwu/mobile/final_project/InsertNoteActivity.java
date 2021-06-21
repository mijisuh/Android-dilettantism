package ddwu.mobile.final_project;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InsertNoteActivity extends Activity {

    EditText etNewWorkName;
    EditText etNewWorkCreator;
    EditText etNewWorkCat;
    EditText etNewWorkDesc;

    NoteDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_note);

        etNewWorkName = findViewById(R.id.etNewNoteTitle);
        etNewWorkCreator = findViewById(R.id.etNewNoteDate);
        etNewWorkCat = findViewById(R.id.etNewNotePlace);
        etNewWorkDesc = findViewById(R.id.etNewNoteContent);

        helper = new NoteDBHelper(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAddNewNote:
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(NoteDBHelper.COL_TITLE, etNewWorkName.getText().toString());
                row.put(NoteDBHelper.COL_DATE, etNewWorkCreator.getText().toString());
                row.put(NoteDBHelper.COL_PLACE, etNewWorkCat.getText().toString());
                row.put(NoteDBHelper.COL_CONTENT, etNewWorkDesc.getText().toString());

                long result = db.insert(NoteDBHelper.TABLE_NAME, null, row);

                helper.close();

                String msg = result > 0 ? "리뷰 등록 성공!" : "리뷰 등록 실패!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAddNewNoteClose:
                finish();
                break;
        }
    }

}