package com.online.flyer.design.postermaker.Leaflet_threadTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Leaflet_DlTemplateAsync extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private final Activity activity;
    private final ArrayList<String> stringArrayList;
    private final OnDownloadTemplateListener onDownloadTemplateListener;

    public interface OnDownloadTemplateListener {
        void onDownloadComplete();
        void onError();
    }

    public Leaflet_DlTemplateAsync(Activity activity, ArrayList<String> stringArrayList, OnDownloadTemplateListener onDownloadTemplateListener) {
        this.activity = activity;
        this.stringArrayList = stringArrayList;
        this.onDownloadTemplateListener = onDownloadTemplateListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            for (int i = 0; i < stringArrayList.size(); i++) {
                String uri = stringArrayList.get(i);
                if (uri == null || uri.trim().isEmpty()) continue;

                File dir = Leaflet_FileUtils.getRootPath(activity);
                dir.mkdirs();
                File file = new File(dir, Leaflet_FileUtils.getFileName(uri));

                if (!file.exists()) {
                    try {
                        URL url = new URL(uri);
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.setConnectTimeout(8000);
                        urlConnection.setReadTimeout(12000);
                        urlConnection.connect();

                        InputStream input = new BufferedInputStream(url.openStream());
                        OutputStream output = new FileOutputStream(file);
                        byte[] data = new byte[1024];
                        while (true) {
                            int count = input.read(data);
                            if (count == -1) {
                                break;
                            }
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (Exception e) {
                        android.util.Log.e("DlTemplateAsync", "Failed to download asset: " + uri, e);
                        // If the primary background image (index 0) fails, that is critical
                        if (i == 0) {
                            return "error";
                        }
                        // Non-critical assets (fonts/stickers) failure should not abort opening the template
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("success")) {
            onDownloadTemplateListener.onDownloadComplete();
        } else {
            onDownloadTemplateListener.onError();
        }
    }

}
