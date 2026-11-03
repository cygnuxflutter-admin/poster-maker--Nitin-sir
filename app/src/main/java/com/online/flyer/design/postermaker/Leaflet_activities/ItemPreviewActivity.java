package com.online.flyer.design.postermaker.Leaflet_activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_DlBgAsync;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_RewardVideoManager;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage;
import com.online.flyer.design.postermaker.Leaflet_model.PreviewItem;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_StickerModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_TemplateModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_TextModel;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_DlTemplateAsync;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_GetPosDetail;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_BlurTransformation;

import com.online.flyer.design.postermaker.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * High-performance, immersive Fullscreen Swipeable Preview Screen (Reels / Stories style)
 * for both Templates and Backgrounds.
 * 
 * Exact features:
 * 1. ViewPager2 with smooth vertical swiping (Swipe up for more)
 * 2. Top bar: Back arrow, Title (Category Name), Subtitle ("Background 2 of 20" / "Template 2 of 27")
 * 3. Card: Rounded corners (16dp), full high-res preview, floating top-right heart like button,
 *    and floating bottom-center pill "Swipe up for more"
 * 4. AdMob Native Ad: Remote-Config controlled with Shimmer placeholder & graceful collapse on failure
 * 5. Primary Action CTA Button: Gradient purple-to-magenta with pencil icon:
 *    - "Edit Template" (for Templates)
 *    - "Use Background" (for Backgrounds)
 */
public class ItemPreviewActivity extends AppCompatActivity {

    private static final String TAG = "ItemPreviewActivity";

    // Intent Extra Constants
    public static final String EXTRA_PREVIEW_MODE = "extra_preview_mode"; // "mode_template" or "mode_background"
    public static final String MODE_TEMPLATE = "mode_template";
    public static final String MODE_BACKGROUND = "mode_background";

    public static final String EXTRA_PREVIEW_ITEMS = "extra_preview_items";
    public static final String EXTRA_POSTER_IMAGES = "extra_poster_images";
    public static final String EXTRA_BG_IMAGES = "extra_bg_images";
    public static final String EXTRA_IMAGE_URLS = "extra_image_urls";
    public static final String EXTRA_INITIAL_POSITION = "extra_initial_position";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";

    public static final String EXTRA_SINGLE_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_SINGLE_TITLE = "extra_title";
    public static final String EXTRA_SINGLE_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_SINGLE_CAT_ID = "extra_cat_id";
    public static final String EXTRA_SINGLE_POST_ID = "extra_post_id";
    public static final String EXTRA_SINGLE_IS_PREMIUM = "extra_is_premium";

    // Views
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private LinearLayout btnUseTemplate;
    private ImageView ivBtnIcon;
    private TextView tvBtnText;

    private ViewPager2 viewPagerPreview;
    private PreviewPagerAdapter pagerAdapter;
    private final List<PreviewItem> previewItemList = new ArrayList<>();
    private int currentPosition = 0;
    private String previewMode = MODE_TEMPLATE;
    private String categoryName = "";

    // Native Ad Container & Remote Config Controls
    private RelativeLayout rlPreviewNativeAd;
    private ShimmerFrameLayout shimmerNativeAd;
    private FrameLayout flNativeAdContainer;
    private NativeAd loadedNativeAd = null;
    private boolean isAdLoaded = false;

    // Remote Config Ad settings
    private boolean adEnabled = true;
    private String adType = "compact";
    private int adHeightDp = 95;
    private String adUnitId = "";

    // Utilities
    private FirebaseAnalytics mFirebaseAnalytics;
    private Leaflet_PreferenceClass preferenceClass;

    // Template loading & progress
    private Dialog loadingDialog;
    private ArrayList<Leaflet_TemplateModel> templateModels;
    private ArrayList<Leaflet_StickerModel> sticker_model;
    private ArrayList<Leaflet_TextModel> text_model;
    private final ArrayList<String> templateDownloadList = new ArrayList<>();
    private boolean isRewarded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Light Status Bar with dark icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        setContentView(R.layout.activity_item_preview);

