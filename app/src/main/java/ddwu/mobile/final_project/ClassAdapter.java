package ddwu.mobile.final_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ClassAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<ClassDTO> list;
    private ImageFileManager imageFileManager = null;

    public ClassAdapter(Context context, int layout, ArrayList<ClassDTO> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageFileManager = new ImageFileManager(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ClassDTO getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ClassAdapter.ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ClassAdapter.ViewHolder();
            viewHolder.tvClassTitle = view.findViewById(R.id.tvClassTitle);
            viewHolder.tvClassTutor = view.findViewById(R.id.tvClassTutor);

            viewHolder.ivClass = view.findViewById(R.id.ivClass);
            view.setTag(viewHolder);
        } else
            viewHolder = (ClassAdapter.ViewHolder) view.getTag();

        ClassDTO dto = list.get(position);

        viewHolder.tvClassTitle.setText(dto.getTitle());
        viewHolder.tvClassTutor.setText(dto.getTutor());

        Bitmap savedBitmap = imageFileManager.getSavedBitmapFromInternal(dto.getImg());

//        파일에서 이미지 파일을 읽어온 결과에 따라 파일 이미지 사용 또는 네트워크 다운로드 수행
        if (savedBitmap != null) {
            viewHolder.ivClass.setImageBitmap(savedBitmap);
        } else {
            ClassAdapter.GetImageAsyncTask task = new ClassAdapter.GetImageAsyncTask(viewHolder);
            task.execute(dto.getImg());
        }

        return view;
    }

    public void setList(ArrayList<ClassDTO> list) {
        this.list = list;
    }

    static class ViewHolder {
        public TextView tvClassTitle = null;
        public TextView tvClassTutor = null;
        public ImageView ivClass = null;
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ClassAdapter.ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ClassAdapter.ViewHolder holder) {
            viewHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = downloadImage(imageAddress);
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            /*작성할 부분*/
            /*네트워크에서 다운 받은 이미지 파일을 ImageFileManager 를 사용하여
             내부저장소에 저장 다운받은 bitmap 을 이미지뷰에 지정*/

            /*네트워크를 통해 bitmap 을 정상적으로 받아왔을 경우 수행
             * 서버로부터 정상적으로 이미지 다운로드를 못했을 경우 null 이 반환되므로 기본 설정 이미지가 계속 유지됨*/
            if (bitmap != null) {
                // 다운로드한 bitmap 을 내부저장소에 저장
                imageFileManager.saveBitmapToInternal(bitmap, imageAddress);
                // 다운로드한 bitmap 을 이미지뷰에 표시
                viewHolder.ivClass.setImageBitmap(bitmap);
            }
        }

        /* 이미지를 다운로드하기 위한 네트워크 관련 메소드 */

        /* 네트워크 환경 조사 */
        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        /* 주소를 전달받아 bitmap 다운로드 후 반환 */
        private Bitmap downloadImage(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            Bitmap result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection)url.openConnection();
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

        /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
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

        /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
        private Bitmap readStreamToBitmap(InputStream stream) {
            return BitmapFactory.decodeStream(stream);
        }

    }
}
