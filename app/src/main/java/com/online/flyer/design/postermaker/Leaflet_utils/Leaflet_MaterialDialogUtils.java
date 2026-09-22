package com.online.flyer.design.postermaker.Leaflet_utils;

import static com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil.loadNativeAd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_interfaces.Leaflet_DialogClickListener;

import java.util.Objects;

public class Leaflet_MaterialDialogUtils {

    private Leaflet_MaterialDialogUtils() {
    }

    public static Leaflet_MaterialDialogUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MaterialDialog createAnimationDialog(Context activity) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.leaflet_lottie_anim_dialog, false)
                .contentColor(Color.TRANSPARENT)
                .backgroundColor(Color.TRANSPARENT)
                .build();
        if (dialog.getWindow() != null) {
            dialog.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    private static class SingletonHolder {
        static final Leaflet_MaterialDialogUtils INSTANCE = new Leaflet_MaterialDialogUtils();
    }

    public void errorDialog(Activity activity, String msg) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.leaflet_error_dialog, false)
                .contentColor(Color.TRANSPARENT)
                .backgroundColor(Color.TRANSPARENT)
                .cancelable(false)
                .build();

        Objects.requireNonNull(materialDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//        @SuppressLint("ResourceType")
//        Dialog materialDialog = new Dialog(activity, 16974126);
//        materialDialog.requestWindowFeature(1);
//        materialDialog.setContentView(R.layout.error_dialog);
//        materialDialog.setCancelable(false);

        materialDialog.show();

        TextView description = (TextView) materialDialog.findViewById(R.id.description);

        description.setText(msg);

        TextView btn_ok = (TextView) materialDialog.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v -> {
            materialDialog.dismiss();
            activity.finish();
        });
    }

    public void errorDialog2(Activity activity, String msg) {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.error_dialog, false)
//                .contentColor(Color.TRANSPARENT)
//                .backgroundColor(Color.TRANSPARENT)
//                .cancelable(false)
//                .build();

        @SuppressLint("ResourceType")
        Dialog materialDialog = new Dialog(activity, 16974126);
        materialDialog.requestWindowFeature(1);
        materialDialog.setContentView(R.layout.leaflet_error_dialog);
        materialDialog.setCancelable(false);

        materialDialog.show();

        TextView description = materialDialog.findViewById(R.id.description);

        description.setText(msg);

        TextView btn_ok = materialDialog.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v -> {
            materialDialog.dismiss();
            activity.finish();
        });
    }

    public void errorDialog3(Activity activity, String msg) {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.error_dialog, false)
//                .contentColor(Color.TRANSPARENT)
//                .backgroundColor(Color.TRANSPARENT)
//                .cancelable(false)
//                .build();

        @SuppressLint("ResourceType")
        Dialog materialDialog = new Dialog(activity, 16974126);
        materialDialog.requestWindowFeature(1);
        materialDialog.setContentView(R.layout.leaflet_error_dialog);
        materialDialog.setCancelable(false);

        materialDialog.show();

        TextView description = materialDialog.findViewById(R.id.description);

        description.setText(msg);

        TextView btn_ok = materialDialog.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(v -> {
            materialDialog.dismiss();
        });
    }

    public void rewardDialog(Activity activity, String title, String message, Leaflet_DialogClickListener positiveButton, Leaflet_DialogClickListener negativeButton) {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.reward_dialog, false)
//                .contentColor(Color.TRANSPARENT)
//                .backgroundColor(Color.TRANSPARENT)
//                .cancelable(false)
//                .build();

        @SuppressLint("ResourceType")
        Dialog materialDialog = new Dialog(activity, 16974126);
        materialDialog.requestWindowFeature(1);
        materialDialog.setContentView(R.layout.leaflet_reward_dialog);
        materialDialog.setCancelable(false);


        materialDialog.show();

        TextView tv_title = materialDialog.findViewById(R.id.title);
        TextView tv_description = materialDialog.findViewById(R.id.description);

        TextView button1 = materialDialog.findViewById(R.id.button1);
        TextView button2 = materialDialog.findViewById(R.id.button2);

        tv_title.setText(title);
        tv_description.setText(message);

        button2.setOnClickListener(v -> positiveButton.onClick(materialDialog));
        button1.setOnClickListener(v -> negativeButton.onClick(materialDialog));

        RelativeLayout native_banner_ad_container = materialDialog.findViewById(R.id.native_banner_ad_containers);

        loadNativeAd(native_banner_ad_container, activity);

    }

    @SuppressLint("SetTextI18n")
    public void resetDialog(Activity activity, Leaflet_DialogClickListener positiveButton, Leaflet_DialogClickListener negativeButton) {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.reset_dialog, false)
