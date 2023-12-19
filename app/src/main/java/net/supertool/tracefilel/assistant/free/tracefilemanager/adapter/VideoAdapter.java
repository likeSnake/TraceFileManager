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

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.VideoFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.CornerTransform;
import net.supertool.tracefilel.assistant.free.tracefilemanager.util.DipPx;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{
    private ArrayList<VideoFile> videoPojos;
    private Context context;
    private boolean isFirst = false;
    private ItemClickListener listener;
    private int select_count =0;
    private long selectSize = 0;
    private String size = "";


    public VideoAdapter(Context context, ArrayList<VideoFile> videoPojos, ItemClickListener listener){
        this.context = context;
        this.videoPojos = videoPojos;
        this.listener = listener;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建ViewHolder并关联item布局


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        VideoFile videoPojo = videoPojos.get(position);

        CornerTransform cornerTransform = new CornerTransform(context, DipPx.dip2px(context, 10));
        cornerTransform.setNeedCorner(true, true, false, false);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_video)
                .transform(cornerTransform);

        Glide.with(context)
                .asBitmap()
                .load(videoPojo.getPath())
                .frame(TimeUnit.SECONDS.toMicros(1))
                .apply(options)
                .into(holder.image_item);
        holder.image_name.setText(videoPojo.getName());
        holder.video_time.setText(videoPojo.getVideoDuration());

        if (isFirst){
            if (!videoPojo.isSelect()){
                holder.ic_select.setImageResource(R.drawable.ic_select);
            }else {
                holder.ic_select.setImageResource(R.drawable.ic_selectd);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = true;

                if (videoPojo.isSelect()){
                    select_count--;
                    selectSize -= videoPojo.getVideoLongSize();
                    videoPojo.setSelect(false);
                    holder.ic_select.setImageResource(R.drawable.ic_select);
                }else {
                    select_count++;
                    selectSize += videoPojo.getVideoLongSize();
                    videoPojo.setSelect(true);
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
        return videoPojos.size();
    }

    public String getSelectSize(){
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_item;
        private ImageView ic_select;
        private TextView image_name,video_time;
        public ViewHolder(View itemView) {
            super(itemView);
            image_item = itemView.findViewById(R.id.image_item);
            ic_select = itemView.findViewById(R.id.ic_select);
            image_name = itemView.findViewById(R.id.image_name);
            video_time = itemView.findViewById(R.id.video_time);
        }
    }
    public interface ItemClickListener{
        void onClick();
        void Deselect();
    }
    public int getSelect_count(){
        return select_count;
    }

    public ArrayList<VideoFile> getSelectItems(){
        return videoPojos;
    }

    public ArrayList<VideoFile> getNowImagePojo(){
        return videoPojos;
    }
    public void NotificationListUpdate(int i){
        select_count=0;

        try {
            videoPojos.remove(i);
            notifyItemRemoved(i);

        }catch (Exception e){
            Log.e("移除item出错",e.getMessage());
        }
    }
}
