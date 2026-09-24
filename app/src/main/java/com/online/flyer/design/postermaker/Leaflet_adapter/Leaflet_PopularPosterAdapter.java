package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_PopularPoster;
import com.online.flyer.design.postermaker.R;

import java.util.List;

public class Leaflet_PopularPosterAdapter extends RecyclerView.Adapter<Leaflet_PopularPosterAdapter.PosterViewHolder> {

    private final Context context;
    private final List<Leaflet_PopularPoster> posterList;
    private OnPosterClickListener listener;

    public interface OnPosterClickListener {
        void onPosterClick(Leaflet_PopularPoster poster, int position);
    }

    public Leaflet_PopularPosterAdapter(Context context, List<Leaflet_PopularPoster> posterList) {
        this.context = context;
        this.posterList = posterList;
    }

    public void setOnPosterClickListener(OnPosterClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Leaflet_PopularPoster> newPosters) {
        if (this.posterList != newPosters) {
            this.posterList.clear();
            this.posterList.addAll(newPosters);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaflet_item_popular_poster, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        Leaflet_PopularPoster poster = posterList.get(position);

        if (!TextUtils.isEmpty(poster.getImageUrl())) {
            // API image available
            holder.posterImage.setVisibility(View.VISIBLE);
            holder.posterPlaceholder.setVisibility(View.GONE);
            Glide.with(context)
                    .load(poster.getImageUrl())
                    .placeholder(R.drawable.bg_popular_card)
                    .error(R.drawable.bg_popular_card)
                    .centerCrop()
                    .into(holder.posterImage);
        } else {
            // No API image — show centered placeholder icon
            holder.posterImage.setVisibility(View.GONE);
            holder.posterPlaceholder.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPosterClick(poster, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posterList.size();
    }

    static class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        LinearLayout posterPlaceholder;

        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            posterPlaceholder = itemView.findViewById(R.id.poster_placeholder);
        }
    }
}
