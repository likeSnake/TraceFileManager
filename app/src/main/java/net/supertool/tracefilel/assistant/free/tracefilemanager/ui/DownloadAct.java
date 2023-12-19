package net.supertool.tracefilel.assistant.free.tracefilemanager.ui;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.JsonRecycleBinFiles;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.StatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.constant.Constant.VideoStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.FilesStatusChange;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.getExcludesList;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.moveRecycleBin;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.recoveryFile;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FilesUtils.getFileBean;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.FileBean;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.adapter.FileAdapter;
import net.supertool.tracefilel.assistant.free.tracefilemanager.myInterface.ItemClickListener;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.ItemSpacingDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;

public class DownloadAct extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<FileBean> fileBeans = new ArrayList<>();
    private FileAdapter fileAdapter;
    private RecyclerView download_recyclerView;
    private TextView text_count,text_title,isEmpty;
    private ImageView ic_back,bt_delete;
    private boolean isFirst = true;
    private boolean isChoosing = false;
    private boolean isRecycleBin = false;
    private boolean isRecover = false;
    private boolean isSelect = false;
    private String actFlag;
    private LinearLayout recover_layout,delete_layout,isRecycleBin_lyaout;
    private boolean DirectDeletion = false;
    private int allItemCount =0;
    private int file_select_count = 0;
    private int folder_select_count = 0;
    private String selectFileSize="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_download);

        initUI();
        initListener();
        initData();
       // startDeleteDialog();
        //startSelectDeleteDialog();
    }
    @SuppressLint("SetTextI18n")
    public void initData(){
        actFlag = getIntent().getStringExtra("actFlag");
        switch (actFlag){
            case "downloadAct":
                isRecycleBin = false;
                String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                fileBeans = getFileBean(downloadPath);
                break;
            case "recycleBin":
                isRecycleBin = true;
                text_title.setText("RecycleBin");
              //  long allFileSize = MMKV.defaultMMKV().decodeLong("recycleBinFilesSize");
                String jsonFiles = MMKV.defaultMMKV().decodeString(JsonRecycleBinFiles);
                fileBeans = new Gson().fromJson(jsonFiles,new TypeToken<ArrayList<FileBean>>(){}.getType());
                break;
        }



        startAdapter(fileBeans);
    }
    public void initListener(){
        ic_back.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        recover_layout.setOnClickListener(this);
        delete_layout.setOnClickListener(this);
    }

    public void initUI(){
        download_recyclerView = findViewById(R.id.download_recyclerView);
        text_count = findViewById(R.id.text_count);
        text_title = findViewById(R.id.text_title);
        ic_back = findViewById(R.id.ic_back);
        bt_delete = findViewById(R.id.bt_delete);
        recover_layout = findViewById(R.id.recover_layout);
        delete_layout = findViewById(R.id.delete_layout);
        isEmpty = findViewById(R.id.isEmpty);
        isRecycleBin_lyaout = findViewById(R.id.isRecycleBin_lyaout);
    }

    public void startAdapter(ArrayList<FileBean> list){
        if (list.isEmpty()){
            isEmpty.setVisibility(View.VISIBLE);
        }else {
            isEmpty.setVisibility(View.GONE);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        fileAdapter = new FileAdapter(this, list, new ItemClickListener() {
            @Override
            public void onClick() {
                allItemCount = fileAdapter.getAllSelect_count();
                file_select_count = fileAdapter.getFile_select_count();
                folder_select_count = fileAdapter.getFolder_select_count();
                selectFileSize = fileAdapter.getSelectFileSize();
                text_count.setText(allItemCount +" Item");

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
        ItemSpacingDecoration itemSpacingDecoration = new ItemSpacingDecoration(this, 10);
        download_recyclerView.addItemDecoration(itemSpacingDecoration);


        download_recyclerView.setLayoutManager(manager);
        download_recyclerView.setAdapter(fileAdapter);

    }

    public void Delete(){
        isChoosing = true;
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        text_count.startAnimation(fadeIn);


        text_count.setVisibility(View.VISIBLE);

        if (isRecycleBin){
            isRecycleBin_lyaout.startAnimation(fadeIn);
            isRecycleBin_lyaout.setVisibility(View.VISIBLE);
        }else {
            bt_delete.startAnimation(fadeIn);
            bt_delete.setVisibility(View.VISIBLE);
        }


        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);

        text_title.startAnimation(fadeOut);
        text_title.setVisibility(View.GONE);
    }

    public void cancelDelete(){
        isFirst = true;
        isChoosing = false;
        fileAdapter.setDirectoryLevel(0);
        fileAdapter.setSelectd(false);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);


        if (isRecycleBin){
            isRecycleBin_lyaout.startAnimation(fadeOut);
            isRecycleBin_lyaout.setVisibility(View.GONE);
        }else {
            bt_delete.startAnimation(fadeOut);
            bt_delete.setVisibility(View.GONE);
        }
        text_count.startAnimation(fadeOut);
        text_count.setVisibility(View.GONE);


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
        if (v.getId()==R.id.ic_back){
            if (isChoosing){
                cancelDelete();
                fileAdapter.DeselectAll();
            }else {
                if (fileAdapter.getDirectoryLevel()!=0){
                    if (fileAdapter.getDirectoryLevel() == 1){
                        if (isRecycleBin){
                            startAdapter(fileBeans);
                        }else {
                            fileAdapter.recallDirectory();
                        }
                    }else {
                        fileAdapter.recallDirectory();
                    }


                }else {
                    finish();
                }
            }


        }
        if (v.getId()==R.id.bt_delete){
            startSelectDeleteDialog();

        }
        if (v.getId() == R.id.delete_layout){

            startDeleteDialog();

        }
        if (v.getId() == R.id.recover_layout){
            isRecover = true;
            DirectDeletion = false;
            deleteFile();
        }
    }
    public void deleteFile(){
        ArrayList<FileBean> selectFileBeans = fileAdapter.getSelectItems();
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
                    if (isRecover){
                        recoveryFile(selectFileBeans.get(i).getPath());
                    }else {
                        moveRecycleBin(selectFileBeans.get(i).getPath());
                    }
                }


            }
        }

        for (int i = 0; i < integers.size(); i++) {
            if (i!=0){
                fileAdapter.myUpdate(integers.get(i)-i);
            }else {
                fileAdapter.myUpdate(integers.get(i));
            }

        }
        FilesStatusChange();
        cancelDelete();
        getExcludesList();
    }

    public void startDeleteDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.getWindow().setBackgroundDrawableResource(R.color.my_colors);
        dialog.setCancelable(false);
      //  ((TextView)dialog.findViewById(R.id.rubbishSize)).setText(rubbish);
        dialog.findViewById(R.id.dialog_Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecover = false;
                DirectDeletion = true;
                deleteFile();
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
        folder_count.setText("Folder: "+ folder_select_count);
        file_count.setText("File: "+ file_select_count);
        allSize.setText("Size: "+ selectFileSize);


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
                    isRecover = false;
                    DirectDeletion = true;
                }else {
                    isRecover = false;
                    DirectDeletion = false;
                }
                deleteFile();
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