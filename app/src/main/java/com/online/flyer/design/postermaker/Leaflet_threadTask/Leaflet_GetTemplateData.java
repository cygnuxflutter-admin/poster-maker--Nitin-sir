package com.online.flyer.design.postermaker.Leaflet_threadTask;

import android.os.AsyncTask;

import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Leaflet_GetTemplateData extends AsyncTask<Void, Void, String> {

    private final Leaflet_PreferenceClass preferenceClass;
    private final JSONArray jsonArray;
    private final ArrayList<Leaflet_PosterModel> posterCoArrayList = new ArrayList<>();
    private final OnGetCatDataListener onGetDataListener;

    public interface OnGetCatDataListener {
        void onGetDataComplete(ArrayList<Leaflet_PosterModel> posterModels);

        void onError();
    }

    public Leaflet_GetTemplateData(Leaflet_PreferenceClass preferenceClass, JSONArray jsonArray, OnGetCatDataListener onGetDataListener) {
        this.preferenceClass = preferenceClass;
        this.jsonArray = jsonArray;
        this.onGetDataListener = onGetDataListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arrayJSONObject = jsonArray.getJSONObject(i);
                String field_46 = arrayJSONObject.getString(preferenceClass.getDataType("field_46"));
                String field_6 = arrayJSONObject.getString(preferenceClass.getDataType("field_6"));
                String field_5 = arrayJSONObject.getString(preferenceClass.getDataType("field_5"));
                ArrayList<Leaflet_PosterImage> posterThumbFullArrayList = getPostThumb(arrayJSONObject, field_46);
                if (posterThumbFullArrayList != null && posterThumbFullArrayList.size() > 0)
                    posterCoArrayList.add(new Leaflet_PosterModel(field_46, field_6, field_5, posterThumbFullArrayList));
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private ArrayList<Leaflet_PosterImage> getPostThumb(JSONObject arrayJSONObject, String cat_id) {
        ArrayList<Leaflet_PosterImage> posterThumbFullArrayList = new ArrayList<>();
        try {
            JSONArray field_7 = arrayJSONObject.getJSONArray(preferenceClass.getDataType("field_7"));
            for (int j = 0; j < field_7.length(); j++) {
                JSONObject jsonObject = field_7.getJSONObject(j);
                String field_10 = jsonObject.getString(preferenceClass.getDataType("field_10"));
                String field_11 = jsonObject.getString(preferenceClass.getDataType("field_11"));
                String field_8 = jsonObject.getString(preferenceClass.getDataType("field_8"));

                if ((j) % preferenceClass.getInt("rv_count", 4) == 0) {
                    posterThumbFullArrayList.add(null);
                }

                posterThumbFullArrayList.add(new Leaflet_PosterImage(field_10, field_11, field_8, cat_id));
            }
            return posterThumbFullArrayList;
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
