package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;

import java.io.IOException;
import java.util.ArrayList;

public class Leaflet_TxtBgAdapter extends RecyclerView.Adapter<Leaflet_TxtBgAdapter.MyViewHolder> {

    private final ArrayList<String> bgArrayList;
    private final Context context;
    private final FontBgClickListener fontBgClickListener;

    public interface FontBgClickListener {
        void onClick(String fontName);
    }

    public Leaflet_TxtBgAdapter(Context context, ArrayList<String> bgArrayList, FontBgClickListener fontBgClickListener) {
        this.context = context;
        this.bgArrayList = bgArrayList;
        this.fontBgClickListener = fontBgClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_effect_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        String sb = bgArrayList.get(position);
        try {
            Drawable drawable = Drawable.createFromResourceStream(context.getResources(),new TypedValue(), context.getAssets().open(sb), null);
            holder.item_image.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> fontBgClickListener.onClick(bgArrayList.get(position)));

    }

    @Override
    public int getItemCount() {
        return bgArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView item_image;

        public MyViewHolder(View v) {
            super(v);
            item_image = v.findViewById(R.id.item_image);
        }
    }
}
