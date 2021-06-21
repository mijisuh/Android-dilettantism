package ddwu.mobile.final_project;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ClassDetailActivity extends AppCompatActivity {

    WebView wvClass;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        String link = getIntent().getStringExtra("link");
        wvClass = findViewById(R.id.wvClass);

        WebSettings wSettings = wvClass.getSettings();
        wSettings.setJavaScriptEnabled(true);

        wvClass.setWebViewClient(new WebViewClient());
        wvClass.loadUrl(link);
    }

}
