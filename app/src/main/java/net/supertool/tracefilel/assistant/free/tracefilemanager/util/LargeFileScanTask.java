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

public class LargeFileScanTask extends AsyncTask<Void, Void, ArrayList<ZipFile>> {
    private Context context;
    private ArrayList<String> pathsToExclude;
    private OnLargeFileScanListener listener;
    private ArrayList<ZipFile> allList;

    public LargeFileScanTask(Context context, ArrayList<ZipFile> allList,OnLargeFileScanListener listener) {
        this.context = context;
        this.allList = allList;
        this.listener = listener;
    }

    @Override
    protected ArrayList<ZipFile> doInBackground(Void... params) {
        ArrayList<ZipFile> bigFilePath = new ArrayList<>();
        System.out.println("开始执行");
        String decodeString = MMKV.defaultMMKV().decodeString(RecoveredFilePath);
        if (decodeString!=null){
            pathsToExclude = new Gson().fromJson(decodeString,new TypeToken<ArrayList<String>>(){}.getType());
        }else {
            pathsToExclude = new ArrayList<>();
        }

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
            }


        }
        MMKV.defaultMMKV().encode("bigFilesSize",bigFileSize);

        return bigFilePath;
    }

    @Override
    protected void onPostExecute(ArrayList<ZipFile> zipFiles) {

        if (listener != null) {
            System.out.println("返回监听");
            listener.onLargeFileScanComplete(zipFiles);
        }
    }

    public interface OnLargeFileScanListener {
        void onLargeFileScanComplete(ArrayList<ZipFile> zipFiles);
    }
}
