package net.supertool.tracefilel.assistant.free.tracefilemanager.adapter;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.unitConversion;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isAudioFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isImageFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isVideoFileByExtension;
import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileUtil.isZipFileByExtension;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tencent.mmkv.MMKV;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.myInterface.ItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class DuplicateFileAdapter extends RecyclerView.Adapter<DuplicateFileAdapter.DuplicateFileViewHolder> {

    private int selects=0;
    private int selectedPosition;
    private Context context;
    private ArrayList<ZipFile> zipBeans;
    private ArrayList<String> select_list = new ArrayList<>();
    private boolean b = false;
    private boolean isFirst = false;
    private int select_count =0;
    private ItemClickListener listener;
    private long selectSize = 0;

    private String size = "";

    public DuplicateFileAdapter(Context context, ArrayList<ZipFile> zipBeans,ItemClickListener listener) {
        this.context = context;
        this.zipBeans = zipBeans;
        this.listener = listener;

        Collections.reverse(this.zipBeans);
    }

    @Override
    public DuplicateFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detailed_file, parent, false);
        return new DuplicateFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DuplicateFileViewHolder holder, int position) {
        ZipFile zipBean = zipBeans.get(position);

        holder.file_name.setText(zipBean.getName());
        holder.file_size.setText(zipBean.getSize());
        holder.file_path.setText(zipBean.getPath());
        holder.file_time.setText(zipBean.getTime());

        if (isImageFileByExtension(zipBean.getName())){
            Glide.with(context)
                    .load(zipBean.getPath())
                    .into(holder.item_image);
        }else if (isVideoFileByExtension(zipBean.getName())){
            Glide.with(context)
                    .asBitmap()
                    .load(zipBean.getPath())
                    .frame(TimeUnit.SECONDS.toMicros(1))
                    .into(holder.item_image);
        }else if (isAudioFileByExtension(zipBean.getName())){
            holder.item_image.setImageResource(R.drawable.ic_audio);
        }else if (isZipFileByExtension(zipBean.getName())){
            holder.item_image.setImageResource(R.drawable.ic_zip_item);
        }else {
            holder.item_image.setImageResource(R.drawable.ic_document);
        }



        if (isFirst){
            if (!zipBean.getSelect()){
                holder.ic_select.setImageResource(R.drawable.ic_select);
            }else {
                holder.ic_select.setImageResource(R.drawable.ic_selectd);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = true;

                if (zipBean.getSelect()){
                    select_count--;
                    zipBean.setSelect(false);
                    holder.ic_select.setImageResource(R.drawable.ic_select);
                    selectSize -= zipBean.getFileSize();
                }else {
                    select_count++;
                    zipBean.setSelect(true);
                    holder.ic_select.setImageResource(R.drawable.ic_selectd);
                    selectSize += zipBean.getFileSize();
                //    File file = new File(zipBean.getPath());
                }
                size = unitConversion(selectSize);
                if (select_count !=0){
                    listener.onClick();
                }else {
                    listener.Deselect();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return zipBeans.size();
    }


    public class DuplicateFileViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image,ic_select;
        TextView file_name,file_path,file_size,file_time;

        public DuplicateFileViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            file_name = itemView.findViewById(R.id.file_name);
            file_path = itemView.findViewById(R.id.file_path);
            file_size = itemView.findViewById(R.id.file_size);
            file_time = itemView.findViewById(R.id.file_time);
            ic_select = itemView.findViewById(R.id.ic_select);

        }
    }
    public ArrayList<ZipFile> getSelectItems(){

        return zipBeans;
    }
    public int getSelect_count(){
        return select_count;
    }

    public String getSelectSize(){
        return size;
    }

    public void myUpdate(int i){
        select_count=0;

        try {
            zipBeans.remove(i);
            notifyItemRemoved(i);

        }catch (Exception e){
            Log.e("移除item出错",e.getMessage());
        }
    }
}