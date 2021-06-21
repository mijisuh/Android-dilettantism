package ddwu.mobile.final_project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class CulartPlaceDetailActivity extends AppCompatActivity {

    final static int PERMISSION_REQ_CODE = 100;

    private TextView tvPerformingplaceName;
    private TextView tvPerformingplaceAddr;
    private TextView tvPerformingplaceOpenDate;
    private TextView tvPerformingplaceTel;
    private TextView tvPerformingplaceHomeUrl;
    private ImageView ivPerformingplaceImage;

    private CulartPlaceDetailDTO dto;
    private CulartPlaceDetailXmlParser parser;
    private String apiAddr;

    private GoogleMap mGoogleMap;
    private Marker centerMarker;
    private MarkerOptions options;
    private LatLng loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culartplace_detail);

        apiAddr = getResources().getString(R.string.api_culartplace_detail);
        parser = new CulartPlaceDetailXmlParser();
        String seq = getIntent().getStringExtra("seq");

        tvPerformingplaceName = findViewById(R.id.tvPerformingplaceName2);
        tvPerformingplaceAddr = findViewById(R.id.tvPerformingplaceAddr);
        tvPerformingplaceOpenDate = findViewById(R.id.tvPerformingplaceOpenDate);
        tvPerformingplaceTel = findViewById(R.id.tvPerformingplaceTel2);
        tvPerformingplaceHomeUrl = findViewById(R.id.tvPerformingplaceHomeUrl2);
        ivPerformingplaceImage = findViewById(R.id.ivPerformingplaceImage);

        options = new MarkerOptions();

        // 공연장 상세정보 openAPI를 이용하여 특정 공연장의 상세정보를 볼 수 있음
        try {
            new openapiAsyncTask().execute(apiAddr + URLEncoder.encode(seq, "UTF-8") + "&ServiceKey=" + getResources().getString(R.string.servicekey_culartplace));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

            options.position(loc);

            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();
        }
    };


    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);      // 매배변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로
    }

    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class openapiAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(CulartPlaceDetailActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) return "Error!";
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            dto = parser.parse(result);

            loc = new LatLng(dto.getLatitude(), dto.getLongitude());

            tvPerformingplaceName.setText(dto.getName());
            tvPerformingplaceAddr.setText(dto.getAddress());
            tvPerformingplaceTel.setText(dto.getTel());
            tvPerformingplaceHomeUrl.setText(dto.getHomeUrl());

            new GetImageAsyncTask().execute(dto.getImageLink());

            if (checkPermission()) {
                mapLoad();
            }

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

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }

        protected String readStreamToString(InputStream stream) {
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
                conn = (HttpURLConnection) url.openConnection();
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

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        public GetImageAsyncTask() {
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageAddress = params[0];
            Bitmap result = downloadImage(imageAddress);
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ivPerformingplaceImage.setImageBitmap(bitmap);
        }

        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        private Bitmap downloadImage(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            Bitmap result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                HttpURLConnection.setFollowRedirects(true);
                stream = getNetworkConnection(conn);
                result = readStreamToBitmap(stream);
                if (stream != null) stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }

            return result;
        }

        private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // 이미지 다운로드 오류 처리 (HTTP 302 에러)
            // 전달 받은 URL로 ResponseCode를 추출해서 Redirection 이면 header에 Location 추가하고 재시도
            int status = conn.getResponseCode();

            // Redirection 처리
            if (status == HttpsURLConnection.HTTP_MOVED_TEMP || status == HttpsURLConnection.HTTP_MOVED_PERM) {
                // Redirected URL 받아오기
                String redirectedUrl = conn.getHeaderField("Location");
                URL url = new URL(redirectedUrl);

                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
            }

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }

        private Bitmap readStreamToBitmap(InputStream stream) {
            return BitmapFactory.decodeStream(stream);
        }
    }

}