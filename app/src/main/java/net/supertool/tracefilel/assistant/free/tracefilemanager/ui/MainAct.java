package net.supertool.tracefilel.assistant.free.tracefilemanager.ui;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.getExcludesList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ImageFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.FilesUtils;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.VideoScanTask;

import java.util.ArrayList;

public class MainAct extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout layout_analyse,layout_download,layout_video, layout_audio,layout_picture,layout_largeFile,layout_zip,layout_recycle,layout_document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        initUI();
        initData();
        initListener();
    }
    public void initUI(){
        layout_analyse = findViewById(R.id.layout_analyse);
        layout_download = findViewById(R.id.layout_download);
        layout_recycle = findViewById(R.id.layout_recycle);
        layout_video = findViewById(R.id.layout_video);
        layout_picture = findViewById(R.id.layout_picture);
        layout_audio = findViewById(R.id.layout_audio);
        layout_zip = findViewById(R.id.layout_zip);
        layout_largeFile = findViewById(R.id.layout_largeFile);
        layout_document = findViewById(R.id.layout_document);
    }
    public void initData(){

        long startTime = System.currentTimeMillis();

        FilesUtils.getMediaCountsAsync(this, new FilesUtils.MediaCountListener() {
            @Override
            public void onMediaCountObtained(ArrayList<ImageFile> imagePathList, ArrayList<VideoFile> videoPathList, ArrayList<ZipFile> zipPathList,ArrayList<ZipFile> allPathList) {
                System.out.println("所有文件获取时长："+(System.currentTimeMillis()-startTime));
                int imageCount = imagePathList.size();
                int videoCount = videoPathList.size();
                int compressedFileCount = zipPathList.size();

                String Gs_imagePathList = new Gson().toJson(imagePathList);
                String Gs_videoPathList = new Gson().toJson(videoPathList);
                String Gs_zipPathList = new Gson().toJson(zipPathList);
                String Gs_allPathList = new Gson().toJson(allPathList);

                MMKV.defaultMMKV().encode("Gs_imagePathList",Gs_imagePathList);
                MMKV.defaultMMKV().encode("Gs_videoPathList",Gs_videoPathList);
                MMKV.defaultMMKV().encode("Gs_zipPathList",Gs_zipPathList);
                MMKV.defaultMMKV().encode("Gs_allPathList",Gs_allPathList);


            }
        });
    }
    public void initListener(){
        layout_analyse.setOnClickListener(this);
        layout_download.setOnClickListener(this);
        layout_recycle.setOnClickListener(this);
        layout_video.setOnClickListener(this);
        layout_picture.setOnClickListener(this);
        layout_audio.setOnClickListener(this);
        layout_zip.setOnClickListener(this);
        layout_largeFile.setOnClickListener(this);
        layout_document.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_analyse){
            startActivity(new Intent(MainAct.this,AnalyseAct.class));
        }
        if (v.getId() == R.id.layout_download){
            Intent intent = new Intent(MainAct.this, DownloadAct.class);
            intent.putExtra("actFlag","downloadAct");
            startActivity(intent);

        }
        if (v.getId() == R.id.layout_video){
            Intent intent = new Intent(MainAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","videoFile");
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_audio){
            Intent intent = new Intent(MainAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","audioFile");
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_picture){
            Intent intent = new Intent(MainAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","imageFile");
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_largeFile){
            Intent intent = new Intent(MainAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","largeFile");
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_zip){
            Intent intent = new Intent(MainAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","zipFile");
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_recycle){
            Intent intent = new Intent(MainAct.this, DownloadAct.class);
            intent.putExtra("actFlag","recycleBin");
            startActivity(intent);
        }
        if (v.getId() == R.id.layout_document){
            Intent intent = new Intent(MainAct.this, DuplicateFileActivity.class);
            intent.putExtra("actFlag","documentFile");
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getExcludesList();

    }
}