//                .contentColor(Color.TRANSPARENT)
//                .backgroundColor(Color.TRANSPARENT)
//                .cancelable(false)
//                .build();

        @SuppressLint("ResourceType")
        Dialog materialDialog = new Dialog(activity, 16974126);
        materialDialog.requestWindowFeature(1);
        materialDialog.setContentView(R.layout.leaflet_reset_dialog);
        materialDialog.setCancelable(false);

        materialDialog.show();

        TextView tv_heater = materialDialog.findViewById(R.id.tv_heater);
        tv_heater.setText("Reset");

        TextView tv_description = materialDialog.findViewById(R.id.tv_description);
        tv_description.setText("Do you want to reset it?");

        TextView btn_yes = materialDialog.findViewById(R.id.btn_yes);
        TextView btn_no = materialDialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(v -> positiveButton.onClick(materialDialog));
        btn_no.setOnClickListener(v -> negativeButton.onClick(materialDialog));

        RelativeLayout native_banner_ad_container = materialDialog.findViewById(R.id.native_banner_ad_containers);
        loadNativeAd(native_banner_ad_container, activity);
    }

    public void exitAlertDialog(Activity activity, String title, String message, Leaflet_DialogClickListener dialogClickListener) {
//
//        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.exitalert_dialog, false)
//                .contentColor(Color.TRANSPARENT)
//                .backgroundColor(Color.TRANSPARENT)
//                .cancelable(false)
//                .build();

        @SuppressLint("ResourceType")
        Dialog materialDialog = new Dialog(activity, 16974126);
        materialDialog.requestWindowFeature(1);
        materialDialog.setContentView(R.layout.leaflet_exitalert_dialog);
        materialDialog.setCancelable(false);

        materialDialog.show();

        TextView tv_title = materialDialog.findViewById(R.id.title);
        TextView tv_description = materialDialog.findViewById(R.id.description);

        TextView button1 = materialDialog.findViewById(R.id.button1);
        TextView button2 = materialDialog.findViewById(R.id.button2);

        tv_title.setText(title);
        tv_description.setText(message);

        button1.setOnClickListener(v -> materialDialog.dismiss());
//        button2.setOnClickListener(v -> dialogClickListener.onClick(materialDialog));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finish();
                System.exit(0);
            }
        });

        RelativeLayout native_banner_ad_container = materialDialog.findViewById(R.id.native_banner_ad_containers);
        loadNativeAd(native_banner_ad_container, activity);
    }


    public void PermissionDialog(Activity activity) {
//        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
//                .customView(R.layout.permission_dialog, false)
//                .contentColor(Color.TRANSPARENT)
//                .backgroundColor(Color.TRANSPARENT)
//                .cancelable(false)
//                .build();
        try {
            @SuppressLint("ResourceType")
            Dialog materialDialog = new Dialog(activity, 16974126);
            materialDialog.requestWindowFeature(1);
            materialDialog.setContentView(R.layout.leaflet_permission_dialog);
            materialDialog.setCancelable(false);

            materialDialog.show();

            TextView btn_cancel = materialDialog.findViewById(R.id.btn_cancel);
            TextView btn_settings = materialDialog.findViewById(R.id.btn_settings);

            btn_cancel.setOnClickListener(v -> materialDialog.dismiss());

            btn_settings.setOnClickListener(v -> {
                materialDialog.dismiss();
                activity.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
//            Log.e("#brand", Build.BRAND);
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", getPackageName(), null);
//            intent.setData(uri);
//            activity.startActivity(intent);
            });
        } catch (Exception e) {

        }

    }

    @SuppressLint("SetTextI18n")
    public void DeleteDialog(Activity activity, Leaflet_DialogClickListener dialogClickListener) {

        @SuppressLint("ResourceType")
        Dialog materialDialog = new Dialog(activity, 16974126);
        materialDialog.requestWindowFeature(1);
        materialDialog.setContentView(R.layout.leaflet_reset_dialog);
        materialDialog.setCancelable(false);

        materialDialog.show();

        TextView tv_heater = materialDialog.findViewById(R.id.tv_heater);
        tv_heater.setText("Delete Poster");

        TextView tv_description = materialDialog.findViewById(R.id.tv_description);
        tv_description.setText("Are you sure you want to delete this poster?");

        TextView btn_yes = materialDialog.findViewById(R.id.btn_yes);
        TextView btn_no = materialDialog.findViewById(R.id.btn_no);

        btn_no.setOnClickListener(v -> materialDialog.dismiss());

        btn_yes.setOnClickListener(v -> dialogClickListener.onClick(materialDialog));

        RelativeLayout native_banner_ad_container = materialDialog.findViewById(R.id.native_banner_ad_containers);
        loadNativeAd(native_banner_ad_container, activity);
    }

}
