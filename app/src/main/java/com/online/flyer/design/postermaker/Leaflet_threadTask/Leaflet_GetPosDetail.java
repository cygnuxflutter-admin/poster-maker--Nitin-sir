package com.online.flyer.design.postermaker.Leaflet_threadTask;

import android.os.AsyncTask;

import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_StickerModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_TemplateModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_TextModel;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Leaflet_GetPosDetail extends AsyncTask<Void, Void, String> {

    private final Leaflet_PreferenceClass preferenceClass;
    private final JSONArray jsonArray;
    private final ArrayList<Leaflet_TemplateModel> templateModelArrayList = new ArrayList<>();
    private final OnGetDataListener onGetDataListener;

    public interface OnGetDataListener {
        void onGetDataComplete(ArrayList<Leaflet_TemplateModel> templateModels);
        void onError();
    }


    public Leaflet_GetPosDetail(Leaflet_PreferenceClass preferenceClass, JSONArray jsonArray, OnGetDataListener onGetDataListener) {
        this.preferenceClass = preferenceClass;
        this.jsonArray = jsonArray;
        this.onGetDataListener = onGetDataListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arrayJSONObject = jsonArray.getJSONObject(i);
                String field_10 = arrayJSONObject.getString(preferenceClass.getDataType("field_10"));
                String field_11 = arrayJSONObject.getString(preferenceClass.getDataType("field_11"));
                String field_9 = arrayJSONObject.getString(preferenceClass.getDataType("field_9"));
                String field_8 = arrayJSONObject.getString(preferenceClass.getDataType("field_8"));
                String field_46 = arrayJSONObject.getString(preferenceClass.getDataType("field_46"));
                ArrayList<Leaflet_TextModel> text_modelArrayList = getTextArray(arrayJSONObject);
                ArrayList<Leaflet_StickerModel> sticker_modelArrayList = getStickerArray(arrayJSONObject);
                templateModelArrayList.add(new Leaflet_TemplateModel(field_46, field_8, field_9, field_10, field_11, text_modelArrayList, sticker_modelArrayList));
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private ArrayList<Leaflet_TextModel> getTextArray(JSONObject arrayJSONObject) {
        ArrayList<Leaflet_TextModel> text_modelArrayList = new ArrayList<>();
        try {
            JSONArray field_12 = arrayJSONObject.getJSONArray(preferenceClass.getDataType("field_12"));
            for (int j = 0; j < field_12.length(); j++) {
                JSONObject jsonObject = field_12.getJSONObject(j);
                String field_21 = jsonObject.getString(preferenceClass.getDataType("field_21"));
                String field_16 = jsonObject.getString(preferenceClass.getDataType("field_16"));
                String field_18 = jsonObject.getString(preferenceClass.getDataType("field_18"));
                String field_23 = jsonObject.getString(preferenceClass.getDataType("field_23"));
                String field_17 = jsonObject.getString(preferenceClass.getDataType("field_17"));
                String field_19 = jsonObject.getString(preferenceClass.getDataType("field_19"));
                String field_15 = jsonObject.getString(preferenceClass.getDataType("field_15"));
                String field_14 = jsonObject.getString(preferenceClass.getDataType("field_14"));
                String field_22 = jsonObject.getString(preferenceClass.getDataType("field_22"));
                String field_20 = jsonObject.getString(preferenceClass.getDataType("field_20"));
                text_modelArrayList.add(new Leaflet_TextModel(field_14, field_15, field_16, field_17, field_18, field_19, field_20, field_21, field_22, field_23));
            }
            return text_modelArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    private ArrayList<Leaflet_StickerModel> getStickerArray(JSONObject arrayJSONObject) {
        ArrayList<Leaflet_StickerModel> sticker_modelArrayList = new ArrayList<>();
        try {
            JSONArray field_13 = arrayJSONObject.getJSONArray(preferenceClass.getDataType("field_13"));
            for (int j = 0; j < field_13.length(); j++) {
                JSONObject jsonObject = field_13.getJSONObject(j);
                String field_25 = jsonObject.getString(preferenceClass.getDataType("field_25"));
                String field_32 = jsonObject.getString(preferenceClass.getDataType("field_32"));
                String field_29 = jsonObject.getString(preferenceClass.getDataType("field_29"));
                String field_28 = jsonObject.getString(preferenceClass.getDataType("field_28"));
                String field_31 = jsonObject.getString(preferenceClass.getDataType("field_31"));
                String field_27 = jsonObject.getString(preferenceClass.getDataType("field_27"));
                String field_26 = jsonObject.getString(preferenceClass.getDataType("field_26"));
                String field_33 = jsonObject.getString(preferenceClass.getDataType("field_33"));
                String field_30 = jsonObject.getString(preferenceClass.getDataType("field_30"));
                String field_24 = jsonObject.getString(preferenceClass.getDataType("field_24"));
                sticker_modelArrayList.add(new Leaflet_StickerModel(field_24, field_25, field_26, field_27, field_28, field_29, field_30, field_31, field_32, field_33));
            }
            return sticker_modelArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("success")) {
            onGetDataListener.onGetDataComplete(templateModelArrayList);
        } else {
            onGetDataListener.onError();
        }
    }
}
