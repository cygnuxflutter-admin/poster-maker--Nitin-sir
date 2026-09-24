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
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_HeroBanner;
import com.online.flyer.design.postermaker.R;

import java.util.List;

public class Leaflet_HeroBannerAdapter extends RecyclerView.Adapter<Leaflet_HeroBannerAdapter.BannerViewHolder> {

    // Use a huge count for infinite looping effect
    private static final int FAKE_INFINITE_COUNT = Integer.MAX_VALUE;

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
        // Start near the middle so user can scroll both left and right
        return (FAKE_INFINITE_COUNT / 2) - ((FAKE_INFINITE_COUNT / 2) % bannerList.size());
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
            holder.bannerImage.setVisibility(View.VISIBLE);
            holder.bannerPlaceholder.setVisibility(View.GONE);
            Glide.with(context)
                    .load(banner.getImageUrl())
                    .placeholder(R.drawable.bg_hero_banner_placeholder)
                    .error(R.drawable.bg_hero_banner_placeholder)
                    .centerCrop()
                    .into(holder.bannerImage);
        } else {
            holder.bannerImage.setVisibility(View.GONE);
            holder.bannerPlaceholder.setVisibility(View.VISIBLE);
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
        // Infinite scrolling — return huge number
        return bannerList.isEmpty() ? 0 : FAKE_INFINITE_COUNT;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        LinearLayout bannerPlaceholder;
        TextView bannerTitle;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_image);
            bannerPlaceholder = itemView.findViewById(R.id.banner_placeholder);
            bannerTitle = itemView.findViewById(R.id.banner_title);
        }
    }
}
