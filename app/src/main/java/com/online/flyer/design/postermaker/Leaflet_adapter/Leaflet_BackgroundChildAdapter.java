package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.util.ArrayList;

public class Leaflet_BackgroundChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_AD = 1;

    private final Activity activity;
    private final ArrayList<Leaflet_BgImage> bgImages;
    private String categoryName = "Background";
    private final int cellWidth;
    private final int cellHeight;
    private final OnBgClickListener onBgClickListener;
    private final Leaflet_NativeAdUtil nativeAdUtil;
    private final Leaflet_PreferenceClass preferenceClass;

    public Leaflet_BackgroundChildAdapter(Activity activity, ArrayList<Leaflet_BgImage> bgImages, int cellWidth, int cellHeight, OnBgClickListener onBgClickListener) {
        this(activity, bgImages, "Background", cellWidth, cellHeight, onBgClickListener);
    }

    public Leaflet_BackgroundChildAdapter(Activity activity, ArrayList<Leaflet_BgImage> bgImages, String categoryName, int cellWidth, int cellHeight, OnBgClickListener onBgClickListener) {
        this.activity = activity;
        this.bgImages = bgImages;
        this.categoryName = (categoryName != null && !categoryName.isEmpty()) ? categoryName : "Background";
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.onBgClickListener = onBgClickListener;
        this.nativeAdUtil = new Leaflet_NativeAdUtil(activity);
        this.preferenceClass = new Leaflet_PreferenceClass(activity);
    }

    @Override
    public int getItemViewType(int position) {
        if (bgImages.get(position) == null) {
            return TYPE_AD;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_grid_native_ad_row, parent, false);
            return new AdViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_poster_rv, parent, false);
            return new BgViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_AD) {
            AdViewHolder adHolder = (AdViewHolder) holder;
            adHolder.itemView.setVisibility(View.VISIBLE);
            adHolder.native_banner_ad_container.setVisibility(View.VISIBLE);
            String adId = preferenceClass.getGridNativeAdId("backgrounds");
            boolean showShimmerOnFail = preferenceClass.shouldShowNativeShimmerOnFail("backgrounds");

            nativeAdUtil.fillAdmobNativeAd(adHolder.native_banner_ad_container, adId, showShimmerOnFail, new Leaflet_NativeAdUtil.OnNativeAdListener() {
                @Override
                public void onAdLoaded() {
                    adHolder.itemView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailed() {
                    if (!showShimmerOnFail) {
                        adHolder.itemView.setVisibility(View.GONE);
                        if (adHolder.itemView.getLayoutParams() != null) {
                            adHolder.itemView.getLayoutParams().height = 0;
                            adHolder.itemView.requestLayout();
                        }
                        if (activity != null) {
                            activity.runOnUiThread(() -> {
                                adHolder.itemView.post(() -> {
                                    int currentPos = adHolder.getAdapterPosition();
                                    if (currentPos != RecyclerView.NO_POSITION && currentPos < bgImages.size() && bgImages.get(currentPos) == null) {
                                        bgImages.remove(currentPos);
                                        notifyItemRemoved(currentPos);
                                        notifyItemRangeChanged(currentPos, bgImages.size() - currentPos);
                                    }
                                });
                            });
                        }
                    }
                }
            });
            return;
        }

        BgViewHolder bgHolder = (BgViewHolder) holder;
        Leaflet_BgImage bgImage = bgImages.get(position);
        if (bgImage == null) return;

        bgHolder.content_layout.getLayoutParams().width = cellWidth;
        bgHolder.content_layout.getLayoutParams().height = cellHeight;
        bgHolder.content_layout.invalidate();

        bgHolder.iv_image.getLayoutParams().width = cellWidth;
        bgHolder.iv_image.getLayoutParams().height = cellHeight;
        bgHolder.iv_image.invalidate();

        int premiumPostCount = preferenceClass.getInt("PremiumPostCount", 3);
        boolean isPremium = (premiumPostCount > 0 && position > 0 && position % premiumPostCount == 0);
        bgImage.setPremium(isPremium);
        boolean isLockFeatureEnabled = preferenceClass.isPremiumPosterLockEnabled();
        boolean isBlurEnabled = preferenceClass.isPremiumPosterBlurEnabled();
        boolean showLockVisuals = isPremium && isLockFeatureEnabled;

        if (showLockVisuals) {
            bgHolder.iv_lock.setVisibility(View.VISIBLE);
            if (bgHolder.fl_center_lock != null) {
                bgHolder.fl_center_lock.setVisibility(View.VISIBLE);
            }
            if (bgHolder.view_blur_overlay != null) {
                bgHolder.view_blur_overlay.setVisibility(isBlurEnabled ? View.VISIBLE : View.GONE);
            }
        } else {
            bgHolder.iv_lock.setVisibility(isPremium ? View.VISIBLE : View.GONE);
            if (bgHolder.fl_center_lock != null) {
                bgHolder.fl_center_lock.setVisibility(View.GONE);
            }
            if (bgHolder.view_blur_overlay != null) {
                bgHolder.view_blur_overlay.setVisibility(View.GONE);
            }
        }

        bgHolder.content_layout.setVisibility(View.VISIBLE);
        if (showLockVisuals && isBlurEnabled) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                bgHolder.iv_image.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(25f, 25f, android.graphics.Shader.TileMode.CLAMP));
            }
            Glide.with(activity)
                    .load(bgImage.getThumb_url())
                    .transform(new com.bumptech.glide.load.resource.bitmap.CenterCrop(), new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_BlurTransformation(15, 3))
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(bgHolder.iv_image);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                bgHolder.iv_image.setRenderEffect(null);
            }
            Glide.with(activity)
                    .load(bgImage.getThumb_url())
                    .transform(new com.bumptech.glide.load.resource.bitmap.CenterCrop())
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(bgHolder.iv_image);
        }

        String bgId = String.valueOf(bgImage.getId());
        boolean isLiked = Leaflet_FavoritesManager.isBackgroundLiked(activity, bgId);
        bgHolder.iv_like.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        bgHolder.iv_like.setOnClickListener(v -> {
            if (Leaflet_FavoritesManager.isBackgroundLiked(activity, bgId)) {
                Leaflet_FavoritesManager.removeLikedBackground(activity, bgId);
                bgHolder.iv_like.setImageResource(R.drawable.ic_heart_outline);
            } else {
                Leaflet_FavoritesManager.addLikedBackground(activity, bgImage);
                bgHolder.iv_like.setImageResource(R.drawable.ic_heart_filled);
            }
        });

        View.OnClickListener bgClickListener = v -> {
            if (bgHolder.iv_image.getDrawable() == null) {
                return;
            }

            int clickPos = bgHolder.getAdapterPosition();
            if (clickPos == RecyclerView.NO_POSITION) clickPos = position;

            android.content.Intent intent = new android.content.Intent(activity, com.online.flyer.design.postermaker.Leaflet_activities.ItemPreviewActivity.class);
            intent.putExtra(com.online.flyer.design.postermaker.Leaflet_activities.ItemPreviewActivity.EXTRA_PREVIEW_MODE, com.online.flyer.design.postermaker.Leaflet_activities.ItemPreviewActivity.MODE_BACKGROUND);
            intent.putExtra(com.online.flyer.design.postermaker.Leaflet_activities.ItemPreviewActivity.EXTRA_BG_IMAGES, bgImages);
            intent.putExtra(com.online.flyer.design.postermaker.Leaflet_activities.ItemPreviewActivity.EXTRA_INITIAL_POSITION, clickPos);
            intent.putExtra(com.online.flyer.design.postermaker.Leaflet_activities.ItemPreviewActivity.EXTRA_CATEGORY_NAME, categoryName);
            intent.putExtra("target_bg_id", bgImage.getId());

            int pCount = preferenceClass.getInt("PremiumPostCount", 3);
            boolean isPrem = (pCount > 0 && clickPos > 0 && clickPos % pCount == 0);
            intent.putExtra("target_is_premium", isPrem);
            boolean lockEnabled = preferenceClass.isPremiumPosterLockEnabled();
            boolean showPopup = preferenceClass.isPremiumRewardPopupEnabled();

            if (isPrem && lockEnabled && showPopup) {
                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils.getInstance().rewardDialog(
                        activity,
                        "Use Background(One Time)",
                        "Use premium background by watching Ads",
                        dialog -> {
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            String adType = preferenceClass.getPremiumPosterAdType();
                            if ("Reward".equalsIgnoreCase(adType)) {
                                com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_RewardVideoManager.showRewardVideoAd(activity, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager.OnRewardAdLoadInterface() {
                                    @Override
                                    public void onAdClose() {
                                        intent.putExtra("is_unlocked_by_reward", true);
                                        activity.startActivityForResult(intent, 2001);
                                    }
                                });
                            } else {
                                com.online.flyer.design.postermaker.Leaflet_MyApplication.showInterstitialAdWithOutCount(activity, () -> {
                                    intent.putExtra("is_unlocked_by_reward", true);
                                    activity.startActivityForResult(intent, 2001);
                                });
                            }
                        },
                        dialog -> {
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                        }
                );
            } else if (!isPrem) {
                com.online.flyer.design.postermaker.Leaflet_MyApplication.showInterstitialAd(activity, () -> {
                    activity.startActivityForResult(intent, 2001);
                });
            } else {
                activity.startActivityForResult(intent, 2001);
            }
        };

        bgHolder.iv_image.setOnClickListener(bgClickListener);
        if (bgHolder.fl_center_lock != null) {
            bgHolder.fl_center_lock.setOnClickListener(bgClickListener);
        }
        if (bgHolder.view_blur_overlay != null) {
            bgHolder.view_blur_overlay.setOnClickListener(bgClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return bgImages.size();
    }

    public interface OnBgClickListener {
        void onClick(String path, boolean premium);
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout native_banner_ad_container;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            native_banner_ad_container = itemView.findViewById(R.id.native_banner_ad_container);
        }
    }

    public static class BgViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout content_layout;
        public final ImageView iv_image;
        public final View iv_lock;
        public final ImageView iv_like;
        public final View fl_center_lock;
        public final View view_blur_overlay;

        public BgViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            iv_lock = itemView.findViewById(R.id.iv_lock);
            iv_like = itemView.findViewById(R.id.iv_like);
            content_layout = itemView.findViewById(R.id.content_layout);
            fl_center_lock = itemView.findViewById(R.id.fl_center_lock);
            view_blur_overlay = itemView.findViewById(R.id.view_blur_overlay);
        }
    }

    public static class MyViewHolder extends BgViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