        preferenceClass = new Leaflet_PreferenceClass(this);
        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        } catch (Exception ignored) {}

        initViews();
        parseIntentData();
        setupListeners();
        setupViewPager();
        loadRemoteConfigAndNativeAd();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        btnUseTemplate = findViewById(R.id.btnUseTemplate);
        ivBtnIcon = findViewById(R.id.ivBtnIcon);
        tvBtnText = findViewById(R.id.tvBtnText);

        viewPagerPreview = findViewById(R.id.viewPagerPreview);

        rlPreviewNativeAd = findViewById(R.id.rl_preview_native_ad);
        shimmerNativeAd = findViewById(R.id.shimmerNativeAd);
        flNativeAdContainer = findViewById(R.id.flNativeAdContainer);
    }

    @SuppressWarnings("unchecked")
    private void parseIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;

        if (intent.hasExtra(EXTRA_PREVIEW_MODE)) {
            previewMode = intent.getStringExtra(EXTRA_PREVIEW_MODE);
        }

        categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);

        // 1. Direct PreviewItem list
        if (intent.hasExtra(EXTRA_PREVIEW_ITEMS)) {
            try {
                Serializable s = intent.getSerializableExtra(EXTRA_PREVIEW_ITEMS);
                if (s instanceof List) {
                    previewItemList.addAll((List<PreviewItem>) s);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing EXTRA_PREVIEW_ITEMS", e);
            }
        }

        // 2. Leaflet_PosterImage list (Templates)
        if (previewItemList.isEmpty() && intent.hasExtra(EXTRA_POSTER_IMAGES)) {
            try {
                Serializable s = intent.getSerializableExtra(EXTRA_POSTER_IMAGES);
                if (s instanceof List) {
                    List<Leaflet_PosterImage> posterList = (List<Leaflet_PosterImage>) s;
                    int premiumInterval = preferenceClass.getInt("PremiumPostCount", 3);
                    int rawInitialPosition = intent.getIntExtra("initialPosition", intent.getIntExtra(EXTRA_INITIAL_POSITION, 0));
                    int extraCatId = intent.getIntExtra(EXTRA_SINGLE_CAT_ID, intent.getIntExtra("extra_cat_id", 0));
                    int targetPostId = intent.getIntExtra("target_post_id", -1);
                    boolean targetIsPremium = intent.getBooleanExtra("target_is_premium", false);
                    int targetItemIndex = -1;
                    for (int i = 0; i < posterList.size(); i++) {
                        Leaflet_PosterImage img = posterList.get(i);
                        if (img == null) continue;
                        if (img.getCat_id() == 0 && extraCatId != 0) {
                            img.setCat_id(extraCatId);
                        }
                        boolean isThisTheTarget = false;
                        if (targetPostId != -1 && img.getPost_id() == targetPostId) {
                            isThisTheTarget = true;
                        } else if (targetPostId == -1 && i == rawInitialPosition) {
                            isThisTheTarget = true;
                        }
                        if (isThisTheTarget && targetItemIndex == -1) {
                            targetItemIndex = previewItemList.size();
                        }
                        boolean isPremium = img.isPremium();
                        if (!isPremium && premiumInterval > 0 && i > 0 && i % premiumInterval == 0) {
                            isPremium = true;
                        }
                        if (isThisTheTarget && targetIsPremium) {
                            isPremium = true;
                        }
                        PreviewItem pi = PreviewItem.fromPosterImage(img, categoryName, isPremium);
                        if (pi != null) {
                            if (pi.getCatId() == 0 && extraCatId != 0) {
                                pi.setCatId(extraCatId);
                            }
                            previewItemList.add(pi);
                        }
                    }
                    if (targetItemIndex != -1) {
                        currentPosition = targetItemIndex;
                        if (intent.getBooleanExtra("is_unlocked_by_reward", false) && targetItemIndex < previewItemList.size()) {
                            previewItemList.get(targetItemIndex).setPreviewUnlocked(true);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing EXTRA_POSTER_IMAGES", e);
            }
        }

        // 3. Leaflet_BgImage list (Backgrounds)
        if (previewItemList.isEmpty() && intent.hasExtra(EXTRA_BG_IMAGES)) {
            try {
                Serializable s = intent.getSerializableExtra(EXTRA_BG_IMAGES);
                if (s instanceof List) {
                    List<Leaflet_BgImage> bgList = (List<Leaflet_BgImage>) s;
                    int premiumInterval = preferenceClass.getInt("PremiumPostCount", 3);
                    int rawInitialPosition = intent.getIntExtra("initialPosition", intent.getIntExtra(EXTRA_INITIAL_POSITION, 0));
                    int targetBgId = intent.getIntExtra("target_bg_id", -1);
                    boolean targetIsPremium = intent.getBooleanExtra("target_is_premium", false);
                    int targetItemIndex = -1;
                    for (int i = 0; i < bgList.size(); i++) {
                        Leaflet_BgImage bg = bgList.get(i);
                        if (bg == null) continue;
                        boolean isThisTheTarget = false;
                        if (targetBgId != -1 && bg.getId() == targetBgId) {
                            isThisTheTarget = true;
                        } else if (targetBgId == -1 && i == rawInitialPosition) {
                            isThisTheTarget = true;
                        }
                        if (isThisTheTarget && targetItemIndex == -1) {
                            targetItemIndex = previewItemList.size();
                        }
                        boolean isPremium = bg.isPremium();
                        if (!isPremium && premiumInterval > 0 && i > 0 && i % premiumInterval == 0) {
                            isPremium = true;
                        }
                        if (isThisTheTarget && targetIsPremium) {
                            isPremium = true;
                        }
                        PreviewItem pi = new PreviewItem(
                                String.valueOf(bg.getId()),
                                categoryName != null ? categoryName : "Background",
                                "Background " + (previewItemList.size() + 1),
                                bg.getImage_url() != null ? bg.getImage_url() : bg.getThumb_url(),
                                "1:1",
                                0,
                                bg.getId(),
                                isPremium
                        );
                        previewItemList.add(pi);
                    }
                    if (targetItemIndex != -1) {
                        currentPosition = targetItemIndex;
                        if (intent.getBooleanExtra("is_unlocked_by_reward", false) && targetItemIndex < previewItemList.size()) {
                            previewItemList.get(targetItemIndex).setPreviewUnlocked(true);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing EXTRA_BG_IMAGES", e);
            }
        }

        // 4. List of image URLs
        if (previewItemList.isEmpty() && intent.hasExtra(EXTRA_IMAGE_URLS)) {
            try {
                List<String> urls = intent.getStringArrayListExtra(EXTRA_IMAGE_URLS);
                if (urls == null || urls.isEmpty()) {
                    String[] arr = intent.getStringArrayExtra(EXTRA_IMAGE_URLS);
                    if (arr != null && arr.length > 0) {
                        urls = java.util.Arrays.asList(arr);
                    }
                }
                if (urls != null) {
                    for (int i = 0; i < urls.size(); i++) {
                        PreviewItem pi = new PreviewItem(
                                String.valueOf(i),
                                categoryName != null ? categoryName : (MODE_BACKGROUND.equals(previewMode) ? "Background" : "Template"),
                                (MODE_BACKGROUND.equals(previewMode) ? "Background " : "Template ") + (i + 1),
                                urls.get(i), "1:1", 0, i, (i % 3 == 0)
                        );
                        previewItemList.add(pi);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing EXTRA_IMAGE_URLS", e);
            }
        }

        // 5. Single item passed
        if (previewItemList.isEmpty() && intent.hasExtra(EXTRA_SINGLE_IMAGE_URL)) {
            String imgUrl = intent.getStringExtra(EXTRA_SINGLE_IMAGE_URL);
            String title = intent.getStringExtra(EXTRA_SINGLE_TITLE);
            String subtitle = intent.getStringExtra(EXTRA_SINGLE_SUBTITLE);
            int catId = intent.getIntExtra(EXTRA_SINGLE_CAT_ID, 0);
            int postId = intent.getIntExtra(EXTRA_SINGLE_POST_ID, 0);
            boolean isPremium = intent.getBooleanExtra(EXTRA_SINGLE_IS_PREMIUM, false);

            previewItemList.add(new PreviewItem(
                    String.valueOf(postId),
                    title != null ? title : (categoryName != null ? categoryName : "Design Preview"),
                    subtitle != null ? subtitle : "",
                    imgUrl, "1:1", catId, postId, isPremium
            ));
        }

        // 6. Default Fallbacks if opened empty
        if (previewItemList.isEmpty()) {
            if (MODE_BACKGROUND.equals(previewMode)) {
                categoryName = "Abstract";
                previewItemList.add(new PreviewItem("1", "Abstract", "Background 1 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f4bd6b15e_28-09-2026__1790571709.png", "1:1", 1, 1, false));
                previewItemList.add(new PreviewItem("2", "Abstract", "Background 2 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f6676bf27_28-09-2026__1790572135.png", "1:1", 1, 2, false));
                previewItemList.add(new PreviewItem("3", "Abstract", "Background 3 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f6952e73a_28-09-2026__1790572181.png", "1:1", 1, 3, false));
                previewItemList.add(new PreviewItem("4", "Abstract", "Background 4 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f6dfd87c9_28-09-2026__1790572255.png", "1:1", 1, 4, true));
            } else {
                categoryName = "Raksha Bandhan";
                previewItemList.add(new PreviewItem("1", "Raksha Bandhan", "Template 1 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f4bd6b15e_28-09-2026__1790571709.png", "1:1", 57, 1, false));
                previewItemList.add(new PreviewItem("2", "Raksha Bandhan", "Template 2 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f6676bf27_28-09-2026__1790572135.png", "1:1", 57, 2, false));
                previewItemList.add(new PreviewItem("3", "Raksha Bandhan", "Template 3 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f6952e73a_28-09-2026__1790572181.png", "1:1", 57, 3, false));
                previewItemList.add(new PreviewItem("4", "Raksha Bandhan", "Template 4 of 4",
                        "https://cygnux.in/postermaker/upload/thumb_image/6ab9f6dfd87c9_28-09-2026__1790572255.png", "1:1", 57, 4, true));
            }
        }

        // Initial position
        if (currentPosition < 0 || currentPosition >= previewItemList.size()) {
            currentPosition = intent.getIntExtra("initialPosition", intent.getIntExtra(EXTRA_INITIAL_POSITION, 0));
        }
        if (currentPosition < 0 || currentPosition >= previewItemList.size()) {
            currentPosition = 0;
        }

        // Configure CTA Button text based on mode
        if (MODE_BACKGROUND.equals(previewMode)) {
            tvBtnText.setText("Use Background");
        } else {
            tvBtnText.setText("Edit Template");
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnUseTemplate.setOnClickListener(v -> onActionClicked());
    }

    private void setupViewPager() {
        // Vertical ViewPager2 (Swipe up for more)
        viewPagerPreview.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        final int initPos = currentPosition;
        pagerAdapter = new PreviewPagerAdapter(this, previewItemList, previewMode, initPos, (position, isLiked) -> {
            // Heart clicked callback
            toggleLike(position);
        }, position -> {
            if (viewPagerPreview != null && position + 1 < previewItemList.size()) {
                viewPagerPreview.setCurrentItem(position + 1, true);
            }
        }, position -> {
            promptUnlockPreview(position);
        });
        viewPagerPreview.setAdapter(pagerAdapter);

        // Immediate entry at initialPosition
        if (!previewItemList.isEmpty()) {
            viewPagerPreview.setCurrentItem(initPos, false);
            updateTopBar(initPos);
            viewPagerPreview.post(() -> {
                viewPagerPreview.setCurrentItem(initPos, false);
                updateTopBar(initPos);
            });
        }

        viewPagerPreview.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                if (position != initPos && pagerAdapter != null) {
                    pagerAdapter.setHasUserSwiped(true);
                }
                updateTopBar(position);

                // Fire Firebase Analytics event
                if (mFirebaseAnalytics != null && position >= 0 && position < previewItemList.size()) {
                    PreviewItem item = previewItemList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("item_position", position);
                    bundle.putString("item_id", item.getId());
                    bundle.putString("item_title", item.getTitle());
                    bundle.putString("preview_mode", previewMode);
                    mFirebaseAnalytics.logEvent("preview_item_swiped", bundle);
                }
            }
        });
    }

    private void updateTopBar(int position) {
        if (position < 0 || position >= previewItemList.size()) return;
        PreviewItem item = previewItemList.get(position);

        String title = (categoryName != null && !categoryName.isEmpty()) ? categoryName : item.getTitle();
        tvTitle.setText(title);

        int totalCount = previewItemList.size();
        int currentNum = position + 1;
        if (MODE_BACKGROUND.equals(previewMode)) {
            tvSubtitle.setText("Background " + currentNum + " of " + totalCount);
        } else {
            tvSubtitle.setText("Template " + currentNum + " of " + totalCount);
        }
        updateButtonStateForPosition(position);
    }

    private void toggleLike(int position) {
        if (position < 0 || position >= previewItemList.size()) return;
        PreviewItem item = previewItemList.get(position);
        String itemId = item.getId();

        boolean currentlyLiked;
        if (MODE_BACKGROUND.equals(previewMode)) {
            currentlyLiked = Leaflet_FavoritesManager.isBackgroundLiked(this, itemId);
            if (!currentlyLiked) {
                Leaflet_BgImage bg = new Leaflet_BgImage(item.getPostId(), item.getImageUrl(), item.getImageUrl());
                Leaflet_FavoritesManager.addLikedBackground(this, bg);
            } else {
                Leaflet_FavoritesManager.removeLikedBackground(this, itemId);
            }
        } else {
            currentlyLiked = Leaflet_FavoritesManager.isPosterLiked(this, itemId);
            if (!currentlyLiked) {
                Leaflet_PosterImage pImg = new Leaflet_PosterImage(
                        String.valueOf(item.getPostId()),
                        item.getImageUrl(),
                        item.getRatio(),
                        String.valueOf(item.getCatId())
                );
                Leaflet_FavoritesManager.addLikedPoster(this, pImg);
            } else {
                Leaflet_FavoritesManager.removeLikedPoster(this, itemId);
            }
        }

        if (pagerAdapter != null) {
            pagerAdapter.notifyItemChanged(position);
        }
    }

    private void showLoadingDialog() {
        try {
            if (loadingDialog == null) {
                loadingDialog = Leaflet_MaterialDialogUtils.getInstance().createAnimationDialog(this);
                if (loadingDialog.getWindow() != null) {
                    loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }
                loadingDialog.setCancelable(false);
            }
            if (!isFinishing() && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }
        } catch (Exception ignored) {}
    }

    private void dismissLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception ignored) {}
    }

    private void resetButtonState() {
        updateButtonStateForPosition(currentPosition);
    }

    private void updateButtonStateForPosition(int position) {
        if (position < 0 || position >= previewItemList.size()) return;
        runOnUiThread(() -> {
            btnUseTemplate.setEnabled(true);
            if (MODE_BACKGROUND.equals(previewMode)) {
                tvBtnText.setText("Use Background");
            } else {
                tvBtnText.setText("Edit Template");
            }
        });
    }

    private void promptUnlockPreview(final int position) {
        if (position < 0 || position >= previewItemList.size()) return;
        final PreviewItem item = previewItemList.get(position);
        if (!item.isPremium() || item.isPreviewUnlocked()) return;

        boolean lockEnabled = preferenceClass.isPremiumPosterLockEnabled();
        boolean showPopup = preferenceClass.isPremiumRewardPopupEnabled();
        if (!lockEnabled || !showPopup) {
            item.setPreviewUnlocked(true);
            if (pagerAdapter != null) pagerAdapter.notifyItemChanged(position);
            return;
        }

        String title = MODE_BACKGROUND.equals(previewMode) ? "Use Background (One Time)" : "Use Template (One Time)";
        String desc = MODE_BACKGROUND.equals(previewMode) ? "Watch a short video ad to unlock and use this premium background." : "Watch a short video ad to unlock and use this premium template.";
        String itemType = MODE_BACKGROUND.equals(previewMode) ? "backgrounds" : "templates";

        Leaflet_MaterialDialogUtils.getInstance().rewardDialog(
                this,
                title,
                desc,
                itemType,
                dialog -> {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    String adType = preferenceClass.getPremiumPosterAdType();
                    if ("Reward".equalsIgnoreCase(adType)) {
                        Leaflet_RewardVideoManager.showRewardVideoAd(ItemPreviewActivity.this, new Leaflet_InterstitialAdManager.OnRewardAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                item.setPreviewUnlocked(true);
                                if (pagerAdapter != null) pagerAdapter.notifyItemChanged(position);
                                Toast.makeText(ItemPreviewActivity.this, "Unlocked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Leaflet_MyApplication.showInterstitialAdWithOutCount(ItemPreviewActivity.this, () -> {
                            item.setPreviewUnlocked(true);
                            if (pagerAdapter != null) pagerAdapter.notifyItemChanged(position);
                            Toast.makeText(ItemPreviewActivity.this, "Unlocked!", Toast.LENGTH_SHORT).show();
                        });
                    }
                },
                dialog -> {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                }
        );
    }

    private void promptEditUnlock(final int position, final Runnable onUnlockedCallback) {
        if (position < 0 || position >= previewItemList.size()) return;
        final PreviewItem item = previewItemList.get(position);
        if (!item.isPremium()) {
            if (onUnlockedCallback != null) onUnlockedCallback.run();
            return;
        }

        boolean lockEnabled = preferenceClass.isPremiumPosterLockEnabled();
        boolean showPopup = preferenceClass.isPremiumRewardPopupEnabled();
        if (!lockEnabled || !showPopup) {
            if (onUnlockedCallback != null) onUnlockedCallback.run();
            return;
        }

        String title = MODE_BACKGROUND.equals(previewMode) ? "Use Background (One Time)" : "Use Template (One Time)";
        String desc = MODE_BACKGROUND.equals(previewMode) ? "Watch a short video ad to unlock and use this premium background." : "Watch a short video ad to unlock and use this premium template.";
        String itemType = MODE_BACKGROUND.equals(previewMode) ? "backgrounds" : "templates";

        Leaflet_MaterialDialogUtils.getInstance().rewardDialog(
                this,
                title,
                desc,
                itemType,
                dialog -> {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    String adType = preferenceClass.getPremiumPosterAdType();
                    if ("Reward".equalsIgnoreCase(adType)) {
                        Leaflet_RewardVideoManager.showRewardVideoAd(ItemPreviewActivity.this, new Leaflet_InterstitialAdManager.OnRewardAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                item.setPreviewUnlocked(true);
                                if (pagerAdapter != null) pagerAdapter.notifyItemChanged(position);
                                if (onUnlockedCallback != null) onUnlockedCallback.run();
                            }
                        });
                    } else {
                        Leaflet_MyApplication.showInterstitialAdWithOutCount(ItemPreviewActivity.this, () -> {
                            item.setPreviewUnlocked(true);
                            if (pagerAdapter != null) pagerAdapter.notifyItemChanged(position);
                            if (onUnlockedCallback != null) onUnlockedCallback.run();
                        });
                    }
                },
                dialog -> {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    resetButtonState();
                }
        );
    }

    private void onActionClicked() {
        int actualPos = (viewPagerPreview != null) ? viewPagerPreview.getCurrentItem() : currentPosition;
        if (actualPos < 0 || actualPos >= previewItemList.size()) return;
        currentPosition = actualPos;
        PreviewItem item = previewItemList.get(actualPos);

        if (MODE_BACKGROUND.equals(previewMode)) {
            // Apply / Use Background
            handleUseBackground(item, actualPos);
        } else {
            // Edit Template
            handleEditTemplate(item, actualPos);
        }
    }

    private void handleUseBackground(PreviewItem item, int position) {
        if (!Leaflet_NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
            return;
        }

        String bgUrl = item.getImageUrl();
        if (bgUrl == null || bgUrl.isEmpty()) {
            Toast.makeText(this, "Invalid background URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (item.isPremium() && preferenceClass.isPremiumPosterLockEnabled() && preferenceClass.isPremiumRewardPopupEnabled()) {
            promptEditUnlock(position, () -> downloadAndOpenBackground(bgUrl));
        } else {
            downloadAndOpenBackground(bgUrl);
        }
    }

    private void downloadAndOpenBackground(String bgUrl) {
        tvBtnText.setText("Opening...");
        btnUseTemplate.setEnabled(false);
        showLoadingDialog();

        ArrayList<String> strings = new ArrayList<>();
        strings.add(bgUrl);

        new Leaflet_DlBgAsync(this, strings, new Leaflet_DlBgAsync.OnDownloadTemplateListener() {
            @Override
            public void onDownloadComplete() {
                dismissLoadingDialog();
                String localPath = Leaflet_FileUtils.getFile(ItemPreviewActivity.this, strings.get(0));
                Intent intent = new Intent(ItemPreviewActivity.this, Leaflet_PosterEditActivity.class);
                intent.putExtra("bg_path", localPath);
                intent.putExtra("loadUserFrame", true);
                intent.putExtra("Temp_Type", "MY_TEMP");
                startActivity(intent);
                finish();
            }

            @Override
            public void onError() {
                dismissLoadingDialog();
                resetButtonState();
                Toast.makeText(ItemPreviewActivity.this, "Something went wrong downloading background", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void handleEditTemplate(PreviewItem item, int position) {
        if (!Leaflet_NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
            return;
        }

        int catId = item.getCatId();
        int postId = item.getPostId();
        if (catId == 0) {
            catId = getIntent().getIntExtra(EXTRA_SINGLE_CAT_ID, getIntent().getIntExtra("extra_cat_id", 0));
        }

        if (catId == 0 || postId == 0) {
            Toast.makeText(this, "Invalid template data", Toast.LENGTH_SHORT).show();
            return;
        }

        final int finalCatId = catId;
        final int finalPostId = postId;

        if (item.isPremium() && preferenceClass.isPremiumPosterLockEnabled() && preferenceClass.isPremiumRewardPopupEnabled()) {
            promptEditUnlock(position, () -> {
                isRewarded = true;
                startLoadingAndOpenTemplate(finalCatId, finalPostId);
            });
        } else {
            startLoadingAndOpenTemplate(finalCatId, finalPostId);
        }
    }

    private void startLoadingAndOpenTemplate(int catId, int postId) {
        tvBtnText.setText("Opening...");
        btnUseTemplate.setEnabled(false);
        showLoadingDialog();
        loadPosterData(preferenceClass.getDataType("field_0"), catId, postId);
    }

    private void loadPosterData(String key, final int cat_id, final int pos_id) {
        String requestUrl = preferenceClass.getDataType("field_1") + preferenceClass.getDataType("field_34") + preferenceClass.getDataType("field_36");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                int error = jsonObject.getInt(preferenceClass.getDataType("field_3"));
                if (error == 0 || error == 404) {
                    dismissLoadingDialog();
                    resetButtonState();
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog2(ItemPreviewActivity.this, getResources().getString(R.string.something_went_wrong));
                    return;
                }

                if (error == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray(preferenceClass.getDataType("field_4"));
                    if (jsonArray.length() == 0) {
                        dismissLoadingDialog();
                        resetButtonState();
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog2(ItemPreviewActivity.this, getResources().getString(R.string.something_went_wrong));
                    } else {
                        new Leaflet_GetPosDetail(preferenceClass, jsonArray, new Leaflet_GetPosDetail.OnGetDataListener() {
                            @Override
                            public void onGetDataComplete(ArrayList<Leaflet_TemplateModel> posterCos) {
                                templateModels = posterCos;
                                sticker_model = posterCos.get(0).getSticker_model();
                                text_model = posterCos.get(0).getText_model();
                                templateDownloadList.clear();
                                templateDownloadList.add(posterCos.get(0).getBack_image());

                                for (int i = 0; i < sticker_model.size(); i++) {
                                    if (!sticker_model.get(i).getSt_image().equals("")) {
                                        templateDownloadList.add(preferenceClass.getDataType("field_1") + "/" + sticker_model.get(i).getSt_image());
                                    }
                                }

                                if (preferenceClass.getInt("download") == 0) {
                                    for (int i = 0; i < text_model.size(); i++) {
                                        templateDownloadList.add(preferenceClass.getDataType("field_37") + text_model.get(i).getFont_family());
                                    }
                                }

                                new Leaflet_DlTemplateAsync(ItemPreviewActivity.this, templateDownloadList, new Leaflet_DlTemplateAsync.OnDownloadTemplateListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        dismissLoadingDialog();
                                        if (Leaflet_NetworkUtils.isNetworkAvailable(ItemPreviewActivity.this)) {
                                            if (isRewarded) {
                                                startPosterEditIntent();
                                                isRewarded = false;
                                                return;
                                            }
                                            Leaflet_MyApplication.showInterstitialAd(ItemPreviewActivity.this, ItemPreviewActivity.this::startPosterEditIntent);
                                        } else {
                                            resetButtonState();
                                            Leaflet_MaterialDialogUtils.getInstance().errorDialog3(ItemPreviewActivity.this, "Make sure you are connected to internet!!");
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        dismissLoadingDialog();
                                        resetButtonState();
                                        Leaflet_MaterialDialogUtils.getInstance().errorDialog3(ItemPreviewActivity.this, getResources().getString(R.string.something_went_wrong));
                                    }
                                }).execute();
                            }

                            @Override
                            public void onError() {
                                dismissLoadingDialog();
                                resetButtonState();
                                Leaflet_MaterialDialogUtils.getInstance().errorDialog3(ItemPreviewActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        }).execute();
                    }
                }
            } catch (Exception e) {
                dismissLoadingDialog();
                resetButtonState();
                e.printStackTrace();
            }
        }, error -> {
            dismissLoadingDialog();
            resetButtonState();
            Leaflet_MaterialDialogUtils.getInstance().errorDialog(ItemPreviewActivity.this, getResources().getString(R.string.something_went_wrong));
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put(preferenceClass.getDataType("field_47"), key);
                postMap.put(preferenceClass.getDataType("field_48"), "1");
                postMap.put(preferenceClass.getDataType("field_46"), String.valueOf(cat_id));
                postMap.put(preferenceClass.getDataType("field_10"), String.valueOf(pos_id));
                return postMap;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void startPosterEditIntent() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ItemPreviewActivity.this, Leaflet_PosterEditActivity.class);
            intent.putParcelableArrayListExtra("template", templateModels);
            intent.putParcelableArrayListExtra("sticker", sticker_model);
            intent.putParcelableArrayListExtra("text", text_model);
            intent.putExtra("loadUserFrame", false);
            intent.putExtra("Temp_Type", "MY_TEMP");
            startActivity(intent);
            finish();
        }, 300);
    }

    // =========================================================================
    // Dynamic Remote-Config Native Ad Integration
    // =========================================================================

    private void loadRemoteConfigAndNativeAd() {
        adEnabled = true;
        adType = "compact";
        adHeightDp = 95;
        adUnitId = BuildConfig.DEBUG ? "ca-app-pub-3940256099942544/2247696110" : preferenceClass.getAdsId("NativeUnitID");

        // 1. Firebase Remote Config
        try {
            String screenKey = MODE_BACKGROUND.equals(previewMode) ? "backgrounds" : "templates";
            JSONObject screenObj = preferenceClass.getScreenAdConfigJson(screenKey);
            if (screenObj != null && screenObj.has("preview_native_ad")) {
                parsePreviewAdJson(screenObj);
            } else {
                FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
                String configJson = remoteConfig.getString("preview_screen");
                if (configJson != null && !configJson.trim().isEmpty() && configJson.trim().startsWith("{")) {
                    parsePreviewAdJson(new JSONObject(configJson));
                }
            }
        } catch (Exception ignored) {}

        // 2. Firebase Realtime Database
        try {
            FirebaseDatabase.getInstance().getReference("all_data").child("preview_screen").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        try {
                            Object raw = snapshot.getValue();
                            if (raw instanceof String) {
                                parsePreviewAdJson(new JSONObject((String) raw));
                            } else if (snapshot.hasChild("preview_native_ad")) {
                                DataSnapshot adSnap = snapshot.child("preview_native_ad");
                                if (adSnap.hasChild("enabled")) {
                                    adEnabled = "true".equalsIgnoreCase(String.valueOf(adSnap.child("enabled").getValue()));
                                }
                                if (adSnap.hasChild("ad_type")) {
                                    adType = String.valueOf(adSnap.child("ad_type").getValue());
                                }
                                if (adSnap.hasChild("height")) {
                                    adHeightDp = Integer.parseInt(String.valueOf(adSnap.child("height").getValue()));
                                }
                                if (adSnap.hasChild("ad_id")) {
                                    String id = String.valueOf(adSnap.child("ad_id").getValue());
                                    if (!id.isEmpty() && !id.contains("xxxx")) adUnitId = id;
                                }
                                applyAdConfigAndFetch();
                            }
                        } catch (Exception ignored) {}
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } catch (Exception ignored) {}

        applyAdConfigAndFetch();
    }

    private void parsePreviewAdJson(JSONObject obj) {
        try {
            if (obj.has("preview_native_ad")) {
                JSONObject adObj = obj.getJSONObject("preview_native_ad");
                if (adObj.has("enabled")) {
                    adEnabled = adObj.optBoolean("enabled", true);
                }
                if (adObj.has("ad_type")) {
                    adType = adObj.optString("ad_type", "compact");
                }
                if (adObj.has("height")) {
                    adHeightDp = adObj.optInt("height", 95);
                }
                if (adObj.has("ad_id")) {
                    String id = adObj.optString("ad_id", "");
                    if (!id.isEmpty() && !id.contains("xxxx")) {
                        adUnitId = id;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in parsePreviewAdJson", e);
        }
    }

    private void applyAdConfigAndFetch() {
        runOnUiThread(() -> {
            if (!adEnabled) {
                if (shimmerNativeAd != null) shimmerNativeAd.stopShimmer();
                rlPreviewNativeAd.setVisibility(View.GONE);
                return;
            }

            if (isAdLoaded && loadedNativeAd != null) {
                rlPreviewNativeAd.setVisibility(View.VISIBLE);
                return;
            }

            float density = getResources().getDisplayMetrics().density;
            ViewGroup.LayoutParams lp = rlPreviewNativeAd.getLayoutParams();
            lp.height = (int) (adHeightDp * density);
            rlPreviewNativeAd.setLayoutParams(lp);

            rlPreviewNativeAd.setVisibility(View.VISIBLE);
            shimmerNativeAd.setVisibility(View.VISIBLE);
            shimmerNativeAd.startShimmer();

            fetchNativeAd();
        });
    }

    private void fetchNativeAd() {
        String finalAdId = BuildConfig.DEBUG ? "ca-app-pub-3940256099942544/2247696110" : adUnitId;
        if (finalAdId == null || finalAdId.trim().isEmpty() || finalAdId.equals("null")) {
            finalAdId = "ca-app-pub-3940256099942544/2247696110";
        }

        try {
            AdLoader.Builder builder = new AdLoader.Builder(this, finalAdId);
            builder.forNativeAd(nativeAd -> {
                if (isFinishing() || isDestroyed()) {
                    nativeAd.destroy();
                    return;
                }
                if (loadedNativeAd != null) {
                    loadedNativeAd.destroy();
                }
                loadedNativeAd = nativeAd;
                isAdLoaded = true;

                shimmerNativeAd.stopShimmer();
                shimmerNativeAd.setVisibility(View.GONE);

                NativeAdView adView = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.layout_preview_native_ad, flNativeAdContainer, false);
                populateNativeAdView(nativeAd, adView);

                flNativeAdContainer.removeAllViews();
                flNativeAdContainer.addView(adView);
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);

            builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    boolean showShimmerOnFail = preferenceClass.shouldShowNativeShimmerOnFail(MODE_BACKGROUND.equals(previewMode) ? "backgrounds" : "templates");
                    if (!showShimmerOnFail) {
                        if (shimmerNativeAd != null) shimmerNativeAd.stopShimmer();
                        rlPreviewNativeAd.setVisibility(View.GONE);
                    } else {
                        rlPreviewNativeAd.setVisibility(View.VISIBLE);
                    }
                }
            });

            AdLoader adLoader = builder.build();
            adLoader.loadAd(new AdRequest.Builder().build());

        } catch (Exception e) {
            rlPreviewNativeAd.setVisibility(View.GONE);
        }
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }

        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.GONE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        if (mediaView != null) {
            if ("medium".equalsIgnoreCase(adType) && adHeightDp >= 180) {
                mediaView.setVisibility(View.VISIBLE);
                adView.setMediaView(mediaView);
            } else {
                mediaView.setVisibility(View.GONE);
            }
        }

        adView.setNativeAd(nativeAd);
    }

    @Override
    public void onBackPressed() {
        dismissLoadingDialog();
        Leaflet_MyApplication.showInterstitialAd(this, new Leaflet_InterstitialAdManager.OnAdLoadInterface() {
            @Override
            public void onAdClose() {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
        if (loadedNativeAd != null) {
            loadedNativeAd.destroy();
            loadedNativeAd = null;
        }
        if (shimmerNativeAd != null) {
            shimmerNativeAd.stopShimmer();
        }
    }

    // =========================================================================
    // ViewPager2 Adapter
    // =========================================================================

    public interface OnLikeClickListener {
        void onLikeClick(int position, boolean isLiked);
    }

    public interface OnSwipePromptClickListener {
        void onSwipePromptClick(int position);
    }

    public interface OnItemUnlockClickListener {
        void onItemUnlockClick(int position);
    }

    public static class PreviewPagerAdapter extends RecyclerView.Adapter<PreviewPagerAdapter.PreviewViewHolder> {

        private final Context context;
        private final List<PreviewItem> items;
        private final String previewMode;
        private final int initialPosition;
        private final OnLikeClickListener onLikeClickListener;
        private final OnSwipePromptClickListener onSwipePromptClickListener;
        private final OnItemUnlockClickListener onItemUnlockClickListener;
        private final Leaflet_PreferenceClass pref;
        private boolean hasUserSwiped = false;

        public PreviewPagerAdapter(Context context, List<PreviewItem> items, String previewMode, int initialPosition,
                                   OnLikeClickListener listener, OnSwipePromptClickListener swipeListener,
                                   OnItemUnlockClickListener unlockListener) {
            this.context = context;
            this.items = items;
            this.previewMode = previewMode;
            this.initialPosition = initialPosition;
            this.onLikeClickListener = listener;
            this.onSwipePromptClickListener = swipeListener;
            this.onItemUnlockClickListener = unlockListener;
            this.pref = new Leaflet_PreferenceClass(context);
        }

        public void setHasUserSwiped(boolean swiped) {
            if (this.hasUserSwiped != swiped) {
                this.hasUserSwiped = swiped;
                notifyItemChanged(initialPosition);
            }
        }

        @NonNull
        @Override
        public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_preview, parent, false);
            return new PreviewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
            PreviewItem item = items.get(position);

            holder.progressBarLoading.setVisibility(View.VISIBLE);

            boolean isPremium = item.isPremium();
            boolean isLockFeatureEnabled = pref.isPremiumPosterLockEnabled();
            boolean isBlurEnabled = pref.isPremiumPosterBlurEnabled();
            boolean isPreviewUnlocked = item.isPreviewUnlocked();

            // The '👑 AD' badge is ALWAYS visible on any premium template
            if (holder.tvPremiumBadge != null) {
                holder.tvPremiumBadge.setVisibility(isPremium ? View.VISIBLE : View.GONE);
            }
            if (holder.llPremiumBadge != null) {
                holder.llPremiumBadge.setVisibility(View.GONE);
            }

            // Lock and blur overlay only if locked and preview NOT yet unlocked
            boolean showLockVisuals = isPremium && isLockFeatureEnabled && !isPreviewUnlocked;
            if (holder.fl_center_lock != null) {
                holder.fl_center_lock.setVisibility(showLockVisuals ? View.VISIBLE : View.GONE);
                holder.fl_center_lock.setOnClickListener(v -> {
                    if (onItemUnlockClickListener != null) {
                        onItemUnlockClickListener.onItemUnlockClick(position);
                    }
                });
            }
            if (holder.view_blur_overlay != null) {
                holder.view_blur_overlay.setVisibility((showLockVisuals && isBlurEnabled) ? View.VISIBLE : View.GONE);
                holder.view_blur_overlay.setOnClickListener(v -> {
                    if (onItemUnlockClickListener != null) {
                        onItemUnlockClickListener.onItemUnlockClick(position);
                    }
                });
            }

            // Swipe up prompt - ONLY on the first item opened, animated gently
            if (position == initialPosition && !hasUserSwiped) {
                holder.llSwipePrompt.setVisibility(View.VISIBLE);
                startSwipeUpAnimation(holder.llSwipePrompt);
                holder.llSwipePrompt.setOnClickListener(v -> {
                    if (onSwipePromptClickListener != null) {
                        onSwipePromptClickListener.onSwipePromptClick(position);
                    }
                });
            } else {
                stopSwipeUpAnimation(holder.llSwipePrompt);
                holder.llSwipePrompt.setVisibility(View.GONE);
                holder.llSwipePrompt.setOnClickListener(null);
            }

            // Like state
            boolean isLiked;
            if (MODE_BACKGROUND.equals(previewMode)) {
                isLiked = Leaflet_FavoritesManager.isBackgroundLiked(context, item.getId());
            } else {
                isLiked = Leaflet_FavoritesManager.isPosterLiked(context, item.getId());
            }

            holder.ivItemLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            holder.ivItemLike.setColorFilter(isLiked ? Color.parseColor("#EF4444") : Color.parseColor("#0066FF"));

            holder.btnLikeCard.setOnClickListener(v -> {
                // Scale pop animation
                holder.btnLikeCard.animate()
                        .scaleX(1.3f)
                        .scaleY(1.3f)
                        .setDuration(120)
                        .withEndAction(() -> {
                            holder.btnLikeCard.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start();
                            if (onLikeClickListener != null) {
                                onLikeClickListener.onLikeClick(position, !isLiked);
                            }
                        })
                        .start();
            });

            // Glide loader with blur if locked, normal otherwise
            if (showLockVisuals && isBlurEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.ivPreviewImage.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(25f, 25f, android.graphics.Shader.TileMode.CLAMP));
                }
                Glide.with(context)
                        .load(item.getImageUrl())
                        .transform(new com.bumptech.glide.load.resource.bitmap.CenterCrop(), new Leaflet_BlurTransformation(20, 3))
                        .placeholder(R.drawable.bg_preview_ad_card_light)
                        .error(R.drawable.bg_preview_ad_card_light)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.progressBarLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBarLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.ivPreviewImage);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.ivPreviewImage.setRenderEffect(null);
                }
                Glide.with(context)
                        .load(item.getImageUrl())
                        .transform(new com.bumptech.glide.load.resource.bitmap.CenterCrop())
                        .placeholder(R.drawable.bg_preview_ad_card_light)
                        .error(R.drawable.bg_preview_ad_card_light)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.progressBarLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBarLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.ivPreviewImage);
            }
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        private void startSwipeUpAnimation(final View view) {
            if (view == null) return;
            stopSwipeUpAnimation(view);

            float distance = -12f * view.getResources().getDisplayMetrics().density;
            android.animation.ObjectAnimator animator = android.animation.ObjectAnimator.ofFloat(view, "translationY", 0f, distance, 0f);
            animator.setDuration(1200);
            animator.setRepeatCount(android.animation.ValueAnimator.INFINITE);
            animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            animator.start();
            view.setTag(R.id.llSwipePrompt, animator);
        }

        private void stopSwipeUpAnimation(View view) {
            if (view == null) return;
            Object tag = view.getTag(R.id.llSwipePrompt);
            if (tag instanceof android.animation.ObjectAnimator) {
                ((android.animation.ObjectAnimator) tag).cancel();
            }
            view.setTag(R.id.llSwipePrompt, null);
            view.clearAnimation();
            view.setTranslationY(0f);
        }

        @Override
        public void onViewRecycled(@NonNull PreviewViewHolder holder) {
            super.onViewRecycled(holder);
            stopSwipeUpAnimation(holder.llSwipePrompt);
        }

        static class PreviewViewHolder extends RecyclerView.ViewHolder {
            final CardView cardPreview;
            final ImageView ivPreviewImage;
            final ProgressBar progressBarLoading;
            final FrameLayout btnLikeCard;
            final ImageView ivItemLike;
            final LinearLayout llSwipePrompt;
            final View llPremiumBadge;
            final TextView tvPremiumBadge;
            final View view_blur_overlay;
            final FrameLayout fl_center_lock;

            public PreviewViewHolder(@NonNull View itemView) {
                super(itemView);
                cardPreview = itemView.findViewById(R.id.cardPreview);
                ivPreviewImage = itemView.findViewById(R.id.ivPreviewImage);
                progressBarLoading = itemView.findViewById(R.id.progressBarLoading);
                btnLikeCard = itemView.findViewById(R.id.btnLikeCard);
                ivItemLike = itemView.findViewById(R.id.ivItemLike);
                llSwipePrompt = itemView.findViewById(R.id.llSwipePrompt);
                llPremiumBadge = itemView.findViewById(R.id.llPremiumBadge);
                tvPremiumBadge = itemView.findViewById(R.id.tvPremiumBadge);
                view_blur_overlay = itemView.findViewById(R.id.view_blur_overlay);
                fl_center_lock = itemView.findViewById(R.id.fl_center_lock);
            }
        }
    }
}
