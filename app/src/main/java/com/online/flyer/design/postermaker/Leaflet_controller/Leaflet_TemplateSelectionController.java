package com.online.flyer.design.postermaker.Leaflet_controller;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_MainSecurity;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_TemplateSelectionActivity;
import com.online.flyer.design.postermaker.Leaflet_fragment.Leaflet_TemplatePagerAdapter;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterModel;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_GetTemplateData;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Leaflet_TemplateSelectionController {

    private static final String TAG = "TemplateController";
    private final Activity activity;
    private final FragmentManager supportFragmentManager;
    private final Leaflet_PreferenceClass preferenceClass;
    private MaterialDialog materialDialog;
    private boolean isDataLoaded = false;

    public Leaflet_TemplateSelectionController(Activity activity, FragmentManager supportFragmentManager, Leaflet_PreferenceClass preferenceClass) {
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.preferenceClass = preferenceClass;
        loadTemplates();
    }

    private void loadTemplates() {
        String cachedJson = Leaflet_DataCacheManager.getInstance().getCache(activity, Leaflet_DataCacheManager.KEY_TEMPLATES);
        if (cachedJson != null && !cachedJson.trim().isEmpty()) {
            // Instant 0-second loading from cache!
            Log.d(TAG, "Loading templates instantly from cache...");
            isDataLoaded = true;
            parseAndDisplayJson(cachedJson, true);

            // Silent background refresh to check if admin added new templates
            getTemplateThumb(preferenceClass.getDataType("field_0"), true);
        } else {
            // No cache yet (first install), show loading dialog
            Log.d(TAG, "No cache found, fetching templates from API...");
            startMaterialDialog();
            getTemplateThumb(preferenceClass.getDataType("field_0"), false);
        }
    }

    public void refreshData(boolean forceRefresh) {
        if (forceRefresh) {
            startMaterialDialog();
            getTemplateThumb(preferenceClass.getDataType("field_0"), false);
        } else {
            getTemplateThumb(preferenceClass.getDataType("field_0"), true);
        }
    }

    public void getTemplateThumb(final String key) {
        getTemplateThumb(key, false);
    }

    public void getTemplateThumb(final String key, final boolean isSilent) {
        String requestUrl = preferenceClass.getDataType("field_1") + preferenceClass.getDataType("field_34") + preferenceClass.getDataType("field_35");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                Log.d(TAG, "getTemplateThumb response (silent=" + isSilent + "): length=" + (response != null ? response.length() : 0));
                if (response == null || response.trim().isEmpty()) {
                    if (!isSilent) dismissMaterialDialog();
                    return;
                }

                JSONObject jsonObject = new JSONObject(response);
                int error = jsonObject.optInt(preferenceClass.getDataType("field_3"), -1);
                if (error == 0 || error == 404) {
                    if (!isSilent) {
                        dismissMaterialDialog();
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog2(activity, activity.getResources().getString(R.string.something_went_wrong));
                    }
                    return;
                }

                if (error == 1) {
                    // Check if fresh data is different from cache before updating
                    String oldCache = Leaflet_DataCacheManager.getInstance().getCache(activity, Leaflet_DataCacheManager.KEY_TEMPLATES);
                    boolean isDifferent = (oldCache == null || !oldCache.equals(response));

                    // Cache fresh copy on disk
                    Leaflet_DataCacheManager.getInstance().saveCache(activity, Leaflet_DataCacheManager.KEY_TEMPLATES, response);

                    if (!isSilent || !isDataLoaded) {
                        // Display fresh data
                        parseAndDisplayJson(response, false);
                    } else if (isDifferent) {
                        // Background data changed, update seamlessly
                        Log.d(TAG, "Templates data updated from background sync!");
                        parseAndDisplayJson(response, true);
                    }
                }
            } catch (Exception e) {
                if (!isSilent) {
                    dismissMaterialDialog();
                    try {
                        String text11 = null;
                        if (preferenceClass.getDecryptionType() == 0) {
                            byte[] octets = Base64.decode(response, Base64.URL_SAFE);
                            String text_base = new String(octets, StandardCharsets.UTF_8);
                            byte[] octets1 = Base64.decode(text_base, Base64.URL_SAFE);
                            text11 = new String(octets1, StandardCharsets.UTF_8);
                        } else if (preferenceClass.getDecryptionType() == 1) {
                            Leaflet_MainSecurity decrypted = Leaflet_MainSecurity.decrypt(preferenceClass.getDataType("main_key"), response);
                            text11 = decrypted.getData();
                        }
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, text11 != null ? text11 : e.getMessage());
                    } catch (Exception e1) {
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, e.getMessage());
                    }
                }
                e.printStackTrace();
            }
        }, error -> {
            if (!isSilent) {
                dismissMaterialDialog();
                Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, error.getMessage());
            }
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put(preferenceClass.getDataType("field_47"), key);
                postMap.put(preferenceClass.getDataType("field_48"), "1");
                postMap.put(preferenceClass.getDataType("field_46"), "0");
                postMap.put(preferenceClass.getDataType("field_8"), "0");
                return postMap;
            }
        };

        int MY_SOCKET_TIMEOUT_MS = 300000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(activity).add(stringRequest);
    }

    private void parseAndDisplayJson(String jsonResponse, boolean isSilentOrCache) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray(preferenceClass.getDataType("field_4"));
            if (jsonArray.length() == 0) {
                if (!isSilentOrCache) {
                    dismissMaterialDialog();
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog2(activity, activity.getResources().getString(R.string.something_went_wrong));
                }
            } else {
                new Leaflet_GetTemplateData(preferenceClass, jsonArray, new Leaflet_GetTemplateData.OnGetCatDataListener() {
                    @Override
                    public void onGetDataComplete(ArrayList<Leaflet_PosterModel> posterDataLists) {
                        isDataLoaded = true;
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
                Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private void setPagerAdapter(ArrayList<Leaflet_PosterModel> posterDataLists) {
        if (activity == null || activity.isFinishing()) return;

        Leaflet_PagerSlidingTabStrip tabs = activity.findViewById(R.id.pagerSlidingTabStrip);
        ViewPager viewPager = activity.findViewById(R.id.viewPager);
        if (viewPager == null || tabs == null) {
            dismissMaterialDialog();
            return;
        }

        int currentItem = viewPager.getCurrentItem();

        ArrayList<Leaflet_PosterModel> tabList = new ArrayList<>();
        int maxItems = Math.min(6, posterDataLists.size());
        for (int i = 0; i < maxItems; i++) {
            tabList.add(posterDataLists.get(i));
        }

        viewPager.setAdapter(new Leaflet_TemplatePagerAdapter(supportFragmentManager, tabList));
        if (currentItem > 0 && currentItem < tabList.size()) {
            viewPager.setCurrentItem(currentItem, false);
        } else {
            viewPager.setCurrentItem(0, false);
        }
        tabs.setViewPager(viewPager);

        if (activity instanceof Leaflet_TemplateSelectionActivity) {
            ((Leaflet_TemplateSelectionActivity) activity).onCategoriesLoaded(posterDataLists, tabList);
        }

        dismissMaterialDialog();
    }

    public void startMaterialDialog() {
        if (materialDialog != null && materialDialog.isShowing()) return;
        materialDialog = Leaflet_MaterialDialogUtils.getInstance().createAnimationDialog(activity);
        Objects.requireNonNull(materialDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    public void dismissMaterialDialog() {
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
    }
}
