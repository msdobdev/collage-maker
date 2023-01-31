package com.ms.dob.photocollage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ms.dob.photocollage.R;

import java.io.File;
import java.util.List;

public class CreationAdapter extends RecyclerView.Adapter<CreationAdapter.CreationViewHolder> {
    public CreationAdapter(List<File> fileArrayList, Context context, onItemClickListener itemclickListener) {
        this.fileArrayList = fileArrayList;
        this.context = context;
        clickListener = itemclickListener;
    }

    onItemClickListener clickListener;
    List<File> fileArrayList;
    Context context;

   public interface onItemClickListener {
        void onCreationItemClick(File file);
    }

    @NonNull
    @Override
    public CreationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CreationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_creationlist, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CreationViewHolder holder, int position) {
        if (fileArrayList != null) {
            File file = fileArrayList.get(position);
            Picasso.with(context)
                    .load(file)
                    //                .resize(deviceWidth, deviceWidth)
//                    .centerInside()
//                    .config(Bitmap.Config.RGB_565)
                    .into(holder.iv_thumb);
            holder.iv_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onCreationItemClick(file);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return fileArrayList != null ? fileArrayList.size() : 0;
    }

    public class CreationViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_thumb;

        public CreationViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_thumb = itemView.findViewById(R.id.iv_thumb);
        }
    }
}
