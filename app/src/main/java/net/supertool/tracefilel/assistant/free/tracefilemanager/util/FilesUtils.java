package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.JsonRecycleBinFiles;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.RecoveredFilePath;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.unitConversion;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.getLocalVideoDuration;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.FileBean;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ImageFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.PathsToExclude;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class FilesUtils {
    public interface MediaCountListener {
        void onMediaCountObtained(ArrayList<ImageFile> imagePathList, ArrayList<VideoFile> videoPathList, ArrayList<ZipFile> zipPathList,ArrayList<ZipFile> allFileList);
    }

    public static void getMediaCountsAsync(Context context, MediaCountListener listener) {
        new MediaCountAsyncTask(context, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class MediaCountAsyncTask extends AsyncTask<Void, Void, MediaCounts> {
        private Context context;
        private MediaCountListener listener;
        ArrayList<ImageFile> imagePathList = new ArrayList<>();
        ArrayList<VideoFile> videoPathList = new ArrayList<>();
        ArrayList<ZipFile> zipPathList = new ArrayList<>();
        ArrayList<ZipFile> allFileList = new ArrayList<>();
        ArrayList<FileBean> pathsToExcludes = new ArrayList<>();

        private MediaCountAsyncTask(Context context, MediaCountListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected MediaCounts doInBackground(Void... voids) {
            MediaCounts mediaCounts = new MediaCounts();
            String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
            ArrayList<String> pathsToExclude;
            if (decodeString!=null){
                pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
            }else {
                pathsToExclude = new ArrayList<>();
            }
/*
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
*/

            Thread imageThread = new Thread(new Runnable() {
                @Override
                public void run() {
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

                                synchronized(allFileList) {
                                    allFileList.add(new ZipFile(imagePath,formattedSize,formattedTime,name,size));
                                }

                            }


                        }

                    }
                    mediaCounts.imagePathList = imagePathList;


                    if (imageCursor != null) {
                        imageCursor.close();
                    }
                }
            });

            Thread videoThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentResolver videoResolver = context.getContentResolver();
                    String[] videoProjection = {MediaStore.Video.Media.DATA};
                    Cursor videoCursor = videoResolver.query(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            videoProjection,
                            null,
                            null,
                            null
                    );
                    if (videoCursor != null) {
                        // 遍历查询结果
                        long allVideoSize = 0;
                        while (videoCursor.moveToNext()) {
                            // 获取视频路径
                            String videoPath = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                            // 添加到图片路径列表
                            if (pathsToExclude.contains(videoPath)){
                                continue;
                            }
                            File file = new File(videoPath);
                            if (file.exists() && file.isFile()) {

                                String name = file.getName();
                                long size = file.length();  // 获取压缩包的大小，单位为字节
                                allVideoSize += size;
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

                                String duration = getLocalVideoDuration(videoPath);

                                videoPathList.add(new VideoFile(videoPath,formattedSize,formattedTime,name,duration,size));
                                synchronized(allFileList) {
                                    allFileList.add(new ZipFile(videoPath, formattedSize, formattedTime, name, size));
                                }
                            }

                        }
                        MMKV.defaultMMKV().encode("allVideoFileSize",allVideoSize);

                    }
                    mediaCounts.videoPathList = videoPathList;
                    if (videoCursor != null) {
                        videoCursor.close();
                    }
                }
            });

            Thread compressedFileThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentResolver fileResolver = context.getContentResolver();
                    String[] fileProjection = {MediaStore.Files.FileColumns.DATA};
                    String fileSelection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                    String[] fileSelectionArgs = new String[]{"application/zip"};
                    Cursor fileCursor = fileResolver.query(
                            MediaStore.Files.getContentUri("external"),
                            fileProjection,
                            fileSelection,
                            fileSelectionArgs,
                            null
                    );
                    if (fileCursor != null) {
                        // 遍历查询结果
                        while (fileCursor.moveToNext()) {
                            String filePath = fileCursor.getString(fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                            if (pathsToExclude.contains(filePath)){
                                continue;
                            }
                            File file = new File(filePath);
                            if (file.exists() && file.isFile()) {
                                String name = file.getName();
                                long size = file.length();  // 获取压缩包的大小，单位为字节
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

                                zipPathList.add(new ZipFile(filePath,formattedSize,formattedTime,name,size));
                                synchronized(allFileList) {
                                    allFileList.add(new ZipFile(filePath, formattedSize, formattedTime, name, size));
                                }
                            }

                        }

                    }
                    mediaCounts.zipPathList = zipPathList;
                    if (fileCursor != null) {
                        fileCursor.close();
                    }
                }
            });

            // 启动线程并等待所有线程完成
            try {
                imageThread.start();
                videoThread.start();
                compressedFileThread.start();
                imageThread.join();
                videoThread.join();
                compressedFileThread.join();

                mediaCounts.allPathList = allFileList;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return mediaCounts;
        }


        @Override
        protected void onPostExecute(MediaCounts mediaCounts) {
            if (listener != null) {
                listener.onMediaCountObtained(
                        mediaCounts.imagePathList,
                        mediaCounts.videoPathList,
                        mediaCounts.zipPathList,
                        mediaCounts.allPathList
                );
            }
        }
    }

    private static class MediaCounts {
        ArrayList<ImageFile> imagePathList ;
        ArrayList<VideoFile> videoPathList ;
        ArrayList<ZipFile> zipPathList  ;
        ArrayList<ZipFile> allPathList  ;
    }

    public static ArrayList<FileBean> getFileBean(String Path){

        String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        ArrayList<String> pathsToExclude;
        if (decodeString!=null){
            pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            pathsToExclude = new ArrayList<>();
        }

        ArrayList<FileBean> fileBeans = new ArrayList<>();
        File downloadDir = new File(Path);
        if (downloadDir.exists() && downloadDir.isDirectory()) {
            // 下载目录存在且是一个目录

            File[] files = downloadDir.listFiles();
            for (File file : files) {
                String path = file.getAbsolutePath();
                if (pathsToExclude.contains(path)){
                    continue;
                }
                if (file.isDirectory()) {
                    // 文件夹
                    String folderName = file.getName();
                    String absolutePath = file.getAbsolutePath();
                    //  long length = file.length();
                    long lastModified = file.lastModified();  // 获取压缩包的最后修改时间，单位为毫秒
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTime = dateFormat.format(new Date(lastModified));

                    File[] files2 = file.listFiles();
                    int fileCount=0;

                    for (File file1 : files2) {
                        if (pathsToExclude.contains(file1.getAbsolutePath())){
                            continue;
                        }
                        fileCount++;
                    }
                  //  int fileCount = files2.length;
                    System.out.println("文件夹中的文件数量：" + fileCount);

                    fileBeans.add(new FileBean(true,absolutePath,null,formattedTime,folderName,0L,fileCount));

                    System.out.println("文件夹名："+folderName);
                } else {
                    // 文件
                    String folderName = file.getName();
                    String absolutePath = file.getAbsolutePath();
                    //  long length = file.length();
                    long lastModified = file.lastModified();  // 获取压缩包的最后修改时间，单位为毫秒
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTime = dateFormat.format(new Date(lastModified));

                    long length = file.length();
                    String size = unitConversion(length);

                    fileBeans.add(0,new FileBean(false,absolutePath,size,formattedTime,folderName,length,0));

                    // 处理文件逻辑...
                }
            }

        }
        Collections.reverse(fileBeans);
        return fileBeans;

    }
}