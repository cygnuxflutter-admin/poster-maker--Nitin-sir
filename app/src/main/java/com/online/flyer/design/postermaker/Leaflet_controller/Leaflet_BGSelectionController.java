package com.online.flyer.design.postermaker.Leaflet_controller;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_ColorPelleteAdapter;
import com.online.flyer.design.postermaker.Leaflet_fragment.Leaflet_BackgroundPagerAdapter;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_GetBgData;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_SaveBitmapTask;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Leaflet_BGSelectionController {

    public final int CAMERA_INTENT = 905;
    public final int GALLERY_INTENT = 907;
    public String mode;
    public File camera_file;

    private final Activity activity;
    private final FragmentManager supportFragmentManager;
    private final Leaflet_PreferenceClass preferenceClass;
    private MaterialDialog materialDialog;

    public Leaflet_BGSelectionController(Activity activity, FragmentManager supportFragmentManager, Leaflet_PreferenceClass preferenceClass, String mode) {
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.preferenceClass = preferenceClass;
        this.mode = mode;
        if (!"create".equals(mode)) {
            loadBgImages();
        }
    }

    public void loadBgImages() {
        String cachedJson = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().getCache(activity, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.KEY_BACKGROUNDS);
        if (cachedJson != null && !cachedJson.trim().isEmpty()) {
            // Instant 0-second loading from cache!
            Log.d("BGController", "Loading backgrounds instantly from cache...");
            parseAndDisplayBgJson(cachedJson, true);

            // Silent background refresh
            getBgThumb(preferenceClass.getDataType("field_0"), true);
        } else {
            Log.d("BGController", "No backgrounds cache found, fetching from API...");
            startMaterialDialog();
            getBgThumb(preferenceClass.getDataType("field_0"), false);
        }
    }

    public void refreshData(boolean forceRefresh) {
        if (forceRefresh) {
            startMaterialDialog();
            getBgThumb(preferenceClass.getDataType("field_0"), false);
        } else {
            getBgThumb(preferenceClass.getDataType("field_0"), true);
        }
    }

    private void getBgThumb(String key) {
        getBgThumb(key, false);
    }

    private void getBgThumb(String key, final boolean isSilent) {
        String requestUrl = preferenceClass.getDataType("field_1") + preferenceClass.getDataType("field_34") + preferenceClass.getDataType("field_38");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                Log.d("BGController", "getBgThumb response (silent=" + isSilent + "): length=" + (response != null ? response.length() : 0));
                if (response == null || response.trim().isEmpty()) {
                    if (!isSilent) dismissMaterialDialog();
                    return;
                }

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has(preferenceClass.getDataType("field_39"))) {
                    // Check if fresh data is different from cache
                    String oldCache = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().getCache(activity, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.KEY_BACKGROUNDS);
                    boolean isDifferent = (oldCache == null || !oldCache.equals(response));

                    // Cache fresh copy on disk
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().saveCache(activity, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.KEY_BACKGROUNDS, response);

                    if (!isSilent) {
                        parseAndDisplayBgJson(response, false);
                    } else if (isDifferent) {
                        Log.d("BGController", "Backgrounds data updated from background sync!");
                        parseAndDisplayBgJson(response, true);
                    }
                } else {
                    if (!isSilent) {
                        dismissMaterialDialog();
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog2(activity, activity.getResources().getString(R.string.something_went_wrong));
                    }
                }
            } catch (Exception e) {
                if (!isSilent) {
                    dismissMaterialDialog();
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, activity.getResources().getString(R.string.something_went_wrong));
                }
                e.printStackTrace();
            }
        }, error -> {
            if (!isSilent) {
                dismissMaterialDialog();
                Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, activity.getResources().getString(R.string.something_went_wrong));
            }
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put(preferenceClass.getDataType("field_47"), key);
                postMap.put(preferenceClass.getDataType("field_48"), "1");
                return postMap;
            }
        };
        Volley.newRequestQueue(activity).add(stringRequest);
    }

    private void parseAndDisplayBgJson(String jsonResponse, boolean isSilentOrCache) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray(preferenceClass.getDataType("field_39"));
            if (jsonArray.length() == 0) {
                if (!isSilentOrCache) {
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog2(activity, activity.getResources().getString(R.string.something_went_wrong));
                }
            } else {
                new Leaflet_GetBgData(preferenceClass, jsonArray, new Leaflet_GetBgData.OnGetCatDataListener() {
                    @Override
                    public void onGetDataComplete(ArrayList<Leaflet_BgModel> posterDataLists) {
                        setPagerAdapter(posterDataLists);
                    }

                    @Override
                    public void onError() {
                        if (!isSilentOrCache) {
                            dismissMaterialDialog();
                            Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, activity.getResources().getString(R.string.something_went_wrong));
                        }
                    }
                }).execute();
            }
        } catch (Exception e) {
            if (!isSilentOrCache) {
                dismissMaterialDialog();
                Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, activity.getResources().getString(R.string.something_went_wrong));
            }
            e.printStackTrace();
        }
    }

    private void setPagerAdapter(ArrayList<Leaflet_BgModel> posterDataLists) {
        if (activity == null || activity.isFinishing()) return;

        Leaflet_PagerSlidingTabStrip tabs = activity.findViewById(R.id.pagerSlidingTabStrip);
        ViewPager viewPager = activity.findViewById(R.id.viewPager);
        if (viewPager == null || tabs == null) {
            dismissMaterialDialog();
            return;
        }

        int currentItem = viewPager.getCurrentItem();

        ArrayList<Leaflet_BgModel> tabList = new ArrayList<>();
        int maxItems = Math.min(6, posterDataLists.size());
        for (int i = 0; i < maxItems; i++) {
            tabList.add(posterDataLists.get(i));
        }

        viewPager.setAdapter(new Leaflet_BackgroundPagerAdapter(supportFragmentManager, tabList));
        if (currentItem > 0 && currentItem < tabList.size()) {
            viewPager.setCurrentItem(currentItem, false);
        } else {
            viewPager.setCurrentItem(0, false);
        }
        tabs.setViewPager(viewPager);

        if (activity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_BackgroundSelectionActivity) {
            ((com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_BackgroundSelectionActivity) activity).onCategoriesLoaded(posterDataLists, tabList);
        }

        dismissMaterialDialog();
    }

    public void openPhotoGallery() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
