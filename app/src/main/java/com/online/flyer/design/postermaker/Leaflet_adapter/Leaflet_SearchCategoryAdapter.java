package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_model.SearchCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class Leaflet_SearchCategoryAdapter extends RecyclerView.Adapter<Leaflet_SearchCategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(SearchCategoryItem item);
    }

    private final Context context;
    private final List<SearchCategoryItem> items = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public Leaflet_SearchCategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateList(List<SearchCategoryItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaflet_item_search_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        SearchCategoryItem item = items.get(position);
        holder.tvName.setText(item.getName());

        if (item.isTemplate()) {
            holder.tvBadge.setText("Template");
            holder.tvBadge.setTextColor(Color.parseColor("#0066FF"));
            holder.tvBadge.setBackgroundResource(R.drawable.bg_search_badge_template);
            holder.flIconContainer.setBackgroundResource(R.drawable.bg_search_badge_template);
            holder.imgIcon.setColorFilter(Color.parseColor("#0066FF"));
        } else {
            holder.tvBadge.setText("Background");
            holder.tvBadge.setTextColor(Color.parseColor("#059669"));
            holder.tvBadge.setBackgroundResource(R.drawable.bg_search_badge_bg);
            holder.flIconContainer.setBackgroundResource(R.drawable.bg_search_badge_bg);
            holder.imgIcon.setColorFilter(Color.parseColor("#059669"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconContainer;
        ImageView imgIcon;
        TextView tvName;
        TextView tvBadge;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconContainer = itemView.findViewById(R.id.fl_icon_container);
            imgIcon = itemView.findViewById(R.id.img_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvBadge = itemView.findViewById(R.id.tv_category_badge);
        }
    }
}
