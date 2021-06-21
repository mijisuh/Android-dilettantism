package ddwu.mobile.final_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {

    ListView lvWorks = null;
    NoteDBHelper helper;
    Cursor cursor;
    NoteAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        lvWorks = (ListView)findViewById(R.id.lvNotes);

        helper = new NoteDBHelper(this);

        adapter = new NoteAdapter(this, null, R.layout.listview_note);

        lvWorks.setAdapter(adapter);

        lvWorks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                new AlertDialog.Builder(NoteActivity.this).setTitle("삭제 확인")
                        .setMessage("선택한 항목을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = helper.getWritableDatabase();

                                String whereClause = NoteDBHelper.COL_ID + "=?";
                                String[] whereArgs = new String[] { String.valueOf(id) };
                                db.delete(NoteDBHelper.TABLE_NAME, whereClause, whereArgs);

                                helper.close();

                                readAllContacts();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });

        lvWorks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteActivity.this, UpdateNoteActivity.class);
                intent.putExtra("id", id);

                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnInsertNote:
                intent = new Intent(NoteActivity.this, InsertNoteActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + NoteDBHelper.TABLE_NAME, null);

        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    private void readAllContacts() {
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + NoteDBHelper.TABLE_NAME, null);

        adapter.changeCursor(cursor);
        helper.close();
    }

}