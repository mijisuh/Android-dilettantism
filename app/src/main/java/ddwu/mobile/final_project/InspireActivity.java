package ddwu.mobile.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class InspireActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspire);
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnCulart1:
                intent = new Intent(this, SearchCulartAreaActivity.class);
                break;
            case R.id.btnCulart2:
                intent = new Intent(this, SearchCulartRealmActivity.class);
                break;
            case R.id.btnCulartPlace1:
                intent = new Intent(this, SearchPerformingplaceActivity.class);
                break;
            case R.id.btnCulartPlace2:
                intent = new Intent(this, SearchArtgalleryActivity.class);
                break;
            case R.id.btnCulartPlace3:
                intent = new Intent(this, SearchLibraryActivity.class);
                break;
            case R.id.btnCulartPlace4:
                intent = new Intent(this, SearchMuseumActivity.class);
                break;
            case R.id.btnCulartPlace5:
                intent = new Intent(this, SearchNearbyActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }
}
