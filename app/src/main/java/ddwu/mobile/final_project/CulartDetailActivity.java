package ddwu.mobile.final_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class CulartDetailActivity extends AppCompatActivity {

    private TextView tvCulartTitle;
    private TextView tvCulartStartdate;
    private TextView tvCulartEnddate;
    private TextView tvCulartPlace;
    private TextView tvCulartRealm;
    private TextView tvCulartPrice;
    private WebView wvCulartContents;
    private TextView tvCulartTel;
    private ImageView ivCulartImg;


    private CulartDetailDTO dto;
    private CulartDetailXmlParser parser;
    private String apiAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culart_detail);

        apiAddr = getResources().getString(R.string.api_culart_detail);
        parser = new CulartDetailXmlParser();
        String seq = getIntent().getStringExtra("seq");

        tvCulartTitle = findViewById(R.id.tvCulartTitle2);
        tvCulartStartdate = findViewById(R.id.tvCulartStartdate);
        tvCulartEnddate = findViewById(R.id.tvCulartEnddate);
        tvCulartPlace = findViewById(R.id.tvCulartPlace2);
        tvCulartRealm = findViewById(R.id.tvCulartRealm);
        tvCulartPrice = findViewById(R.id.tvCulartPrice);
        wvCulartContents = findViewById(R.id.tvCulartContents);
        tvCulartTel = findViewById(R.id.tvCulartTel);

        ivCulartImg = findViewById(R.id.ivCulartImg);

        try {
            new CulartAsyncTask().execute(apiAddr + URLEncoder.encode(seq, "UTF-8") + "&ServiceKey=" + getResources().getString(R.string.servicekey_culart));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchCulartPlace:
                String seq = dto.getPlaceSeq();

                Intent intent = new Intent(CulartDetailActivity.this, CulartPlaceDetailActivity.class);
                intent.putExtra("seq", seq);

                startActivity(intent);
        }

    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class CulartAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(CulartDetailActivity.this, "Wait", "Downloading...");
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

            tvCulartTitle.setText(dto.getTitle());
            tvCulartStartdate.setText(dto.getStartDate());
            tvCulartEnddate.setText(dto.getEndDate());
            tvCulartPlace.setText(dto.getPlace());
            tvCulartRealm.setText(dto.getRealm());
            tvCulartPrice.setText(dto.getPrice());
            tvCulartTel.setText(dto.getTel());

            String str = "<meta http-equiv='Content-Type' content='text/html; charset=utf-16le'>"
                    + "<html><header><style type='text/css'>img { max-width: 100%; height: auto; }</style></header><body><div style='text-align: center;'>"
                    + "<a href=" + dto.getUrl() + ">예매 및 관련 사이트 이동</a><br>"
                    + dto.getContents() + "</div></body></html>";

            wvCulartContents.loadData(str, "text/html", "UTF-8");

            new GetImageAsyncTask().execute(dto.getImgUrl());

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
            bitmap = resize(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            ivCulartImg.setImageBitmap(bitmap);
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