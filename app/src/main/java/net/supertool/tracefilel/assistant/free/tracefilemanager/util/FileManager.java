package net.supertool.tracefilel.assistant.free.tracefilemanager.util;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.RecoveredFilePath;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.bean.FileInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    private DuplicateFileTask duplicateFileTask;

    public void findDuplicateFiles(ArrayList<ZipFile> directoryPath, DuplicateFileListener listener) {
        duplicateFileTask = new DuplicateFileTask(listener);
        duplicateFileTask.execute(directoryPath);
      //  Unchecked generics array creation for varargs parameter
    }

    public void cancelDuplicateFileSearch() {
        if (duplicateFileTask != null) {
            duplicateFileTask.cancel(true);
        }
    }

    private static class DuplicateFileTask extends AsyncTask<ArrayList<ZipFile> , Void, Map<ArrayList<ZipFile>, FileInfo>> {
        private DuplicateFileListener listener;

        public DuplicateFileTask(DuplicateFileListener listener) {
            this.listener = listener;
        }

        @Override
        protected Map<ArrayList<ZipFile>, FileInfo> doInBackground(ArrayList<ZipFile>... params) {
            ArrayList<ZipFile> allList = params[0];
            Map<String, ArrayList<ZipFile>> duplicateFilesMap = new HashMap<>();
            Map<ArrayList<ZipFile>, FileInfo> fanhui = new HashMap<>();
            ArrayList<ZipFile> bigFilePath = new ArrayList<>();

            String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
            ArrayList<String> pathsToExclude;
            if (decodeString!=null){
                pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
            }else {
                pathsToExclude = new ArrayList<>();
            }

            ArrayList<ZipFile> chongfu = new ArrayList<>();
            long bigFileSize=0;

            for (ZipFile zipFile : allList) {
                File file = new File(zipFile.getPath());
                if (file.exists() && file.isFile()) {
                    if (pathsToExclude.contains(zipFile.getPath())){
                        continue;
                    }
                    if (file.length()>(1024*1024*50)){
                        bigFilePath.add(zipFile);
                        bigFileSize+=file.length();
                    }

                    String key = file.length() + "_" + getFileExtension(file.getName());
                    if (duplicateFilesMap.containsKey(key)) {
                        duplicateFilesMap.get(key).add(zipFile);
                    } else {
                        ArrayList<ZipFile> fileList = new ArrayList<>();
                        fileList.add(zipFile);
                        duplicateFilesMap.put(key, fileList);
                    }
                }


            }
            MMKV.defaultMMKV().encode("bigFilesSize",bigFileSize);
            double bigMB = (double) bigFileSize / (1024 * 1024);
            String formatBigSize;


            if (bigMB < 1) {
                // 如果大小小于1MB，则显示为KB
                double sizeKB = (double) bigFileSize / 1024;
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                formatBigSize = decimalFormat.format(sizeKB) + "KB";
            } else if (bigMB < 1024){
                // 如果大小大于等于1MB，则显示为MB
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                formatBigSize = decimalFormat.format(bigMB) + "MB";
            }else {
                // 如果大小大于等于1GB，则显示为GB
                double sizeGB = (double) bigMB / 1024;
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                formatBigSize = decimalFormat.format(sizeGB) + "GB";
            }

            for (Map.Entry<String, ArrayList<ZipFile>> entry : duplicateFilesMap.entrySet()) {
                ArrayList<ZipFile> cfList = entry.getValue();
                if (cfList.size() > 1) {

                    chongfu.addAll(cfList);
                }
            }
            long allSize=0;

            for (ZipFile zipFile : chongfu) {
                String path = zipFile.getPath();
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    long size = file.length();

                    allSize+=size;
                }
            }
            MMKV.defaultMMKV().encode("duplicateFileSize",allSize);


            double sizeMB = (double) allSize / (1024 * 1024);
            String formattedSize;


            if (sizeMB < 1) {
                // 如果大小小于1MB，则显示为KB
                double sizeKB = (double) allSize / 1024;
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                formattedSize = decimalFormat.format(sizeKB) + " KB";
            } else if (sizeMB < 1024){
                // 如果大小大于等于1MB，则显示为MB
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                formattedSize = decimalFormat.format(sizeMB) + " MB";
            }else {
                // 如果大小大于等于1GB，则显示为GB
                double sizeGB = (double) sizeMB / 1024;
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                formattedSize = decimalFormat.format(sizeGB) + " GB";
            }
            fanhui.put(chongfu,new FileInfo(formattedSize,bigFilePath,formatBigSize));
            return fanhui;
        }

        @Override
        protected void onPostExecute(Map<ArrayList<ZipFile>, FileInfo> duplicateFilePaths) {
            if (duplicateFilePaths != null && listener != null) {
                listener.onDuplicateFilesFound(duplicateFilePaths);
            }
        }


        private static ArrayList<File> getAllFilesInDirectory(String directoryPath) {
            ArrayList<File> files = new ArrayList<>();
            File directory = new File(directoryPath);
            File[] fileList = directory.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        files.add(file);
                    } else if (file.isDirectory()) {
                        files.addAll(getAllFilesInDirectory(file.getAbsolutePath()));
                    }
                }
            }
            return files;
        }
    }

    public interface DuplicateFileListener {
        void onDuplicateFilesFound(Map<ArrayList<ZipFile>, FileInfo> duplicateFilePaths);
    }

    public static String unitConversion(long allSize){
        double sizeMB = (double) allSize / (1024 * 1024);
        String formattedSize;


        if (sizeMB < 1) {
            // 如果大小小于1MB，则显示为KB
            double sizeKB = (double) allSize / 1024;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            formattedSize = decimalFormat.format(sizeKB) + " KB";
        } else if (sizeMB < 1024){
            // 如果大小大于等于1MB，则显示为MB
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            formattedSize = decimalFormat.format(sizeMB) + " MB";
        }else {
            // 如果大小大于等于1GB，则显示为GB
            double sizeGB = (double) sizeMB / 1024;
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            formattedSize = decimalFormat.format(sizeGB) + " GB";
        }
        return formattedSize;
    }
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
}
