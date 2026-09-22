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

    private final Activity activity;
    private final FragmentManager supportFragmentManager;
    private final Leaflet_PreferenceClass preferenceClass;
    private MaterialDialog materialDialog;

    public Leaflet_TemplateSelectionController(Activity activity, FragmentManager supportFragmentManager, Leaflet_PreferenceClass preferenceClass) {
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.preferenceClass = preferenceClass;
        loadTemplates();
    }

    private void loadTemplates() {
        startMaterialDialog();
        getTemplateThumb(preferenceClass.getDataType("field_0"));
    }

    public void getTemplateThumb(final String key) {
        String requestUrl = preferenceClass.getDataType("field_1") + preferenceClass.getDataType("field_34") + preferenceClass.getDataType("field_35");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                Log.d("xgdgdg", "getTemplateThumb: " + response);
                JSONObject jsonObject = new JSONObject(response);
                int error = jsonObject.getInt(preferenceClass.getDataType("field_3"));
                if (error == 0 || error == 404) {
                    dismissMaterialDialog();
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog2(activity, activity.getResources().getString(R.string.something_went_wrong));
                    return;
                }
                if (error == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray(preferenceClass.getDataType("field_4"));
                    if (jsonArray.length() == 0) {
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog2(activity, activity.getResources().getString(R.string.something_went_wrong));
                    } else {
                        new Leaflet_GetTemplateData(preferenceClass, jsonArray, new Leaflet_GetTemplateData.OnGetCatDataListener() {
                            @Override
                            public void onGetDataComplete(ArrayList<Leaflet_PosterModel> posterDataLists) {
                                setPagerAdapter(posterDataLists);
                            }

                            @Override
                            public void onError() {
                                dismissMaterialDialog();
                                Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, activity.getResources().getString(R.string.something_went_wrong));
                            }
                        }).execute();
                    }
                }
            } catch (Exception e) {
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

                    Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, text11);
                    e.printStackTrace();
                } catch (Exception e1) {
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, e.getMessage());
                    e.printStackTrace();
                }
            }
        }, error -> {
            dismissMaterialDialog();
            Leaflet_MaterialDialogUtils.getInstance().errorDialog(activity, error.getMessage());
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

    private void setPagerAdapter(ArrayList<Leaflet_PosterModel> posterDataLists) {
        Leaflet_PagerSlidingTabStrip tabs = activity.findViewById(R.id.pagerSlidingTabStrip);
        ViewPager viewPager = activity.findViewById(R.id.viewPager);

        ArrayList<Leaflet_PosterModel> tabList = new ArrayList<>();
        int maxItems = Math.min(4, posterDataLists.size());
        for (int i = 0; i < maxItems; i++) {
            tabList.add(posterDataLists.get(i));
        }

        viewPager.setAdapter(new Leaflet_TemplatePagerAdapter(supportFragmentManager, tabList));
        viewPager.setCurrentItem(0);
        tabs.setViewPager(viewPager);

        if (activity instanceof Leaflet_TemplateSelectionActivity) {
            ((Leaflet_TemplateSelectionActivity) activity).onCategoriesLoaded(posterDataLists, tabList);
        }

        dismissMaterialDialog();
    }

    public void startMaterialDialog() {
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
