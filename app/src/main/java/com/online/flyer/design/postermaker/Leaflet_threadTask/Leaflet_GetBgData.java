package com.online.flyer.design.postermaker.Leaflet_threadTask;

import android.os.AsyncTask;

import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Leaflet_GetBgData extends AsyncTask<Void, Void, String> {

    private final Leaflet_PreferenceClass preferenceClass;
    private final JSONArray jsonArray;
    private final ArrayList<Leaflet_BgModel> posterCoArrayList = new ArrayList<>();
    private final OnGetCatDataListener onGetDataListener;

    public interface OnGetCatDataListener {
        void onGetDataComplete(ArrayList<Leaflet_BgModel> posterDataLists);
        void onError();
    }

    public Leaflet_GetBgData(Leaflet_PreferenceClass preferenceClass, JSONArray jsonArray, OnGetCatDataListener onGetDataListener) {
        this.preferenceClass = preferenceClass;
        this.jsonArray = jsonArray;
        this.onGetDataListener = onGetDataListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arrayJSONObject = jsonArray.getJSONObject(i);
                String field_40 = arrayJSONObject.getString(preferenceClass.getDataType("field_40"));
                String field_41 = arrayJSONObject.getString(preferenceClass.getDataType("field_41"));
                ArrayList<Leaflet_BgImage> posterThumbFullArrayList = getPostThumb(arrayJSONObject);
                posterCoArrayList.add(new Leaflet_BgModel(field_40, field_41, posterThumbFullArrayList));
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private ArrayList<Leaflet_BgImage> getPostThumb(JSONObject arrayJSONObject) {
        ArrayList<Leaflet_BgImage> bgImageArrayList = new ArrayList<>();
        try {
            JSONArray field_42 = arrayJSONObject.getJSONArray(preferenceClass.getDataType("field_42"));
            for (int j = 0; j < field_42.length(); j++) {
                JSONObject textJSONObject = field_42.getJSONObject(j);
                int field_43 = textJSONObject.getInt(preferenceClass.getDataType("field_43"));
                String field_44 = textJSONObject.getString(preferenceClass.getDataType("field_44"));
                String field_45 = textJSONObject.getString(preferenceClass.getDataType("field_45"));

                int rvCount = preferenceClass.getInt("rv_count", 4);
                if (rvCount > 0 && j % rvCount == 0) {
                    bgImageArrayList.add(null);
                }

                bgImageArrayList.add(new Leaflet_BgImage(field_43, field_44, field_45));
            }
            return bgImageArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("success")) {
            onGetDataListener.onGetDataComplete(posterCoArrayList);
        } else {
            onGetDataListener.onError();
        }
    }
}
