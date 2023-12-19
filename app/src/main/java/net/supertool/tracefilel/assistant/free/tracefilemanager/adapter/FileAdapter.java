package net.supertool.tracefilel.assistant.free.tracefilemanager.adapter;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.unitConversion;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isAudioFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isImageFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isVideoFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isZipFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FilesUtils.getFileBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.FileBean;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.myInterface.ItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FullViewHolder> {

    private int selects=0;
    private int selectedPosition;
    private Context context;
    private ArrayList<FileBean> fileBeans;
    private ArrayList<String> select_list = new ArrayList<>();
    private boolean b = false;
    private ItemClickListener listener;
    private boolean isFirst = false;
    private int all_select_count =0;
    private long selectSize = 0;
    private int file_select_count = 0;
    private int folder_select_count = 0;
    private int directoryLevel = 0;
    private String currentDirectory="";
    private boolean isFirstClick = true;
    private boolean isSelectd = false;
    private String selectFileSize = "";

    public FileAdapter(Context context, ArrayList<FileBean> fileBeans,ItemClickListener listener) {
        this.context = context;
        this.fileBeans = fileBeans;
        this.listener = listener;
      //  currentDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    }

    @Override
    public FullViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new FullViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FullViewHolder holder, int position) {
        FileBean fileBean = fileBeans.get(position);

        holder.file_name.setText(fileBean.getName());
        holder.file_time.setText(fileBean.getTime());
        if (fileBean.isFolder()){
            holder.item_image.setImageResource(R.drawable.ic_folder_blue);
            holder.file_counts.setText(fileBean.getFileCount()+" Item");

        }else {
            holder.file_counts.setText(fileBean.getSize());

            if (isImageFileByExtension(fileBean.getName())){
                Glide.with(context)
                        .load(fileBean.getPath())
                        .into(holder.item_image);
            }else if (isVideoFileByExtension(fileBean.getName())){
                Glide.with(context)
                        .asBitmap()
                        .load(fileBean.getPath())
                        .frame(TimeUnit.SECONDS.toMicros(1))
                        .into(holder.item_image);
            }else if (isAudioFileByExtension(fileBean.getName())){
                holder.item_image.setImageResource(R.drawable.ic_audio);
            }else if (isZipFileByExtension(fileBean.getName())){
                holder.item_image.setImageResource(R.drawable.ic_zip_item);
            }else {
                holder.item_image.setImageResource(R.drawable.ic_document);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if(isSelectd){
                    onSelectOnClick(fileBean,holder);
                }else {
                    String path = fileBean.getPath();
                    if (fileBean.isFolder()){
                        currentDirectory = path;
                        System.out.println("当前路径："+path);
                        directoryLevel++;
                        fileBeans = getFileBean(path);
                        notifyDataSetChanged();
                    }else {
                        onSelectOnClick(fileBean,holder);
                    }


                }

            }
        });
       /* Glide.with(context)
                .load(fileBean.getPath())
                .into(holder.item_image);*/

        if (isFirst){
            if (!fileBean.isSelect()){
                holder.ic_select.setImageResource(R.drawable.ic_select);
            }else {
                holder.ic_select.setImageResource(R.drawable.ic_selectd);
            }
        }


        holder.ic_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectOnClick(fileBean,holder);
            }
        });
    }

    public void onSelectOnClick(FileBean fileBean,FullViewHolder holder){
        isFirst = true;

        if (fileBean.isSelect()){
            if (fileBean.isFolder()){
                folder_select_count--;
            }else {
                file_select_count--;
            }
            all_select_count--;
            fileBean.setSelect(false);
            holder.ic_select.setImageResource(R.drawable.ic_select);
               selectSize -= fileBean.getFileSize();
        }else {
            if (fileBean.isFolder()){
                folder_select_count++;
            }else {
                file_select_count++;
            }
            all_select_count++;
            fileBean.setSelect(true);
            holder.ic_select.setImageResource(R.drawable.ic_selectd);
                selectSize += fileBean.getFileSize();
            //    File file = new File(fileBean.getPath());
        }
        selectFileSize = unitConversion(selectSize);
        if (all_select_count !=0){
            if (isFirstClick){
                isFirstClick = false;
            }
            isSelectd = true;
            listener.onClick();
        }else {
            isSelectd = false;
            isFirstClick = true;
            listener.Deselect();
        }
    }
    @Override
    public int getItemCount() {
        return fileBeans.size();
    }


    public class FullViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image,ic_select;
        TextView file_name,file_time,file_counts;

        public FullViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            file_name = itemView.findViewById(R.id.file_name);

            file_time = itemView.findViewById(R.id.file_time);
            ic_select = itemView.findViewById(R.id.ic_select);
            file_counts = itemView.findViewById(R.id.file_counts);

        }
    }
    public ArrayList<FileBean> getSelectItems(){

        return fileBeans;
    }

    public int getFile_select_count() {
        return file_select_count;
    }

    public int getFolder_select_count() {
        return folder_select_count;
    }




    public int getAllSelect_count(){
        return all_select_count;
    }

    public int getDirectoryLevel(){
        return directoryLevel;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void recallDirectory(){
        directoryLevel--;
        File file = new File(currentDirectory);
        File parentFile = file.getParentFile();
        currentDirectory = parentFile.getAbsolutePath();
        System.out.println("parentFile:"+parentFile.getAbsolutePath());
        fileBeans = getFileBean(parentFile.getAbsolutePath());
        notifyDataSetChanged();

    }

    public void setDirectoryLevel(int i){
        all_select_count = 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void DeselectAll(){
        selectSize = 0;
        all_select_count =0;
        folder_select_count =0;
        file_select_count =0;
        for (FileBean fileBean : fileBeans) {
            fileBean.setSelect(false);
        }
        notifyDataSetChanged();
    }

    public void myUpdate(int i){
        all_select_count =0;
        selectSize = 0;
        folder_select_count =0;
        file_select_count =0;
        try {
            fileBeans.remove(i);
            notifyItemRemoved(i);

        }catch (Exception e){
            Log.e("移除item出错",e.getMessage());
        }
    }

    public void setSelectd(boolean b){
        this.isSelectd = b;
    }

    public String getSelectFileSize() {
        return selectFileSize;
    }
}