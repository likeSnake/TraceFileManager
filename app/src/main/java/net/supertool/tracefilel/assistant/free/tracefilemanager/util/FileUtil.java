package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.AudioStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.DocumentStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.ImageStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.JsonRecycleBinFiles;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.RecoveredFilePath;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.StatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.VideoStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.ZipStatusChange;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.FileBean;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.PathsToExclude;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    public static boolean checkAllPermission(Activity activity, int REQUEST_CODE){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                return true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE);
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                return false;
            }
        } else {
            return true;
        }
    }
    public static void moveRecycleBin(String path){
        String s = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        ArrayList<String> list;

        if (s!=null){
            list = new Gson().fromJson(s,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            list = new ArrayList<>();
        }
        list.add(path);

        String jsonList = new Gson().toJson(list);
        MMKV.defaultMMKV().encode(RecoveredFilePath,jsonList);
    }

    public static void recoveryFile(String path){
        String s = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        ArrayList<String> list;

        if (s!=null){
            list = new Gson().fromJson(s,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            list = new ArrayList<>();
        }
        list.remove(path);

        String jsonList = new Gson().toJson(list);
        MMKV.defaultMMKV().encode(RecoveredFilePath,jsonList);
    }
    public static ArrayList<FileBean> getExcludesList(){
        long startTime = System.currentTimeMillis();
        String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        ArrayList<FileBean> pathsToExcludes = new ArrayList<>();
        ArrayList<String> pathsToExclude;
        if (decodeString!=null){
            pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            pathsToExclude = new ArrayList<>();
        }
        long recycleBinSize = 0;
        for (String excludeFilePath : pathsToExclude) {
            File file = new File(excludeFilePath);
            if (file.exists() ) {
                String formattedSize="";
                long size=0;
                boolean isDirectory = false;
                int fileCount = 0;
                if (file.isFile()){
                    size = file.length();
                    recycleBinSize += size;
                    double sizeMB = (double) size / (1024 * 1024);

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
                }else if (file.isDirectory()){
                    isDirectory = true;
                    File[] files = file.listFiles();
                    if (files!=null){
                        fileCount = files.length;
                    }

                }

                String name = file.getName();
                long lastModified = file.lastModified();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedTime = dateFormat.format(new Date(lastModified));

                pathsToExcludes.add(new FileBean(isDirectory,excludeFilePath,formattedSize,formattedTime,name,size,fileCount));
            }
        }
        Collections.reverse(pathsToExcludes);
        MMKV.defaultMMKV().encode(JsonRecycleBinFiles,new Gson().toJson(pathsToExcludes));
        MMKV.defaultMMKV().encode("recycleBinFilesSize",recycleBinSize);


        System.out.println("读取回收文件的时间："+(System.currentTimeMillis()-startTime));
        return pathsToExcludes;
    }
    public static String getDuration(String filePath){
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(filePath);  //filePath为文件的路径
            player.prepare();
        } catch (Exception e) {
            Log.e("视频工具报错", "getDuration: "+e.toString());
        }
        double duration= player.getDuration();//获取媒体文件时长

        player.release();//记得释放资源
        return String.valueOf(duration/1000);
    }
    public static String getLocalVideoDuration(String filePath) {
        int duration = 0;
        try {
            MediaMetadataRetriever mmr = new  MediaMetadataRetriever();
            mmr.setDataSource(filePath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION))/1000;//除以 1000 返回是秒
            //时长(毫秒)
//            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
//            //宽
//            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//            //高
//            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        } catch (Exception e) {
            e.printStackTrace();
            return "00:00";
        }
        return formatTime(duration);
    }

    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds);
    }

    public static void FilesStatusChange(){
        StatusChange = true;
        VideoStatusChange = true;
        ImageStatusChange = true;
        AudioStatusChange = true;
        ZipStatusChange = true;
        DocumentStatusChange = true;
    }

    public static boolean isImageFileByExtension(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif")
                    ||fileExtension.equals("webp") ||fileExtension.equals("svg") ;
        }
        return false;
    }

    public static boolean isVideoFileByExtension(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            return fileExtension.equals("mp4")   ;
        }
        return false;
    }

    public static boolean isAudioFileByExtension(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            return fileExtension.equals("mp3") || fileExtension.equals("aac") || fileExtension.equals("wma") || fileExtension.equals("wav")
                    || fileExtension.equals("flac")|| fileExtension.equals("ogg")
                    ;
        }
        return false;
    }

    public static boolean isZipFileByExtension(String fileName){
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
            return fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("tar")|| fileExtension.equals("7z")
                    ;
        }
        return false;
    }
}
