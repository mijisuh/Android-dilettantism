package ddwu.mobile.final_project;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NoteAdapter extends CursorAdapter {

    LayoutInflater inflater;
    Cursor cursor;

    public NoteAdapter(Context context, Cursor c, int flags) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvReviewTitle = (TextView)view.findViewById(R.id.tvNoteTitle);
        TextView tvReviewDate = (TextView)view.findViewById(R.id.tvNoteDate);
        TextView tvReviewPlace = (TextView)view.findViewById(R.id.tvNotePlace);
        TextView tvReviewContent = (TextView)view.findViewById(R.id.tvNoteContent);

        tvReviewTitle.setText(cursor.getString(cursor.getColumnIndex(NoteDBHelper.COL_TITLE)));
        tvReviewDate.setText(cursor.getString(cursor.getColumnIndex(NoteDBHelper.COL_DATE)));
        tvReviewPlace.setText(cursor.getString(cursor.getColumnIndex(NoteDBHelper.COL_PLACE)));
        tvReviewContent.setText(cursor.getString(cursor.getColumnIndex(NoteDBHelper.COL_CONTENT)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemLayout = inflater.inflate(R.layout.listview_note, parent, false);
        return listItemLayout;
    }

}