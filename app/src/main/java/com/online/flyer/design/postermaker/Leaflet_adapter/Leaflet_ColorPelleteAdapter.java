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
        holder.iv_lock.setVisibility(View.GONE);
        holder.iv_image.getLayoutParams().width = cellWidth;
        holder.iv_image.getLayoutParams().height = cellHeight;
        holder.iv_image.invalidate();

        try { holder.iv_image.setBackgroundColor(Color.parseColor(colorList[position])); } catch (Exception e) { holder.iv_image.setBackgroundColor(Color.BLACK); }

        holder.iv_image.setOnClickListener(v -> colorPelleteListener.onClick(colorList[position]));

    }

    @Override
    public int getItemCount() {
        return colorList.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        android.view.View iv_lock;
        android.view.View ad_layout;
        android.view.View iv_like;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            iv_lock = itemView.findViewById(R.id.iv_lock);
            ad_layout = itemView.findViewById(R.id.ad_layout);
            iv_like = itemView.findViewById(R.id.iv_like);
            
            if (ad_layout != null) {
                ad_layout.setVisibility(View.GONE);
            }
            if (iv_like != null) {
                iv_like.setVisibility(View.GONE);
            }
            
            // Hide progress bar if present (it doesn't have an ID, so we find it by type)
            if (itemView instanceof android.view.ViewGroup) {
                android.view.ViewGroup vg = (android.view.ViewGroup) itemView;
                hideProgressBars(vg);
            }
        }
        
        private void hideProgressBars(android.view.ViewGroup vg) {
            for (int i = 0; i < vg.getChildCount(); i++) {
                android.view.View child = vg.getChildAt(i);
                if (child instanceof android.widget.ProgressBar) {
                    child.setVisibility(View.GONE);
                } else if (child instanceof android.view.ViewGroup) {
                    hideProgressBars((android.view.ViewGroup) child);
                }
            }
        }
    }
}

