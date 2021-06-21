package ddwu.mobile.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NearbyDetailActivity extends AppCompatActivity {

    TextView tvName;
    TextView tvPhone;
    TextView tvAddress;

    String select;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_detail);

        Intent intent = getIntent();
        select = intent.getStringExtra("select");
        name = intent.getStringExtra("name");

        tvName = findViewById(R.id.tvNearbyName);
        tvPhone = findViewById(R.id.tvNearbyTel);
        tvAddress = findViewById(R.id.tvNearbyAddr);

        tvName.setText(name);
        tvPhone.setText(intent.getStringExtra("phone"));
        tvAddress.setText(intent.getStringExtra("address"));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                Intent intent = null;
                switch (select) {
                    case "gallery":
                        intent = new Intent(NearbyDetailActivity.this, SearchArtgalleryActivity.class);
                        break;
                    case "library":
                        intent = new Intent(NearbyDetailActivity.this, SearchLibraryActivity.class);
                        break;
                    case "museum":
                        intent = new Intent(NearbyDetailActivity.this, SearchMuseumActivity.class);
                        break;
                }
                intent.putExtra("name", name);
                if (intent != null) startActivity(intent);
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }
}