//                    || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    activity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSION_GRANTED);
//                } else {
//                    MailER_MaterialDialogUtils.getInstance().PermissionDialog(activity);
//                }
//                return;
//            }
//        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager.setExternalIntentActive(true);
        activity.startActivityForResult(Intent.createChooser(intent, activity.getResources().getString(R.string.select_picture)), GALLERY_INTENT);
    }

    public void openCamera() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (activity.checkSelfPermission("android.permission.CAMERA") != PERMISSION_GRANTED
//                    || activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PERMISSION_GRANTED
//                    || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    activity.requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSION_GRANTED);
//                } else {
//                    MailER_MaterialDialogUtils.getInstance().PermissionDialog(activity);
//                }
//                return;
//            }
//        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        camera_file = new File(Environment.getExternalStorageDirectory(), ".temp.jpg");
        camera_file = new File(activity.getCacheDir(), ".temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",
                        camera_file));

        com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager.setExternalIntentActive(true);
        activity.startActivityForResult(intent, CAMERA_INTENT);


    }

    public void openColorDialog() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
//                    || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    activity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSION_GRANTED);
//                } else {
//                    MailER_MaterialDialogUtils.getInstance().PermissionDialog(activity);
//                }
//                return;
//            }
//        }
        itemColorAdapter();
    }

    public void itemColorAdapter() {
        Dialog dialogColor = new Dialog(activity, R.style.DialogTheme);
        dialogColor.setContentView(R.layout.leaflet_color_pallete_layout);

        Objects.requireNonNull(dialogColor.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation_2;

        dialogColor.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ImageView tvBack = dialogColor.findViewById(R.id.tv_back);

        tvBack.setOnClickListener(v -> dialogColor.dismiss());

        ImageView tvCustom = dialogColor.findViewById(R.id.tv_custom);
        tvCustom.setOnClickListener(v -> {
            dialogColor.dismiss();
            openColorPicker();
        });

        RecyclerView rvSubList = dialogColor.findViewById(R.id.rv_sub_list);
        rvSubList.setLayoutManager(new GridLayoutManager(activity, 3));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float screenDensity = activity.getResources().getDisplayMetrics().density;
        int screenWidth = (displayMetrics).widthPixels;
        int cellWidth = (int) (screenWidth - (42 * screenDensity)) / 3;
        int cellHeight = (700 * cellWidth) / 507;

        Leaflet_ColorPelleteAdapter colorListAdapter = new Leaflet_ColorPelleteAdapter(activity, cellWidth, cellHeight, (colorCode) -> {
            dialogColor.dismiss();
            Bitmap bitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
            try { bitmap.eraseColor(Color.parseColor(colorCode)); } catch (Exception e) { bitmap.eraseColor(Color.BLACK); }
            new Leaflet_SaveBitmapTask(bitmap, new File(/*Environment.getExternalStorageDirectory()*/activity.getCacheDir(), ".temp.jpg").getPath(), new Leaflet_SaveBitmapTask.OnColorBitmapListener() {
                @Override
                public void onDownloadComplete() {
                    if (mode.equals("user")) {
                        Intent intent = new Intent();
                        intent.putExtra("local", false);
                        intent.putExtra("bg_image", new File(activity.getCacheDir(), ".temp.jpg").getPath());
//                        intent.putExtra("bg_image", new File(Environment.getExternalStorageDirectory(), ".temp.jpg").getPath());
                        activity.setResult(RESULT_OK, intent);
                        activity.finish();
                    } else {
                        startCrop(Uri.fromFile(new File(activity.getCacheDir(), ".temp.jpg")));
//                        startCrop(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), ".temp.jpg")));
                    }
                }

                @Override
                public void onError() {
                    //Toast.makeText(activity, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                }
            }).execute();
        });

        rvSubList.setAdapter(colorListAdapter);

        dialogColor.show();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA

            }, 1);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA

            }, 1);
        }

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES);
            int result2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

            if (result1 == 0 && result2 == 0) {
                return true;
            }
        } else {
            int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

            if (result == 0 && result1 == 0 && result2 == 0) {
                return true;
            }
        }

        return false;
    }

    private void openColorPicker() {
        new AmbilWarnaDialog(activity, Color.CYAN, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Bitmap bitmap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(color);
                new Leaflet_SaveBitmapTask(bitmap, new File(/*Environment.getExternalStorageDirectory()*/activity.getCacheDir(), ".temp.jpg").getPath(), new Leaflet_SaveBitmapTask.OnColorBitmapListener() {
                    @Override
                    public void onDownloadComplete() {
                        if (mode.equals("user")) {
                            Intent intent = new Intent();
                            intent.putExtra("local", false);
//                            intent.putExtra("bg_image", new File(Environment.getExternalStorageDirectory(), ".temp.jpg").getPath());
                            intent.putExtra("bg_image", new File(activity.getCacheDir(), ".temp.jpg").getPath());
                            activity.setResult(RESULT_OK, intent);
                            activity.finish();
                        } else {
//                            startCrop(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), ".temp.jpg")));
                            startCrop(Uri.fromFile(new File(activity.getCacheDir(), ".temp.jpg")));
                        }
                    }

                    @Override
                    public void onError() {
                        // Toast.makeText(activity, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }

            public void onCancel(AmbilWarnaDialog dialog) {
            }
        }).show();
    }

    public void startCrop(@NonNull Uri uri) {
        String destinationFileName = "SampleCropImage.png";
//        File file=new File(activity.getCacheDir(), destinationFileName);
//        if (file.exists()){
//            file.delete();
//        }
//        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), destinationFileName)));
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(activity.getCacheDir(), destinationFileName)));
        uCrop = advancedConfig(uCrop);
        uCrop.start(activity);
    }


    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(activity, R.color.purple_700));
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.purple_700));
        options.setRootViewBackgroundColor(ContextCompat.getColor(activity, R.color.purple_200));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(activity, R.color.selected));
        options.setToolbarWidgetColor(ContextCompat.getColor(activity, R.color.white));
        options.setAspectRatioOptions(1,
                new AspectRatio("1:1", 1, 1),
                new AspectRatio("3:2", 3, 2),
                new AspectRatio("2:3", 2, 3),
                new AspectRatio("4:3", 4, 3),
                new AspectRatio("3:4", 3, 4),
                new AspectRatio("16:9", 16, 9),
                new AspectRatio("5:4", 5, 4),
                new AspectRatio("4:5", 4, 5));
        return uCrop.withOptions(options);
    }

    public void startMaterialDialog() {
        materialDialog = Leaflet_MaterialDialogUtils.getInstance().createAnimationDialog(activity);
        Objects.requireNonNull(materialDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    public void dismissMaterialDialog() {
        try {
            if (activity != null && !activity.isFinishing()) {
                if (materialDialog != null && materialDialog.isShowing()) {
                    materialDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

