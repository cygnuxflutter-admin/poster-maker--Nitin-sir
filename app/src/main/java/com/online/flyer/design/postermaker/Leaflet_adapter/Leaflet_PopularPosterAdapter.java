package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
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
            if (holder.posterPlaceholder != null) holder.posterPlaceholder.setVisibility(View.GONE);
            if (holder.posterImage != null) holder.posterImage.setVisibility(View.VISIBLE);
            if (holder.posterShimmer != null) {
                holder.posterShimmer.setVisibility(View.VISIBLE);
                holder.posterShimmer.startShimmer();
            }

            Glide.with(context)
                    .load(poster.getImageUrl())
                    .priority(com.bumptech.glide.Priority.HIGH)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (holder.posterShimmer != null) {
                                holder.posterShimmer.stopShimmer();
                                holder.posterShimmer.setVisibility(View.GONE);
                            }
                            if (holder.posterPlaceholder != null) {
                                holder.posterPlaceholder.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (holder.posterShimmer != null) {
                                holder.posterShimmer.stopShimmer();
                                holder.posterShimmer.setVisibility(View.GONE);
                            }
                            if (holder.posterImage != null) {
                                holder.posterImage.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }
                    })
                    .into(holder.posterImage);
        } else {
            // No API image — show centered placeholder icon
            if (holder.posterShimmer != null) {
                holder.posterShimmer.stopShimmer();
                holder.posterShimmer.setVisibility(View.GONE);
            }
            if (holder.posterImage != null) {
                holder.posterImage.setVisibility(View.GONE);
            }
            if (holder.posterPlaceholder != null) {
                holder.posterPlaceholder.setVisibility(View.VISIBLE);
            }
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
        ShimmerFrameLayout posterShimmer;

        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            posterPlaceholder = itemView.findViewById(R.id.poster_placeholder);
            posterShimmer = itemView.findViewById(R.id.poster_shimmer);
        }
    }
}
