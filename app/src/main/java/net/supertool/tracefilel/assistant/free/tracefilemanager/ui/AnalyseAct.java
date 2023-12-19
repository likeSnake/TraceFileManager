package net.supertool.tracefilel.assistant.free.tracefilemanager.ui;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.StatusChange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.adapter.PreviewAdapter;
import net.supertool.tracefilel.assistant.free.tracefilemanager.bean.FileInfo;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyseAct extends AppCompatActivity implements FileManager.DuplicateFileListener ,View.OnClickListener{
    private Map<String, List<File>> duplicateFilesMap = new HashMap<>();
    private RecyclerView preview_recyclerView,bigFile_recyclerView;
    private PreviewAdapter previewAdapter;
    private TextView file_size,big_file_size,isBigFileEmpty,isPreviewEmpty;
    private Button bigFile_more,bt_more;
    private ImageView ic_back;
    private ProgressBar preview_progressBar,bigFile_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_analyse);
        initUI();
       // initData();
        initListener();
    }
    public void initUI(){
        preview_recyclerView = findViewById(R.id.preview_recyclerView);
        file_size = findViewById(R.id.file_size);
        bigFile_recyclerView = findViewById(R.id.bigFile_recyclerView);
        big_file_size = findViewById(R.id.big_file_size);
        bigFile_more = findViewById(R.id.bigFile_more);
        bt_more = findViewById(R.id.bt_more);
        ic_back = findViewById(R.id.ic_back);
        isBigFileEmpty = findViewById(R.id.isBigFileEmpty);
        isPreviewEmpty = findViewById(R.id.isPreviewEmpty);
        preview_progressBar = findViewById(R.id.preview_progressBar);
        bigFile_progressBar = findViewById(R.id.bigFile_progressBar);
    }
    public void initData(){
        String gs_allPathList = MMKV.defaultMMKV().decodeString("Gs_allPathList");
        if (gs_allPathList!=null){
            ArrayList<ZipFile> allList = new Gson().fromJson(gs_allPathList,new TypeToken<ArrayList<ZipFile>>() {}.getType());

            File storageDir = Environment.getExternalStorageDirectory();
            FileManager fileManager = new FileManager();
            // 启动查询重复文件的任务

            preview_progressBar.setVisibility(View.VISIBLE);
            bigFile_progressBar.setVisibility(View.VISIBLE);
            preview_recyclerView.setVisibility(View.GONE);
            bigFile_recyclerView.setVisibility(View.GONE);

            fileManager.findDuplicateFiles(allList, this);
        }else {
            System.out.println("获取到的文件为空！！！！");
        }
    }

    public void startAdapter(ArrayList<ZipFile> list){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        previewAdapter = new PreviewAdapter(this,list);
        preview_recyclerView.setLayoutManager(layoutManager);
        preview_recyclerView.setAdapter(previewAdapter);

    }

    public void startBigFileAdapter(ArrayList<ZipFile> list){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        previewAdapter = new PreviewAdapter(this,list);
        bigFile_recyclerView.setLayoutManager(layoutManager);
        bigFile_recyclerView.setAdapter(previewAdapter);

    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
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
    public String calculateMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
        }
        fis.close();
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public void initListener(){
        bt_more.setOnClickListener(this);
        bigFile_more.setOnClickListener(this);
        ic_back.setOnClickListener(this);
    }

    @Override
    public void onDuplicateFilesFound(Map<ArrayList<ZipFile>, FileInfo> files) {

        runOnUiThread(()->{
            ArrayList<ZipFile> key = new ArrayList<>();
            FileInfo fileInfo = new FileInfo();
            for (Map.Entry<ArrayList<ZipFile>,FileInfo> entry : files.entrySet()) {
                key = entry.getKey();
                fileInfo = entry.getValue();
            }


            String formattedSize = fileInfo.getFormattedSize();
            String bigFileSize = fileInfo.getBigFileSize();
            ArrayList<ZipFile> bigFilePath = fileInfo.getBigFilePath();

            String jsonBigFiles = new Gson().toJson(bigFilePath);
            String jsonChongFuFiles = new Gson().toJson(key);

            MMKV.defaultMMKV().encode("jsonBigFiles",jsonBigFiles);
            MMKV.defaultMMKV().encode("jsonChongFuFiles",jsonChongFuFiles);

            MMKV.defaultMMKV().encode("formattedSize",formattedSize);
            MMKV.defaultMMKV().encode("bigFileSize",bigFileSize);

            preview_progressBar.setVisibility(View.GONE);
            bigFile_progressBar.setVisibility(View.GONE);
            preview_recyclerView.setVisibility(View.VISIBLE);
            bigFile_recyclerView.setVisibility(View.VISIBLE);

            if (bigFilePath.isEmpty()){
                bigFile_more.setVisibility(View.GONE);
                isBigFileEmpty.setVisibility(View.VISIBLE);
                bigFile_recyclerView.setVisibility(View.GONE);
                big_file_size.setText("0 B");
            }else {
                bigFile_recyclerView.setVisibility(View.VISIBLE);
                isBigFileEmpty.setVisibility(View.GONE);
                bigFile_more.setVisibility(View.VISIBLE);
                big_file_size.setText(bigFileSize);
                startBigFileAdapter(bigFilePath);
            }
            if (key.isEmpty()){
                preview_recyclerView.setVisibility(View.GONE);
                isPreviewEmpty.setVisibility(View.VISIBLE);
                bt_more.setVisibility(View.GONE);
                file_size.setText("0 B");
            }else {
                preview_recyclerView.setVisibility(View.VISIBLE);
                isPreviewEmpty.setVisibility(View.GONE);
                bt_more.setVisibility(View.VISIBLE);
                file_size.setText(formattedSize);
                startAdapter(key);
            }


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StatusChange){
            initData();
            StatusChange = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatusChange = true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_more){
            Intent intent = new Intent(AnalyseAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","ChongFu");
            startActivity(intent);
        }

        if (v.getId()==R.id.bigFile_more){
            Intent intent = new Intent(AnalyseAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","bigFile");
            startActivity(intent);
        }
        if (v.getId()==R.id.ic_back){
            finish();
        }
    }
}