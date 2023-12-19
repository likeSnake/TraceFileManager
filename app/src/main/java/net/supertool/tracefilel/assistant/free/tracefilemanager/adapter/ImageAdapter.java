package net.supertool.tracefilel.assistant.free.tracefilemanager.adapter;

import static net.supertool.tracefilel.assistant.free.tracefilemanager.util.FileManager.unitConversion;

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
import com.bumptech.glide.request.RequestOptions;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ImageFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.CornerTransform;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.DipPx;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
    private ArrayList<ImageFile> imageFiles;
    private Context context;
    private boolean isFirst = false;
    private ItemClickListener listener;
    private int select_count =0;
    private long selectSize = 0;
    private String size = "";


    public ImageAdapter(Context context, ArrayList<ImageFile> imageFiles, ItemClickListener listener){
        this.context = context;
        this.imageFiles = imageFiles;
        this.listener = listener;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建ViewHolder并关联item布局


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImageFile imageFile = imageFiles.get(position);

        Glide.with(context).asBitmap()
                .load(imageFile.getPath())
                .into(holder.image_item);

        holder.image_name.setText(imageFile.getName());


        if (isFirst){
            if (!imageFile.isSelect()){
                holder.ic_select.setImageResource(R.drawable.ic_select);
            }else {
                holder.ic_select.setImageResource(R.drawable.ic_selectd);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = true;

                if (imageFile.isSelect()){
                    select_count--;
                    selectSize -= imageFile.getImageLongSize();
                    imageFile.setSelect(false);
                    holder.ic_select.setImageResource(R.drawable.ic_select);
                }else {
                    select_count++;
                    selectSize += imageFile.getImageLongSize();
                    imageFile.setSelect(true);
                    holder.ic_select.setImageResource(R.drawable.ic_selectd);

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
        return imageFiles.size();
    }

    public String getSelectSize(){
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_item;
        private ImageView ic_select;
        private TextView image_name;
        public ViewHolder(View itemView) {
            super(itemView);
            image_item = itemView.findViewById(R.id.image_item);
            ic_select = itemView.findViewById(R.id.ic_select);
            image_name = itemView.findViewById(R.id.image_name);

        }
    }
    public interface ItemClickListener{
        void onClick();
        void Deselect();
    }
    public int getSelect_count(){
        return select_count;
    }

    public ArrayList<ImageFile> getSelectItems(){
        return imageFiles;
    }

    public ArrayList<ImageFile> getNowImagePojo(){
        return imageFiles;
    }
    public void NotificationListUpdate(int i){
        select_count=0;

        try {
            imageFiles.remove(i);
            notifyItemRemoved(i);

        }catch (Exception e){
            Log.e("移除item出错",e.getMessage());
        }
    }
}
