package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.RecoveredFilePath;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.unitConversion;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ZipScanTask extends AsyncTask<Void, Void, ArrayList<ZipFile>> {
    private Context context;
    private ArrayList<String> pathsToExclude;
    private OnVideoScanListener listener;

    public ZipScanTask(Context context, OnVideoScanListener listener) {
        this.context = context;

        this.listener = listener;
    }

    @Override
    protected ArrayList<ZipFile> doInBackground(Void... voids) {
        ArrayList<ZipFile> audioPathList = new ArrayList<>();
        String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        if (decodeString!=null){
            pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            pathsToExclude = new ArrayList<>();
        }

        ContentResolver fileResolver = context.getContentResolver();
        String[] fileProjection = {MediaStore.Files.FileColumns.DATA};
        String fileSelection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] fileSelectionArgs = new String[]{"application/zip"};
        Cursor zipCursor = fileResolver.query(
                MediaStore.Files.getContentUri("external"),
                fileProjection,
                fileSelection,
                fileSelectionArgs,
                null
        );

        if (zipCursor != null) {
            // 遍历查询结果
            long allZipFileSize = 0;
            while (zipCursor.moveToNext()) {
                // 获取zip路径
                String audioPath = zipCursor.getString(zipCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));

                File file = new File(audioPath);
                if (file.exists() && file.isFile()) {
                    if (pathsToExclude.contains(audioPath)) {
                        continue;
                    }
                    String name = file.getName();
                    long size = file.length();  // 获取音频文件大小，单位为字节
                    allZipFileSize += size;
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

                    long lastModified = file.lastModified();  // 获取视频文件的最后修改时间，单位为毫秒

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTime = dateFormat.format(new Date(lastModified));

                    audioPathList.add(new ZipFile(audioPath, formattedSize, formattedTime, name,size));
                }
            }
            zipCursor.close();
            MMKV.defaultMMKV().encode("allZipFileSize",allZipFileSize);

        }

        return audioPathList;
    }

    @Override
    protected void onPostExecute(ArrayList<ZipFile> zipFiles) {
        if (listener != null) {
            listener.onVideoScanComplete(zipFiles);
        }
    }

    public interface OnVideoScanListener {
        void onVideoScanComplete(ArrayList<ZipFile> zipFiles);
    }
}
