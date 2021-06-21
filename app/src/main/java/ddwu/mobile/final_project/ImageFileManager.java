package ddwu.mobile.final_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFileManager {

    final static String TAG = "ImageFileManager";
    final static String IMG_EXT = ".jpg";

    private Context context;


    public ImageFileManager(Context context) {
        this.context = context;
    }

    public String saveBitmapToInternal(Bitmap bitmap, String url) {
        String fileName = null;
        try {
            fileName = getFileNameFromUrl(url);

            File saveFile = new File(context.getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(saveFile);

            bitmap = resize(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileName = null;
        } catch (IOException e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    public Bitmap getSavedBitmapFromInternal(String url) {
        String fileName = getFileNameFromUrl(url);;

        String path = context.getFilesDir().getPath() + "/" + fileName;

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        Log.i(TAG, path);

        return bitmap;
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

    public String getFileNameFromUrl(String url) {
        String fileName = Uri.parse(url).getLastPathSegment();
        return fileName.replace("\n", "");
    }

}
