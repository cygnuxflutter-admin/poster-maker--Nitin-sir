package com.online.flyer.design.postermaker.Leaflet_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_MyCreationAdapter;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_SignatureFileUtils;

import static com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd;

public class Leaflet_MyCreationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtEmptyMsg;
    private Leaflet_MyCreationAdapter myCreationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.leaflet_activity_my_creation);

        findByID();

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        setTemplateAdapter();
    }

    private void findByID() {
        recyclerView = findViewById(R.id.template_rv);
        txtEmptyMsg = findViewById(R.id.txtEmptyMsg);

        RelativeLayout rl_ad = findViewById(R.id.rl_ad);
        if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
//            if (new Leaflet_PreferenceClass(this).getAdsId("BannerAdunitID") != null) {
                loadAdmobBannerAd(this, rl_ad);
//            }
        }
    }

    private void setTemplateAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float screenDensity = getResources().getDisplayMetrics().density;
        int screenWidth = (displayMetrics).widthPixels;
        int cellWidth = (int) (screenWidth - (42 * screenDensity)) / 2;
        int cellHeight = (700 * cellWidth) / 507;

        myCreationAdapter = new Leaflet_MyCreationAdapter(this, new Leaflet_SignatureFileUtils(getApplicationContext()).getFilePaths(), cellWidth, cellHeight, () -> txtEmptyMsg.setVisibility(View.VISIBLE));
        recyclerView.setAdapter(myCreationAdapter);

        if (myCreationAdapter.getItemCount() > 0) {
            txtEmptyMsg.setVisibility(View.GONE);
        } else {
            txtEmptyMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myCreationAdapter != null) {
            setTemplateAdapter();
        }
    }
}