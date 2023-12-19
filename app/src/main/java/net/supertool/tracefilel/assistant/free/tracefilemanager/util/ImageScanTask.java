package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.RecoveredFilePath;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.getLocalVideoDuration;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ImageFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImageScanTask extends AsyncTask<Void, Void, ArrayList<ImageFile>> {
    private Context context;
    private ArrayList<String> pathsToExclude;
    private OnVideoScanListener listener;

    public ImageScanTask(Context context, OnVideoScanListener listener) {
        this.context = context;

        this.listener = listener;
    }

    @Override
    protected ArrayList<ImageFile> doInBackground(Void... voids) {
        ArrayList<ImageFile> imagePathList = new ArrayList<>();
        String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        if (decodeString!=null){
            pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            pathsToExclude = new ArrayList<>();
        }

        ContentResolver imageResolver = context.getContentResolver();
        String[] imageProjection = {MediaStore.Images.Media.DATA};
        Cursor imageCursor = imageResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                null,
                null,
                null
        );

        if (imageCursor != null) {
            // 遍历查询结果
            long allImageSize = 0;
            while (imageCursor.moveToNext()) {
                // 获取图片路径
                String imagePath = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                if (pathsToExclude.contains(imagePath)){
                    continue;
                }
                File file = new File(imagePath);
                if (file.exists() && file.isFile()) {
                    // 添加到图片路径列表


                    String name = file.getName();
                    long size = file.length();  // 获取压缩包的大小，单位为字节
                    allImageSize += size;
                    double sizeMB = (double) size / (1024 * 1024);
                    String formattedSize;

                    if (sizeMB < 1) {
                        // 如果大小小于1MB，则显示为KB
                        double sizeKB = (double) size / 1024;
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        formattedSize = decimalFormat.format(sizeKB) + "KB";
                    } else {
                        // 如果大小大于等于1MB，则显示为MB
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        formattedSize = decimalFormat.format(sizeMB) + "MB";
                    }

                    long lastModified = file.lastModified();  // 获取压缩包的最后修改时间，单位为毫秒

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTime = dateFormat.format(new Date(lastModified));

                    imagePathList.add(new ImageFile(imagePath,formattedSize,formattedTime,name,size));
                }
            }
            MMKV.defaultMMKV().encode("allImageFileSize",allImageSize);
            imageCursor.close();
        }

        return imagePathList;
    }

    @Override
    protected void onPostExecute(ArrayList<ImageFile> imageFiles) {
        if (listener != null) {
            listener.onVideoScanComplete(imageFiles);
        }
    }

    public interface OnVideoScanListener {
        void onVideoScanComplete(ArrayList<ImageFile> imageFiles);
    }
}
