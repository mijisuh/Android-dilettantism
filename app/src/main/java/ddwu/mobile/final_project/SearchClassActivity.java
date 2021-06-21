package ddwu.mobile.final_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SearchClassActivity extends AppCompatActivity {
    public static final String TAG = "원데이클래스 검색";

    EditText etKeyword;
    ListView lvClass;
    String apiAddr;
    String keywd;

    ArrayList<ClassDTO> resultList;
    ClassAdapter adapter;
    ClassXmlParser parser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_class);

        etKeyword = findViewById(R.id.etClassKeyword);

        lvClass = findViewById(R.id.lvClass);

        resultList = new ArrayList();
        adapter = new ClassAdapter(this, R.layout.listview_class, resultList);
        lvClass.setAdapter(adapter);

        apiAddr = getResources().getString(R.string.api_class);
        parser = new ClassXmlParser();

        // 공연장 항목을 클릭 시 일련번호(seq)를 이용해 공연장의 상세 정보를 보여주는 액티비티 생성
        lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassDTO dto = resultList.get(position);
                String link = dto.getLink();
                Intent intent = new Intent(SearchClassActivity.this, ClassDetailActivity.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchClass: // 사용자로부터 입력받은 키워드로 공연장 검색
                keywd = "원데이클래스체험" + etKeyword.getText().toString();
                try {
                    new SearchClassActivity.SearchClassAsyncTask().execute(apiAddr + URLEncoder.encode(keywd, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class SearchClassAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchClassActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            Log.i(TAG, address);
            String result = downloadContents(address);
            if (result == null) return "Error!";
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            resultList = parser.parse(result);

            adapter.setList(resultList);
            adapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }

        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("X-Naver-Client-Id", getResources().getString(R.string.client_id));
            conn.setRequestProperty("X-Naver-Client-Secret", getResources().getString(R.string.client_secret));

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }

        protected String readStreamToString(InputStream stream){
            StringBuilder result = new StringBuilder();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String readLine = bufferedReader.readLine();

                while (readLine != null) {
                    result.append(readLine + "\n");
                    readLine = bufferedReader.readLine();
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        protected String downloadContents(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            String result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection)url.openConnection();
                stream = getNetworkConnection(conn);
                result = readStreamToString(stream);
                if (stream != null) stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }

            return result;
        }
    }
}
