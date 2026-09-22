package com.online.flyer.design.postermaker.Leaflet_activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.print.PrintHelper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_Constant;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.io.File;
import java.util.Objects;

public class Leaflet_ShareActivity extends AppCompatActivity implements View.OnClickListener {

        private String imagePath;
//    private String imagePathWithWaterMark="";
    private RelativeLayout remove_wm_ll, rl_ad;
    private LinearLayout ic_print_ll;
    private ImageView preview_image, ic_back, iv_whatsapp, iv_facebook, ic_twitter, iv_instagram, iv_more;
    private ProgressDialog progressDialog;
    private Leaflet_PreferenceClass preferenceClass;
//    private boolean premiumPoster;
Boolean rateSubmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.leaflet_activity_share);


        findByID();
        setImage();

        ic_back.setOnClickListener(v -> onBackPressed());

        ic_print_ll.setOnClickListener(this);
        iv_whatsapp.setOnClickListener(this);
        iv_facebook.setOnClickListener(this);
        ic_twitter.setOnClickListener(this);
        iv_instagram.setOnClickListener(this);
        iv_more.setOnClickListener(this);

//        if (premiumPoster) {
        remove_wm_ll.setVisibility(View.GONE);
//        } else {
//            remove_wm_ll.setVisibility(View.VISIBLE);
//        }

        rl_ad.setVisibility(View.VISIBLE);
        if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
//            if (preferenceClass.getAdsId("BannerAdunitID") != null) {
                Leaflet_LoadAds.loadAdmobBannerAd(this, rl_ad);
//            }
        }


//        rateSubmit = preferenceClass.getRateSubmited("rateSubmitted");
//        Leaflet_MyApplication.getInstance().appReview(Leaflet_ShareActivity.this);

//        rate_buttonNext();
    }

    private void findByID() {
        remove_wm_ll = findViewById(R.id.remove_wm_ll);
        rl_ad = findViewById(R.id.rl_ad);
        preview_image = findViewById(R.id.preview_image);
        ic_back = findViewById(R.id.ic_back);

        ic_print_ll = findViewById(R.id.ic_print_ll);
        iv_whatsapp = findViewById(R.id.iv_whatsapp);
        iv_facebook = findViewById(R.id.iv_facebook);
        iv_instagram = findViewById(R.id.iv_instagram);
        ic_twitter = findViewById(R.id.ic_twitter);
        iv_more = findViewById(R.id.iv_more);

        preferenceClass = new Leaflet_PreferenceClass(this);

        imagePath = getIntent().getStringExtra("imagePath");
//        imagePathWithWaterMark = getIntent().getStringExtra("imagePathWithWaterMark");
//        premiumPoster = getIntent().getBooleanExtra("premiumPoster", false);

    }

    private void setImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        preview_image.setImageBitmap(bitmap);
//        if (Constant.bitmap != null) {
//            preview_image.setImageBitmap(Constant.bitmap);
//        }
    }

    private void startLoader() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void DismissLoader() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
//                    || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(MailER_ShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        || ActivityCompat.shouldShowRequestPermissionRationale(MailER_ShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSION_GRANTED);
//                } else {
//                    MailER_MaterialDialogUtils.getInstance().PermissionDialog(this);
//                }
//                return;
//            }
//        }

        if (id == R.id.ic_print_ll) {
            printImage();
        } else if (id == R.id.iv_whatsapp) {
            shareIntent("com.whatsapp", "WhatsApp app isn't found!", false);
        } else if (id == R.id.iv_facebook) {
            shareIntent("com.facebook.katana", "Facebook app isn't found!", false);
        } else if (id == R.id.iv_instagram) {
            shareIntent("com.instagram.android", "Instagram app isn't found!", false);
        } else if (id == R.id.ic_twitter) {
            shareIntent("com.twitter.android", "Twitter app isn't found!", false);
        } else if (id == R.id.iv_more) {
            shareIntent("", "", true);
        }
    }

    private void printImage() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = Leaflet_Constant.bitmap;
        photoPrinter.printBitmap("poster_maker.jpg", bitmap);
    }

    public void shareIntent(String package_name, String error_msg, boolean more) {

        String shareBody = "I'm using amazing Poster maker App, I just created a new poster within 2 min and it's completely free. I recommend you to try this app. \n\n" +
                "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(imagePath)));

        if (more) {
            startActivity(Intent.createChooser(shareIntent, "Share Image using"));
            return;
        }

        try {
            shareIntent.setPackage(package_name);
            startActivity(shareIntent);
        } catch (Exception e2) {
            Toast.makeText(this, error_msg, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class copyFileAsync extends AsyncTask<String, String, Boolean> {

        private MaterialDialog materialDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            materialDialog = Leaflet_MaterialDialogUtils.getInstance().createAnimationDialog(Leaflet_ShareActivity.this);
            Objects.requireNonNull(materialDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            materialDialog.setCancelable(false);
            materialDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return Leaflet_FileUtils.copyFile(new File(Leaflet_FileUtils.getPremiumPath(Leaflet_ShareActivity.this)), new File(Leaflet_Constant.imgPath));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (materialDialog != null && materialDialog.isShowing())
                materialDialog.dismiss();
            if (result) {
                remove_wm_ll.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeFile(Leaflet_Constant.imgPath);
                preview_image.setImageBitmap(bitmap);
                saveDialog();
            }
        }

        private void saveDialog() {
            @SuppressLint("ResourceType")
            Dialog dialog = new Dialog(Leaflet_ShareActivity.this, 16974126);
            dialog.requestWindowFeature(1);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.leaflet_premium_save_dialog);
            TextView btn_ok = dialog.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

//    private void rate_buttonNext() {
//        // startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//        /*  Drawable icon = getResources().getDrawable(R.drawable.dialog_icon);*/
//        MailER_RateButtonDialog ratingDialog = new MailER_RateButtonDialog(MailER_ShareActivity.this, "Rate the App", "Please rate the app and provide your feedback.", new MailER_RateButtonDialog.onRatingDialogListener() {
//            @Override
//            public void onRatingSelected(float rating) {
//            }
//
//            @Override
//            public void onDialogCancelled() {
//            }
//        });
//
//        if (!rateSubmit) {
//            ratingDialog.Show();
//        } else {
//            Toast.makeText(this, "rating submitted allready", Toast.LENGTH_SHORT).show();
//        }
//    }


}