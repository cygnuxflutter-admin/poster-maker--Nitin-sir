package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.util.ArrayList;

public class Leaflet_PosterGroupChildAdapter extends RecyclerView.Adapter<Leaflet_PosterGroupChildAdapter.MyViewHolder> {

    private final Activity activity;
    private final ArrayList<Leaflet_PosterImage> posterThumbLists;
    private final int cellWidth, cellHeight, cat_id;
    private final OnPosterClickListener onPosterClickListener;
    private final Leaflet_NativeAdUtil nativeAdUtil;
    private Leaflet_PreferenceClass preferenceClass;

    public Leaflet_PosterGroupChildAdapter(Activity activity, ArrayList<Leaflet_PosterImage> posterThumbLists, int cat_id, int cellWidth, int cellHeight, OnPosterClickListener onPosterClickListener) {
        this.activity = activity;
        this.posterThumbLists = posterThumbLists;
        this.cat_id = cat_id;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.onPosterClickListener = onPosterClickListener;
        this.nativeAdUtil = new Leaflet_NativeAdUtil(activity, cellWidth, cellHeight);
        preferenceClass = new Leaflet_PreferenceClass(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_poster_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.native_banner_ad_container.getLayoutParams().width = cellWidth;
        holder.native_banner_ad_container.getLayoutParams().height = cellHeight;
        holder.native_banner_ad_container.invalidate();

        holder.iv_image.getLayoutParams().width = cellWidth;
        holder.iv_image.getLayoutParams().height = cellHeight;
        holder.iv_image.invalidate();

        if (position % preferenceClass.getInt("PremiumPostCount", 3)==0) {
            holder.iv_lock.setVisibility(View.VISIBLE);
        } else {
        holder.iv_lock.setVisibility(View.GONE);
        }

        if (posterThumbLists.get(position) != null) {
            holder.content_layout.setVisibility(View.VISIBLE);
            holder.ad_layout.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(posterThumbLists.get(position).getPost_thumb())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .animate(R.anim.abc_fade_in)
                    .skipMemoryCache(false)
                    .centerCrop()
                    .into(holder.iv_image);

            String posterId = String.valueOf(posterThumbLists.get(position).getPost_id());
            boolean isLiked = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager.isPosterLiked(activity, posterId);
            holder.iv_like.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

            holder.iv_like.setOnClickListener(v -> {
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager.isPosterLiked(activity, posterId)) {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager.removeLikedPoster(activity, posterId);
                    holder.iv_like.setImageResource(R.drawable.ic_heart_outline);
                } else {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager.addLikedPoster(activity, posterThumbLists.get(position));
                    holder.iv_like.setImageResource(R.drawable.ic_heart_filled);
                }
            });


            holder.iv_image.setOnClickListener(v -> {
                if (holder.iv_image.getDrawable() == null) {
                    return;
                }
                onPosterClickListener.onPosterClick(posterThumbLists.get(position).getCat_id(), posterThumbLists.get(position).getPost_id(), holder.iv_lock.getVisibility() == View.VISIBLE);
            });
        } else {
            holder.content_layout.setVisibility(View.GONE);
            holder.ad_layout.setVisibility(View.VISIBLE);
            holder.iv_lock.setVisibility(View.GONE);
            nativeAdUtil.fillAdmobNativeAd(holder.native_banner_ad_container);
        }
    }

    @Override
    public int getItemCount() {
        return posterThumbLists.size();
    }

    public interface OnPosterClickListener {
        void onPosterClick(int cat_id, int post_id, boolean premium);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout content_layout, ad_layout, native_banner_ad_container;
        private final ImageView iv_image;
        private final View iv_lock;
        private final ImageView iv_like;

        MyViewHolder(View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            iv_lock = itemView.findViewById(R.id.iv_lock);
            iv_like = itemView.findViewById(R.id.iv_like);
            content_layout = itemView.findViewById(R.id.content_layout);
            ad_layout = itemView.findViewById(R.id.ad_layout);
            native_banner_ad_container = itemView.findViewById(R.id.native_banner_ad_container);
            //TitanicTextView titanicTextView = itemView.findViewById(R.id.titanicTextView);
            ImageView titanicTextView = itemView.findViewById(R.id.titanicTextView);
          //  new Titanic().start(titanicTextView);
        }
    }
}
