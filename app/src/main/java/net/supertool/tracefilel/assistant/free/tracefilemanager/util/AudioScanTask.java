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

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.AudioFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AudioScanTask extends AsyncTask<Void, Void, ArrayList<ZipFile>> {
    private Context context;
    private ArrayList<String> pathsToExclude;
    private OnVideoScanListener listener;

    public AudioScanTask(Context context, OnVideoScanListener listener) {
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

        ContentResolver audioResolver = context.getContentResolver();
        String[] videoProjection = {MediaStore.Audio.Media.DATA};
        Cursor audioCursor = audioResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                null
        );

        if (audioCursor != null) {
            // 遍历查询结果
            long allAudioSize = 0;
            while (audioCursor.moveToNext()) {
                // 获取视频路径
                String audioPath = audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                // 添加到视频路径列表

                File file = new File(audioPath);
                if (file.exists() && file.isFile()) {
                    if (pathsToExclude.contains(audioPath)) {
                        continue;
                    }
                    String name = file.getName();
                    long size = file.length();  // 获取音频文件大小，单位为字节
                    allAudioSize += size;
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
            MMKV.defaultMMKV().encode("allAudioFileSize",allAudioSize);
            audioCursor.close();
        }

        return audioPathList;
    }

    @Override
    protected void onPostExecute(ArrayList<ZipFile> audioPathList) {
        if (listener != null) {
            listener.onVideoScanComplete(audioPathList);
        }
    }

    public interface OnVideoScanListener {
        void onVideoScanComplete(ArrayList<ZipFile> audioPathList);
    }
}
