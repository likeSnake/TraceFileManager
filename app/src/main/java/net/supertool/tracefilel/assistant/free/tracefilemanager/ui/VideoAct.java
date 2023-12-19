package net.supertool.tracefilel.assistant.free.tracefilemanager.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.adapter.VideoAdapter;

import java.util.ArrayList;

public class VideoAct extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<VideoFile> videoFiles = new ArrayList<>();
    private RecyclerView video_recyclerView;
    private VideoAdapter videoAdapter;
    private ImageView ic_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video);

        initUI();
        initData();
        initListener();
    }
    public void initUI(){
        video_recyclerView = findViewById(R.id.video_recyclerView);
        ic_back = findViewById(R.id.ic_back);
    }
    public void initData(){
        String gs_videoPathList = MMKV.defaultMMKV().decodeString("Gs_videoPathList");
        if (gs_videoPathList!=null){
            videoFiles = new Gson().fromJson(gs_videoPathList,new TypeToken<ArrayList<VideoFile>>(){}.getType());
            startVideoAdapter(videoFiles);
        }
    }
    public void initListener(){}

    public void startVideoAdapter(ArrayList<VideoFile> list){

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        video_recyclerView.setLayoutManager(manager);
        videoAdapter = new VideoAdapter(this, list, new VideoAdapter.ItemClickListener() {
            @Override
            public void onClick() {

            }

            @Override
            public void Deselect() {

            }
        });

        video_recyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.ic_back){
            finish();
        }
    }
}