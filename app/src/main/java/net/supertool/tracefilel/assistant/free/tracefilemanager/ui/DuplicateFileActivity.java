package net.supertool.tracefilel.assistant.free.tracefilemanager.ui;


import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.AudioStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.DocumentStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.ImageStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.JsonRecycleBinFiles;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.StatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.VideoStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.ZipStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.getFileExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.unitConversion;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.FilesStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.getExcludesList;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.moveRecycleBin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.AudioFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ImageFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.adapter.DuplicateFileAdapter;
import net.supertool.tracefilel.assistant.free.tracefilemanager.adapter.ImageAdapter;
import net.supertool.tracefilel.assistant.free.tracefilemanager.adapter.VideoAdapter;
import net.supertool.tracefilel.assistant.free.tracefilemanager.bean.FileInfo;
import net.supertool.tracefilel.assistant.free.tracefilemanager.myInterface.ItemClickListener;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.AudioScanTask;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.DocumentScanTask;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.ImageScanTask;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.LargeFileScanTask;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.VideoScanTask;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.ZipScanTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class DuplicateFileActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private DuplicateFileAdapter duplicateFileAdapter;
    private VideoAdapter videoAdapter;
    private int item_count=0, selectItemCount;
    private TextView text_title,text_count,fileSize,duplicateFileCount,duplicate_text,isEmpty;
    private boolean isFirst = true;
    private Button select_no,select_yes;
    private ImageView ic_back;
    private long allFileSize = 0;
    private String flag = "";
    private boolean isSelect = false,isReady = false;
    private String selectSize;
    private boolean DirectDeletion = false;
    private ProgressBar bigFile_progressBar;
    private ImageAdapter imageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_file_list);

        initUI();
        initData();
        initListener();

    }

    public void initUI(){
        recyclerView = findViewById(R.id.recyclerView);
        text_title = findViewById(R.id.text_title);
        select_no = findViewById(R.id.select_no);
        select_yes = findViewById(R.id.select_yes);
        text_count = findViewById(R.id.text_count);
        ic_back = findViewById(R.id.ic_back);
        fileSize = findViewById(R.id.fileSize);
        isEmpty = findViewById(R.id.isEmpty);
        duplicate_text = findViewById(R.id.duplicate_text);
        duplicateFileCount = findViewById(R.id.duplicateFileCount);
        bigFile_progressBar = findViewById(R.id.bigFile_progressBar);
    }
    @SuppressLint("SetTextI18n")
    public void initData(){
        String jsonFiles = null;
        flag = getIntent().getStringExtra("actFlag");
        if (isReady){
            isReady = false;
            flag = "bigFile";
        }

        if (flag==null){
            flag = "ChongFu";
        }
        switch (flag){
            case "documentFile":
                text_title.setText("DocumentFile");
                duplicate_text.setText("document File: ");
                if (DocumentStatusChange){
                    getDocumentFile();
                    DocumentStatusChange = false;
                }else {
                    String gs_documentPathList = MMKV.defaultMMKV().decodeString("Gs_documentPathList");
                    if (gs_documentPathList!=null){
                        ArrayList<ZipFile> zipFiles = new Gson().fromJson(gs_documentPathList,new TypeToken<ArrayList<ZipFile>>() {}.getType());
                        int size = zipFiles.size();
                        String documentFileSize;
                        if (size==0){
                            documentFileSize = "0KB";
                        }else {
                            allFileSize = MMKV.defaultMMKV().decodeLong("allDocumentFileSize");
                            documentFileSize = unitConversion(allFileSize);
                        }
                        duplicateFileCount.setText(String.valueOf(size));


                        fileSize.setText(documentFileSize);
                        startDuplicateFileAdapter(zipFiles);
                    }
                }


                break;
            case "largeFile":
                text_title.setText("Large File");
                duplicate_text.setText("Large File: ");
                startLargeFile();
                break;
            case "ChongFu":
                text_title.setText("Duplicate File");
                duplicate_text.setText("Duplicate File: ");
                allFileSize = MMKV.defaultMMKV().decodeLong("duplicateFileSize");
                jsonFiles = MMKV.defaultMMKV().decodeString("jsonChongFuFiles");
                String s = unitConversion(allFileSize);
                fileSize.setText(s);
                break;
            case "bigFile":
                text_title.setText("Large File");
                duplicate_text.setText("Large File: ");
                allFileSize = MMKV.defaultMMKV().decodeLong("bigFilesSize");

                jsonFiles = MMKV.defaultMMKV().decodeString("jsonBigFiles");
                String bigFileSize = unitConversion(allFileSize);
                fileSize.setText(bigFileSize);
                break;
            case "videoFile":
                text_title.setText("Video");
                duplicate_text.setText("Video File: ");
                if (VideoStatusChange){
                    getVideoFile();
                    VideoStatusChange = false;
                }else {

                    String gs_videoPathList = MMKV.defaultMMKV().decodeString("Gs_videoPathList");
                    if (gs_videoPathList!=null){
                        ArrayList<VideoFile> videoPathList = new Gson().fromJson(gs_videoPathList,new TypeToken<ArrayList<VideoFile>>() {}.getType());
                        int size = videoPathList.size();
                        duplicateFileCount.setText(String.valueOf(size));
                        String videoFileSize;
                        if (size==0){
                            videoFileSize = "0KB";
                        }else {
                            allFileSize = MMKV.defaultMMKV().decodeLong("allVideoFileSize");
                            videoFileSize = unitConversion(allFileSize);
                        }


                        fileSize.setText(videoFileSize);
                        startVideoAdapter(videoPathList);
                    }
                }

                break;
            case "imageFile":
                text_title.setText("Image");
                duplicate_text.setText("Image File: ");
                if (ImageStatusChange){
                    getImageFile();
                    ImageStatusChange = false;
                }else {

                    String gs_imagePathList = MMKV.defaultMMKV().decodeString("Gs_imagePathList");
                    if (gs_imagePathList!=null){
                        ArrayList<ImageFile> imageFiles = new Gson().fromJson(gs_imagePathList,new TypeToken<ArrayList<ImageFile>>() {}.getType());
                        int size = imageFiles.size();
                        String imageFileSize;
                        if (size==0){
                            imageFileSize = "0KB";
                        }else {
                            allFileSize = MMKV.defaultMMKV().decodeLong("allImageFileSize");
                            imageFileSize = unitConversion(allFileSize);
                        }
                        duplicateFileCount.setText(String.valueOf(size));


                        fileSize.setText(imageFileSize);
                        startImageAdapter(imageFiles);
                    }
                }
                break;
            case "audioFile":
                text_title.setText("Audio");
                duplicate_text.setText("Audio File: ");
                if (AudioStatusChange){
                    getAudioFile();
                    AudioStatusChange = false;
                }else {
                    String gs_audioPathList = MMKV.defaultMMKV().decodeString("Gs_audioPathList");
                    if (gs_audioPathList!=null){
                        ArrayList<ZipFile> audioFiles = new Gson().fromJson(gs_audioPathList,new TypeToken<ArrayList<ZipFile>>() {}.getType());
                        int size = audioFiles.size();
                        String audioFileSize;
                        if (size==0){
                            audioFileSize = "0KB";
                        }else {
                            allFileSize = MMKV.defaultMMKV().decodeLong("allAudioFileSize");
                            audioFileSize = unitConversion(allFileSize);
                        }
                        duplicateFileCount.setText(String.valueOf(size));


                        fileSize.setText(audioFileSize);
                        startDuplicateFileAdapter(audioFiles);
                    }
                }
                break;
            case "zipFile":
                text_title.setText("ZipFile");
                duplicate_text.setText("Zip File: ");


                if (ZipStatusChange){
                    getZipFile();
                    ZipStatusChange = false;
                }else {
                    String gs_zipPathList = MMKV.defaultMMKV().decodeString("Gs_zipPathList");
                    if (gs_zipPathList!=null){
                        ArrayList<ZipFile> zipFiles = new Gson().fromJson(gs_zipPathList,new TypeToken<ArrayList<ZipFile>>() {}.getType());
                        int size = zipFiles.size();
                        String zipFileSize;
                        if (size==0){
                            zipFileSize = "0KB";
                        }else {
                            allFileSize = MMKV.defaultMMKV().decodeLong("allZipFileSize");
                            zipFileSize = unitConversion(allFileSize);
                        }
                        duplicateFileCount.setText(String.valueOf(size));


                        fileSize.setText(zipFileSize);
                        startDuplicateFileAdapter(zipFiles);
                    }
                }
                break;

        }





        if (jsonFiles!=null){
            ArrayList<ZipFile> zipItems = new Gson().fromJson(jsonFiles,new TypeToken<ArrayList<ZipFile>>() {}.getType());
            int size = zipItems.size();
            duplicateFileCount.setText(String.valueOf(size));

            startDuplicateFileAdapter(zipItems);
        }
    }
    public void initListener(){
        ic_back.setOnClickListener(this);
        select_yes.setOnClickListener(this);
    }

    public void getDocumentFile(){
        bigFile_progressBar.setVisibility(View.VISIBLE);
        long startTime = System.currentTimeMillis();
        new DocumentScanTask(this, new DocumentScanTask.OnVideoScanListener() {
            @Override
            public void onVideoScanComplete(ArrayList<ZipFile> documentFiles) {
                System.out.println("读取文档列表时间："+(System.currentTimeMillis()-startTime));
                String Gs_documentPathList = new Gson().toJson(documentFiles);
                MMKV.defaultMMKV().encode("Gs_documentPathList",Gs_documentPathList);
                int size = documentFiles.size();
                String documentFileSize;
                if (size==0){
                    documentFileSize="0KB";
                }else {
                    allFileSize = MMKV.defaultMMKV().decodeLong("allDocumentFileSize");
                    documentFileSize = unitConversion(allFileSize);
                }
                duplicateFileCount.setText(String.valueOf(size));


                fileSize.setText(documentFileSize);
                startDuplicateFileAdapter(documentFiles);

                bigFile_progressBar.setVisibility(View.GONE);
            }
        }).execute();

    }

    public void startLargeFile(){
        bigFile_progressBar.setVisibility(View.VISIBLE);
        String gs_allPathList = MMKV.defaultMMKV().decodeString("Gs_allPathList");
        if (gs_allPathList!=null){
            ArrayList<ZipFile> allList = new Gson().fromJson(gs_allPathList,new TypeToken<ArrayList<ZipFile>>() {}.getType());

            new LargeFileScanTask(this, allList, new LargeFileScanTask.OnLargeFileScanListener() {
                @Override
                public void onLargeFileScanComplete(ArrayList<ZipFile> zipFiles) {
                    bigFile_progressBar.setVisibility(View.GONE);

                    MMKV.defaultMMKV().encode("jsonBigFiles",new Gson().toJson(zipFiles));

                    isReady = true;
                    initData();
                }
            }).execute();




        }else {
            System.out.println("获取到的文件为空！！！！");
        }
    }
    public void getZipFile(){
        bigFile_progressBar.setVisibility(View.VISIBLE);
        long startTime = System.currentTimeMillis();

        new ZipScanTask(this, new ZipScanTask.OnVideoScanListener() {
            @Override
            public void onVideoScanComplete(ArrayList<ZipFile> zipFiles) {
                System.out.println("读取压缩包列表时间："+(System.currentTimeMillis()-startTime));
                String Gs_zipFiles = new Gson().toJson(zipFiles);
                MMKV.defaultMMKV().encode("Gs_zipPathList",Gs_zipFiles);
                int size = zipFiles.size();
                String audioFileSize;
                if (size==0){
                    audioFileSize="0KB";
                }else {
                    allFileSize = MMKV.defaultMMKV().decodeLong("allZipFileSize");
                    audioFileSize = unitConversion(allFileSize);
                }

                duplicateFileCount.setText(String.valueOf(size));


                fileSize.setText(audioFileSize);
                startDuplicateFileAdapter(zipFiles);

                bigFile_progressBar.setVisibility(View.GONE);
            }
        }).execute();


    }

    public void getAudioFile(){
        bigFile_progressBar.setVisibility(View.VISIBLE);
        long startTime = System.currentTimeMillis();
        new AudioScanTask(this, new AudioScanTask.OnVideoScanListener() {
            @Override
            public void onVideoScanComplete(ArrayList<ZipFile> audioPathList) {
                System.out.println("读取音频列表时间："+(System.currentTimeMillis()-startTime));
                String Gs_audioPathList = new Gson().toJson(audioPathList);
                MMKV.defaultMMKV().encode("Gs_audioPathList",Gs_audioPathList);
                int size = audioPathList.size();
                String audioFileSize;
                if (size==0){
                    audioFileSize="0KB";
                }else {
                    allFileSize = MMKV.defaultMMKV().decodeLong("allAudioFileSize");
                    audioFileSize = unitConversion(allFileSize);
                }

                duplicateFileCount.setText(String.valueOf(size));


                fileSize.setText(audioFileSize);
                startDuplicateFileAdapter(audioPathList);

                bigFile_progressBar.setVisibility(View.GONE);
            }
        }).execute();

    }

    public void getImageFile(){
        bigFile_progressBar.setVisibility(View.VISIBLE);
        long startTime = System.currentTimeMillis();
        new ImageScanTask(this, new ImageScanTask.OnVideoScanListener() {
            @Override
            public void onVideoScanComplete(ArrayList<ImageFile> imageFiles) {
                System.out.println("读取图片列表时间："+(System.currentTimeMillis()-startTime));
                String Gs_imageFiles = new Gson().toJson(imageFiles);
                MMKV.defaultMMKV().encode("Gs_imagePathList",Gs_imageFiles);
                int size = imageFiles.size();
                String videoFileSize;
                if (size==0){
                    videoFileSize="0KB";
                }else {
                    allFileSize = MMKV.defaultMMKV().decodeLong("allImageFileSize");
                    videoFileSize = unitConversion(allFileSize);
                }
                duplicateFileCount.setText(String.valueOf(size));


                fileSize.setText(videoFileSize);
                startImageAdapter(imageFiles);

                bigFile_progressBar.setVisibility(View.GONE);
            }
        }).execute();

    }

    public void getVideoFile(){
        bigFile_progressBar.setVisibility(View.VISIBLE);
        long startTime = System.currentTimeMillis();
        new VideoScanTask(this, new VideoScanTask.OnVideoScanListener() {
            @Override
            public void onVideoScanComplete(ArrayList<VideoFile> videoPathList) {
                System.out.println("读取视频列表时间："+(System.currentTimeMillis()-startTime));
                String Gs_videoPathList = new Gson().toJson(videoPathList);
                MMKV.defaultMMKV().encode("Gs_videoPathList",Gs_videoPathList);
                int size = videoPathList.size();
                String videoFileSize;
                if (size==0){
                    videoFileSize="0KB";
                }else {
                    allFileSize = MMKV.defaultMMKV().decodeLong("allVideoFileSize");
                    videoFileSize = unitConversion(allFileSize);
                }
                duplicateFileCount.setText(String.valueOf(size));


                fileSize.setText(videoFileSize);
                startVideoAdapter(videoPathList);

                bigFile_progressBar.setVisibility(View.GONE);
            }
        }).execute();
    }
   /* public void startAudioFileAdapter(ArrayList<AudioFile> list){
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        duplicateFileAdapter = new DuplicateFileAdapter(this, list, new ItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick() {
                selectItemCount = duplicateFileAdapter.getSelect_count();
                text_count.setText(selectItemCount+" Item");
                selectSize = duplicateFileAdapter.getSelectSize();
                select_yes.setText(selectSize);
                if (isFirst){
                    isFirst = false;

                    Delete();
                }

            }

            @Override
            public void Deselect() {
                cancelDelete();
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(duplicateFileAdapter);

    }*/

    public void startImageAdapter(ArrayList<ImageFile> list){
        if (list.isEmpty()){
            isEmpty.setVisibility(View.VISIBLE);
        }else {
            isEmpty.setVisibility(View.GONE);
        }
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);

        imageAdapter = new ImageAdapter(this, list, new ImageAdapter.ItemClickListener() {
            @Override
            public void onClick() {
                selectItemCount = imageAdapter.getSelect_count();
                text_count.setText(selectItemCount+" Item");
                selectSize = imageAdapter.getSelectSize();
                select_yes.setText(selectSize);
                if (isFirst){
                    isFirst = false;
                    Delete();
                }
            }

            @Override
            public void Deselect() {
                cancelDelete();
            }
        });


        recyclerView.setAdapter(imageAdapter);
    }

    public void startVideoAdapter(ArrayList<VideoFile> list){
        if (list.isEmpty()){
            isEmpty.setVisibility(View.VISIBLE);
        }else {
            isEmpty.setVisibility(View.GONE);
        }
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        videoAdapter = new VideoAdapter(this, list, new VideoAdapter.ItemClickListener() {
            @Override
            public void onClick() {
                selectItemCount = videoAdapter.getSelect_count();
                text_count.setText(selectItemCount+" Item");
                selectSize = videoAdapter.getSelectSize();
                select_yes.setText(selectSize);
                if (isFirst){
                    isFirst = false;
                    Delete();
                }
            }

            @Override
            public void Deselect() {
                cancelDelete();
            }
        });

        recyclerView.setAdapter(videoAdapter);
    }

    public void startDuplicateFileAdapter(ArrayList<ZipFile> list){
        if (list.isEmpty()){
            isEmpty.setVisibility(View.VISIBLE);
        }else {
            isEmpty.setVisibility(View.GONE);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        duplicateFileAdapter = new DuplicateFileAdapter(this, list, new ItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick() {
                selectItemCount = duplicateFileAdapter.getSelect_count();
                text_count.setText(selectItemCount+" Item");
                selectSize = duplicateFileAdapter.getSelectSize();
                select_yes.setText(selectSize);
                if (isFirst){
                    isFirst = false;

                    Delete();
                }

            }

            @Override
            public void Deselect() {
                cancelDelete();
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(duplicateFileAdapter);

    }

    public void Delete(){

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        text_count.startAnimation(fadeIn);

        select_no.setVisibility(View.GONE);
        select_yes.setVisibility(View.VISIBLE);
        text_count.setVisibility(View.VISIBLE);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);

        text_title.startAnimation(fadeOut);
        text_title.setVisibility(View.GONE);
    }

    public void cancelDelete(){
        isFirst = true;
        String s = unitConversion(allFileSize);
        fileSize.setText(s);
        int itemCount=0;
        switch (flag){
            case "largeFile":
            case "documentFile":
            case "zipFile":
            case "audioFile":
            case "ChongFu":
            case "bigFile":
                itemCount = duplicateFileAdapter.getItemCount();
                break;
            case "videoFile":
                itemCount = videoAdapter.getItemCount();
                break;
            case "imageFile":
                itemCount = imageAdapter.getItemCount();
                break;
        }

        duplicateFileCount.setText(String.valueOf(itemCount));

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);

        text_count.startAnimation(fadeOut);

        text_count.setVisibility(View.GONE);
        select_no.setVisibility(View.VISIBLE);
        select_yes.setVisibility(View.GONE);
        showTitle();
    }

    public void showTitle(){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        text_title.startAnimation(fadeIn);
        text_title.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ic_back){
            finish();
        }
        if (v.getId() == R.id.select_yes){
            //ZipDelete();
            //MoveToRecycleBin();
            startSelectDeleteDialog();
        }
    }

    public void check(){



    }

    //视频删除操作
    public void deleteVideoFile(){
        ArrayList<VideoFile> selectFileBeans = videoAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < selectFileBeans.size(); i++) {

            if (selectFileBeans.get(i).isSelect()) {
                integers.add(i);
                if (DirectDeletion){
                    File file = new File(selectFileBeans.get(i).getPath());
                    if (file.exists()&&file.isFile()){
                        if (file.delete()){
                            Log.i("delete","文件已删除:"+selectFileBeans.get(i).getPath());
                        }else {
                            Log.i("delete","文件删除失败:"+selectFileBeans.get(i).getPath());
                        }

                    }else {
                        Log.i("delete","不是一个文件:"+selectFileBeans.get(i).getPath());
                    }
                }else {
                    moveRecycleBin(selectFileBeans.get(i).getPath());
                }

                allFileSize-=selectFileBeans.get(i).getVideoLongSize();
            }
        }

        for (int i = 0; i < integers.size(); i++) {
            if (i!=0){
                videoAdapter.NotificationListUpdate(integers.get(i)-i);
            }else {
                videoAdapter.NotificationListUpdate(integers.get(i));
            }

        }
        FilesStatusChange();
        cancelDelete();
        getExcludesList();
    }

    public void deleteImageFile(){
        ArrayList<ImageFile> selectFileBeans = imageAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < selectFileBeans.size(); i++) {

            if (selectFileBeans.get(i).isSelect()) {
                integers.add(i);
                if (DirectDeletion){
                    File file = new File(selectFileBeans.get(i).getPath());
                    if (file.exists()&&file.isFile()){
                        if (file.delete()){
                            Log.i("delete","文件已删除:"+selectFileBeans.get(i).getPath());
                        }else {
                            Log.i("delete","文件删除失败:"+selectFileBeans.get(i).getPath());
                        }

                    }else {
                        Log.i("delete","不是一个文件:"+selectFileBeans.get(i).getPath());
                    }
                }else {
                    moveRecycleBin(selectFileBeans.get(i).getPath());
                }

                allFileSize-=selectFileBeans.get(i).getImageLongSize();
            }
        }

        for (int i = 0; i < integers.size(); i++) {
            if (i!=0){
                imageAdapter.NotificationListUpdate(integers.get(i)-i);
            }else {
                imageAdapter.NotificationListUpdate(integers.get(i));
            }

        }
        FilesStatusChange();
        cancelDelete();
        getExcludesList();
    }
    //大文件和重复文件删除操作
    public void deleteFile(){

        ArrayList<ZipFile> selectFileBeans = duplicateFileAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < selectFileBeans.size(); i++) {

            if (selectFileBeans.get(i).getSelect()) {
                integers.add(i);
                if (DirectDeletion){
                    File file = new File(selectFileBeans.get(i).getPath());
                    if (file.exists()&&file.isFile()){
                        if (file.delete()){
                            Log.i("delete","文件已删除:"+selectFileBeans.get(i).getPath());
                        }else {
                            Log.i("delete","文件删除失败:"+selectFileBeans.get(i).getPath());
                        }

                    }else {
                        Log.i("delete","不是一个文件:"+selectFileBeans.get(i).getPath());
                    }
                }else {
                    moveRecycleBin(selectFileBeans.get(i).getPath());
                }

                allFileSize-=selectFileBeans.get(i).getFileSize();
            }
        }

        for (int i = 0; i < integers.size(); i++) {
            if (i!=0){
                duplicateFileAdapter.myUpdate(integers.get(i)-i);
            }else {
                duplicateFileAdapter.myUpdate(integers.get(i));
            }

        }
        FilesStatusChange();
        cancelDelete();
        getExcludesList();
    }
    public void updateFileSize(){
        String s = unitConversion(allFileSize);
        fileSize.setText(s);
    }

    public void MoveToRecycleBin(){
        ArrayList<ZipFile> selectZipBeans = duplicateFileAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();

        for (int i = 0; i < selectZipBeans.size(); i++) {
            if (selectZipBeans.get(i).getSelect()){
                integers.add(i);
                File file = new File(selectZipBeans.get(i).getPath());
                if (file.exists()&&file.isFile()){
                    if (file.delete()){
                        Log.i("delete","文件已删除:"+selectZipBeans.get(i).getPath());
                    }else {
                        Log.i("delete","文件删除失败:"+selectZipBeans.get(i).getPath());
                    }

                }else {
                    Log.i("delete","不是一个文件:"+selectZipBeans.get(i).getPath());
                }
            }
        }

        for (int i = 0; i < integers.size(); i++) {
            if (i!=0){
                duplicateFileAdapter.myUpdate(integers.get(i)-i);
            }else {
                duplicateFileAdapter.myUpdate(integers.get(i));
            }

        }

        cancelDelete();

    }

    public void ZipDelete(){
        ArrayList<ZipFile> selectZipBeans = duplicateFileAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();

        for (int i = 0; i < selectZipBeans.size(); i++) {
            if (selectZipBeans.get(i).getSelect()){
                integers.add(i);
                File file = new File(selectZipBeans.get(i).getPath());
                if (file.exists()&&file.isFile()){

                    String removedName = file.length() + "_" + getFileExtension(file.getName());

                    if (i == 0) {
                        // 如果要移除的项是第一个项，只判断后面两项
                        ZipFile next = selectZipBeans.get(i + 1);
                        String nextName = next.getFileSize() + "_" +getFileExtension(next.getName());
                        if (removedName.equals(nextName)){
                            //判断是否只有两项
                            if (selectZipBeans.size()>2){
                                ZipFile nex2 = selectZipBeans.get(i + 2);
                                String nextName2 = nex2.getFileSize() + "_" +getFileExtension(nex2.getName());
                                if (removedName.equals(nextName2)){
                                    //三项相同
                                }else {
                                    //两项相同
                                    integers.add(i+1);
                                }
                            }
                            //只有两项
                            integers.add(i+1);

                        }
                    }else if (i == selectZipBeans.size() - 1) {
                        // 如果要移除的项是最后一个项只判断前面两项
                        ZipFile previous = selectZipBeans.get(i - 1);
                        String previousName = previous.getFileSize() + "_" +getFileExtension(previous.getName());
                        if (removedName.equals(previousName)) {
                            //判断是否只有两项
                            if (selectZipBeans.size()>2){
                                ZipFile previous2 = selectZipBeans.get(i + 2);
                                String previousName2 = previous2.getFileSize() + "_" + getFileExtension(previous2.getName());
                                if (removedName.equals(previousName2)) {
                                    //三项相同
                                } else {
                                    //两项相同
                                    integers.add(i-1);
                                }
                            }
                            integers.add(i-1);
                        }

                    }else {
                        ZipFile previous = selectZipBeans.get(i - 1);
                        ZipFile next = selectZipBeans.get(i + 1);

                        String previousName = previous.getFileSize() + "_" +getFileExtension(previous.getName());
                        String nextName = next.getFileSize() + "_" +getFileExtension(next.getName());

                        if (removedName.equals(previousName) && !removedName.equals(nextName)) {
                            if (i-1 == 0) {
                                System.out.println("前一项相同且到顶");
                                // 如果要移除的项是第二个项,到顶了
                                //两项相同
                                integers.add(0);

                            }else {
                                ZipFile previous2 = selectZipBeans.get(i - 2);
                                String previousName2 = previous2.getFileSize() + "_" +getFileExtension(previous2.getName());
                                if (removedName.equals(previousName2)){
                                    //三项相同
                                    System.out.println("前两项相同");

                                }else {
                                    //两项相同
                                    System.out.println("前一项相同");
                                    integers.add(i-1);
                                }

                            }
                            // 如果removeItem前一项与removeItem的名称相同，而后一项与removeItem的名称不同

                        }else if (!removedName.equals(previousName) && removedName.equals(nextName)) {
                            // 如果removeItem前一项与removeItem的名称不同，而后一项与removeItem的名称相同
                            if (i+1 == selectZipBeans.size() - 1) {
                                // 如果要移除的项是倒数第二项，到底了
                                //两项相同
                                System.out.println("后一项相同且到底");
                                integers.add(i+1);
                            }else {
                                ZipFile next2 = selectZipBeans.get(i + 2);
                                String nextName2 = next2.getFileSize() + "_" +getFileExtension(next2.getName());
                                if (removedName.equals(nextName2)){
                                    //三项相同
                                    System.out.println("后两项相同");
                                }else {
                                    //两项相同
                                    integers.add(i+1);
                                    System.out.println("后一项相同");
                                }

                            }

                        }

                    }


/*

                    if (i+1<selectZipBeans.size()){
                        String s1 = selectZipBeans.get(i + 1).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i + 1).getName());
                        if (key.equals(s1)){
                            if (i+2<selectZipBeans.size()){
                                String sAdd1 = selectZipBeans.get(i + 2).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i + 2).getName());
                                if (key.equals(sAdd1)){

                                }else {
                                    if (i!=0){
                                        String s2 = selectZipBeans.get(i - 1).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 1).getName());
                                        if (key.equals(s2)){
                                            if ((i-1)!=0){
                                                String sSub2 = selectZipBeans.get(i - 2).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 2).getName());
                                                if (key.equals(sSub2)){

                                                }
                                            }
                                        }
                                    }
                                }
                            }else {
                                if (i!=0){
                                    String s2 = selectZipBeans.get(i - 1).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 1).getName());
                                    if (key.equals(s2)){
                                        if ((i-1)!=0){
                                            String sSub2 = selectZipBeans.get(i - 2).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 2).getName());
                                            if (key.equals(sSub2)){

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (i!=0){
                            String s2 = selectZipBeans.get(i - 1).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 1).getName());
                            if (key.equals(s2)){
                                if ((i-1)!=0){
                                    String sSub2 = selectZipBeans.get(i - 2).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 2).getName());
                                    if (key.equals(sSub2)){

                                    }
                                }
                            }
                        }


                    }else {
                        String s2 = selectZipBeans.get(i - 1).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 1).getName());
                        if (key.equals(s2)){
                            if ((i-1)!=0){
                                String sSub2 = selectZipBeans.get(i - 2).getFileSize() + "_" + getFileExtension(selectZipBeans.get(i - 2).getName());
                                if (key.equals(sSub2)){

                                }
                            }
                        }
                    }
*/

                    if (file.delete()){
                        Log.i("delete","文件已删除:"+selectZipBeans.get(i).getPath());
                        long fileSize = selectZipBeans.get(i).getFileSize();
                        allFileSize -= fileSize;

                    }else {
                        Log.i("delete","文件删除失败:"+selectZipBeans.get(i).getPath());
                    }

                }else {
                    Log.i("delete","不是一个文件:"+selectZipBeans.get(i).getPath());
                }
            }
        }

        for (int i = 0; i < integers.size(); i++) {
            /*if (i!=0){
                duplicateFileAdapter.myUpdate(integers.get(i)-i);
            }else */{
                duplicateFileAdapter.myUpdate(integers.get(i));
            }

        }

        cancelDelete();

    }

    @SuppressLint("SetTextI18n")
    public void startSelectDeleteDialog() {
        isSelect = false;
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_hint);
        dialog.getWindow().setBackgroundDrawableResource(R.color.my_colors);
        dialog.setCancelable(false);
        ImageView select = (ImageView) dialog.findViewById(R.id.ic_select);
        TextView folder_count = (TextView) dialog.findViewById(R.id.folder_count);
        TextView file_count = (TextView) dialog.findViewById(R.id.file_count);
        TextView allSize = (TextView) dialog.findViewById(R.id.allSize);
        TextView dialog_content = (TextView) dialog.findViewById(R.id.dialog_content);
        folder_count.setText("Folder: "+ 0);
        file_count.setText("File: "+ selectItemCount);
        allSize.setText("Size: "+ selectSize);


        //  ((TextView)dialog.findViewById(R.id.rubbishSize)).setText(rubbish);
        dialog.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelect){
                    isSelect = false;
                    select.setImageResource(R.drawable.ic_select_no);
                    dialog_content.setText(getString(R.string.dialog_content_recycle));
                }else {
                    isSelect = true;
                    select.setImageResource(R.drawable.ic_select_yes);
                    dialog_content.setText(getString(R.string.dialog_content_delete));

                }
            }
        });
        dialog.findViewById(R.id.dialog_Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelect) {
                    DirectDeletion = true;
                }else {
                    DirectDeletion = false;
                }
                switch (flag){
                    case "largeFile":
                    case "documentFile":
                    case "zipFile":
                    case "ChongFu":
                    case "bigFile":
                    case "audioFile":
                        deleteFile();
                        break;
                    case "videoFile":
                        deleteVideoFile();
                        break;

                    case "imageFile":
                        deleteImageFile();
                        break;
                }

                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialog_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}