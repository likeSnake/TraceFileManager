package net.supertool.tracefilel.assistant.free.tracefilemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.supertool.tracefilel.assistant.free.tracefilemanager.File.ZipFile;
import net.supertool.tracefilel.assistant.free.tracefilemanager.R;

import java.util.ArrayList;
import java.util.Collections;

public class FullAdapter extends RecyclerView.Adapter<FullAdapter.FullViewHolder> {

    private int selects=0;
    private int selectedPosition;
    private Context context;
    private ArrayList<ZipFile> zipBeans;
    private ArrayList<String> select_list = new ArrayList<>();
    private boolean b = false;

    public FullAdapter(Context context, ArrayList<ZipFile> zipBeans) {
        this.context = context;
        this.zipBeans = zipBeans;

        Collections.reverse(this.zipBeans);
    }

    @Override
    public FullViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detailed_file, parent, false);
        return new FullViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FullViewHolder holder, int position) {
        ZipFile zipBean = zipBeans.get(position);

        holder.file_name.setText(zipBean.getName());
        holder.file_size.setText(zipBean.getSize());
        holder.file_path.setText(zipBean.getPath());
        holder.file_time.setText(zipBean.getTime());

        Glide.with(context)
                .load(zipBean.getPath())
                .into(holder.item_image);

    }

    @Override
    public int getItemCount() {
        return zipBeans.size();
    }


    public class FullViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView file_name,file_path,file_size,file_time;

        public FullViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            file_name = itemView.findViewById(R.id.file_name);
            file_path = itemView.findViewById(R.id.file_path);
            file_size = itemView.findViewById(R.id.file_size);
            file_time = itemView.findViewById(R.id.file_time);

        }
    }
    public ArrayList<ZipFile> getSelectItems(){

        return zipBeans;
    }

}