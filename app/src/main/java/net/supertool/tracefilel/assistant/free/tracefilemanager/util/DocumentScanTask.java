package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.RecoveredFilePath;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

public class DocumentScanTask extends AsyncTask<Void, Void, ArrayList<ZipFile>> {
    private Context context;
    private ArrayList<String> pathsToExclude;
    private OnVideoScanListener listener;

    public DocumentScanTask(Context context, OnVideoScanListener listener) {
        this.context = context;

        this.listener = listener;
    }

    @Override
    protected ArrayList<ZipFile> doInBackground(Void... voids) {
        ArrayList<ZipFile> documentPathList = new ArrayList<>();
        String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        if (decodeString!=null){
            pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            pathsToExclude = new ArrayList<>();
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns.DATA};

        String selection = MediaStore.Files.FileColumns.MIME_TYPE + " IN (?, ?, ?, ?, ?, ?)";
        String[] selectionArgs = {
                "application/msword", // Word文件
                "application/vnd.ms-excel", // Excel文件
                "application/vnd.ms-powerpoint", // PowerPoint文件
                "application/pdf", // PDF文件
                "text/plain", // TXT文件
                "application/x-rar-compressed" // RAR文件
        };

        Cursor documentCursor = contentResolver.query(uri, projection, selection, selectionArgs, null);


        if (documentCursor != null) {
            // 遍历查询结果
            long allDocumentFileSize = 0;
            while (documentCursor.moveToNext()) {
                // 获取document路径
                String documentPath = documentCursor.getString(documentCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));

                File file = new File(documentPath);
                if (file.exists() && file.isFile()) {
                    if (pathsToExclude.contains(documentPath)) {
                        continue;
                    }
                    String name = file.getName();
                    long size = file.length();  // 获取音频文件大小，单位为字节
                    allDocumentFileSize += size;
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

                    documentPathList.add(new ZipFile(documentPath, formattedSize, formattedTime, name,size));
                }
            }
            documentCursor.close();
            MMKV.defaultMMKV().encode("allDocumentFileSize",allDocumentFileSize);

        }

        return documentPathList;
    }

    @Override
    protected void onPostExecute(ArrayList<ZipFile> documentFiles) {
        if (listener != null) {
            listener.onVideoScanComplete(documentFiles);
        }
    }

    public interface OnVideoScanListener {
        void onVideoScanComplete(ArrayList<ZipFile> documentFiles);
    }
}
