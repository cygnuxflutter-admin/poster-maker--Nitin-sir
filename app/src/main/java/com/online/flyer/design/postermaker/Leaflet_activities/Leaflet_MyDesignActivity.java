package com.online.flyer.design.postermaker.Leaflet_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_MyDesignAdapter;
import com.online.flyer.design.postermaker.Leaflet_components.Leaflet_TemplateInfo;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DatabaseHandler;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.util.ArrayList;

import static com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd;

public class Leaflet_MyDesignActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtEmptyMsg;
    private int template_id, position;
    private Leaflet_MyDesignAdapter myDesignAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.leaflet_activity_my_design);

        findByID();

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        setTemplateAdapter();

    }

    private void findByID() {
        recyclerView = findViewById(R.id.template_rv);
        txtEmptyMsg = findViewById(R.id.txtEmptyMsg);

        RelativeLayout rl_ad = findViewById(R.id.rl_ad);
        if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
            if (new Leaflet_PreferenceClass(this).getAdsId("BannerAdunitID") != null) {
                loadAdmobBannerAd(this, rl_ad);
            }
        }
    }

    private void setTemplateAdapter() {
        Leaflet_DatabaseHandler dh = Leaflet_DatabaseHandler.getDbHandler(this);
        ArrayList<Leaflet_TemplateInfo> templateList = dh.getTemplateListDes("USER");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float screenDensity = getResources().getDisplayMetrics().density;
        int screenWidth = (displayMetrics).widthPixels;
        int cellWidth = (int) (screenWidth - (42 * screenDensity)) / 2;
        int cellHeight = (700 * cellWidth) / 507;

        myDesignAdapter = new Leaflet_MyDesignAdapter(this, templateList, cellWidth, cellHeight, new Leaflet_MyDesignAdapter.MyDesignClickListener() {
            @Override
            public void onPostClick(int template_id, int position) {
                Leaflet_MyDesignActivity.this.template_id = template_id;
                Leaflet_MyDesignActivity.this.position = position;
                if (Leaflet_NetworkUtils.isNetworkAvailable(Leaflet_MyDesignActivity.this)) {
                    startIntent();
                } else {
                    Toast.makeText(Leaflet_MyDesignActivity.this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEmptyAdapter() {
                txtEmptyMsg.setVisibility(View.VISIBLE);
            }
        });
        recyclerView.setAdapter(myDesignAdapter);

        if (templateList.size() > 0) {
            txtEmptyMsg.setVisibility(View.GONE);
        } else {
            txtEmptyMsg.setVisibility(View.VISIBLE);
        }
    }


    private void startIntent() {
        Intent intent = new Intent(this, Leaflet_PosterEditActivity.class);
        intent.putExtra("loadUserFrame", false);
        intent.putExtra("Temp_Type", "USER");
        intent.putExtra("template_id", template_id);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myDesignAdapter != null) {
            setTemplateAdapter();
        }
    }
}
