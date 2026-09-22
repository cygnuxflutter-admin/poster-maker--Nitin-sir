package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;

public class Leaflet_ColorPelleteAdapter extends RecyclerView.Adapter<Leaflet_ColorPelleteAdapter.MyViewHolder> {

    private final String[] colorList;
    private final int cellWidth;
    private final int cellHeight;
    private final ColorPelleteListener colorPelleteListener;

    public interface ColorPelleteListener {
        void onClick(String colorCode);
    }

    public Leaflet_ColorPelleteAdapter(Context context, int cellWidth, int cellHeight, ColorPelleteListener colorPelleteListener) {
        this.colorList = context.getResources().getStringArray(R.array.color_list);
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.colorPelleteListener = colorPelleteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_poster_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.titanicTextView.setVisibility(View.GONE);
        holder.iv_lock.setVisibility(View.GONE);
        holder.iv_image.getLayoutParams().width = cellWidth;
        holder.iv_image.getLayoutParams().height = cellHeight;
        holder.iv_image.invalidate();

        holder.iv_image.setBackgroundColor(Color.parseColor(colorList[position]));

        holder.iv_image.setOnClickListener(v -> colorPelleteListener.onClick(colorList[position]));

    }

    @Override
    public int getItemCount() {
        return colorList.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, titanicTextView, iv_lock;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            titanicTextView = itemView.findViewById(R.id.titanicTextView);
            iv_lock = itemView.findViewById(R.id.iv_lock);

        }
    }
}
