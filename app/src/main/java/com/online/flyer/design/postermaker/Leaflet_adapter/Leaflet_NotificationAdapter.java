package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NotificationModel;
import com.online.flyer.design.postermaker.R;

import java.util.List;

public class Leaflet_NotificationAdapter extends RecyclerView.Adapter<Leaflet_NotificationAdapter.ViewHolder> {
    private Context context;
    private List<Leaflet_NotificationModel> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Leaflet_NotificationModel model, int position);
    }

    public Leaflet_NotificationAdapter(Context context, List<Leaflet_NotificationModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaflet_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Leaflet_NotificationModel model = list.get(position);

        holder.tvTitle.setText(model.getTitle());
        holder.tvMessage.setText(model.getMessage());

        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(model.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.tvTime.setText(timeAgo);

        if (model.isRead()) {
            holder.viewUnreadDot.setVisibility(View.GONE);
            holder.lytParent.setBackgroundColor(android.graphics.Color.parseColor("#FFFFFF"));
        } else {
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
            holder.lytParent.setBackgroundColor(android.graphics.Color.parseColor("#F3F4F6"));
        }

        if (model.getImage() != null && !model.getImage().trim().isEmpty() && !model.getImage().trim().equalsIgnoreCase("null")) {
            if (holder.cvImage != null) holder.cvImage.setVisibility(View.VISIBLE);
            holder.ivImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(model.getImage()).into(holder.ivImage);
        } else {
            if (holder.cvImage != null) holder.cvImage.setVisibility(View.GONE);
            holder.ivImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(model, position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        View viewUnreadDot;
        ImageView ivImage;
        androidx.cardview.widget.CardView cvImage;
        LinearLayout lytParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);
            ivImage = itemView.findViewById(R.id.iv_image);
            cvImage = itemView.findViewById(R.id.cv_image);
            lytParent = itemView.findViewById(R.id.lyt_parent);
        }
    }
}

