package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_HeroBanner;
import com.online.flyer.design.postermaker.R;

import java.util.List;

public class Leaflet_HeroBannerAdapter extends RecyclerView.Adapter<Leaflet_HeroBannerAdapter.BannerViewHolder> {

    // Safe loop multiplier for smooth infinite carousel without integer overflow
    private static final int FAKE_LOOP_MULTIPLIER = 1000;

    private final Context context;
    private final List<Leaflet_HeroBanner> bannerList;
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Leaflet_HeroBanner banner, int position);
    }

    public Leaflet_HeroBannerAdapter(Context context, List<Leaflet_HeroBanner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Leaflet_HeroBanner> newBanners) {
        if (this.bannerList != newBanners) {
            this.bannerList.clear();
            this.bannerList.addAll(newBanners);
        }
        notifyDataSetChanged();
    }

    /**
     * Returns the real item count (not the infinite count).
     * Used for indicators, auto-scroll modulo, etc.
     */
    public int getRealCount() {
        return bannerList.size();
    }

    /**
     * Returns the starting position for ViewPager2 so we start in the middle
     * of the fake list — allows swiping both directions infinitely.
     */
    public int getStartPosition() {
        if (bannerList.isEmpty()) return 0;
        int total = bannerList.size() * FAKE_LOOP_MULTIPLIER;
        return (total / 2) - ((total / 2) % bannerList.size());
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaflet_item_hero_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // Map fake infinite position to real data position
        int realPosition = position % bannerList.size();
        Leaflet_HeroBanner banner = bannerList.get(realPosition);

        if (!TextUtils.isEmpty(banner.getImageUrl())) {
            if (holder.bannerPlaceholder != null) holder.bannerPlaceholder.setVisibility(View.GONE);
            if (holder.bannerImage != null) holder.bannerImage.setVisibility(View.VISIBLE);
            if (holder.bannerShimmer != null) {
                holder.bannerShimmer.setVisibility(View.VISIBLE);
                holder.bannerShimmer.startShimmer();
            }

            Glide.with(context)
                    .load(banner.getImageUrl())
                    .priority(com.bumptech.glide.Priority.HIGH)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (holder.bannerShimmer != null) {
                                holder.bannerShimmer.stopShimmer();
                                holder.bannerShimmer.setVisibility(View.GONE);
                            }
                            if (holder.bannerPlaceholder != null) {
                                holder.bannerPlaceholder.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (holder.bannerShimmer != null) {
                                holder.bannerShimmer.stopShimmer();
                                holder.bannerShimmer.setVisibility(View.GONE);
                            }
                            if (holder.bannerImage != null) {
                                holder.bannerImage.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }
                    })
                    .into(holder.bannerImage);
        } else {
            if (holder.bannerShimmer != null) {
                holder.bannerShimmer.stopShimmer();
                holder.bannerShimmer.setVisibility(View.GONE);
            }
            if (holder.bannerImage != null) {
                holder.bannerImage.setVisibility(View.GONE);
            }
            if (holder.bannerPlaceholder != null) {
                holder.bannerPlaceholder.setVisibility(View.VISIBLE);
            }
        }

        if (!TextUtils.isEmpty(banner.getTitle())) {
            holder.bannerTitle.setVisibility(View.VISIBLE);
            holder.bannerTitle.setText(banner.getTitle());
        } else {
            holder.bannerTitle.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner, realPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.isEmpty() ? 0 : bannerList.size() * FAKE_LOOP_MULTIPLIER;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        LinearLayout bannerPlaceholder;
        TextView bannerTitle;
        ShimmerFrameLayout bannerShimmer;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_image);
            bannerPlaceholder = itemView.findViewById(R.id.banner_placeholder);
            bannerTitle = itemView.findViewById(R.id.banner_title);
            bannerShimmer = itemView.findViewById(R.id.banner_shimmer);
        }
    }
}
