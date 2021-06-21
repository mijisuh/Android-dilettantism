package ddwu.mobile.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnCreate:
                intent = new Intent(this, SearchClassActivity.class);
                break;
            case R.id.btnInspire:
                intent = new Intent(this, InspireActivity.class);
                break;
            case R.id.btnMyNote:
                intent = new Intent(this, NoteActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }

}