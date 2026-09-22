package com.online.flyer.design.postermaker.Leaflet_activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_PosterGroupChildAdapter;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_BackgroundChildAdapter;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FavoritesManager;
import com.online.flyer.design.postermaker.R;

import java.util.ArrayList;

public class Leaflet_FavoritesActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String TYPE_POSTERS = "POSTERS";
    public static final String TYPE_BACKGROUNDS = "BACKGROUNDS";

    private RecyclerView recyclerView;
    private TextView txtHeader;
    private TextView txtNoData;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_favorites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        type = getIntent().getStringExtra(EXTRA_TYPE);

        txtHeader = findViewById(R.id.txt_header);
        txtNoData = findViewById(R.id.txt_no_data);
        recyclerView = findViewById(R.id.recycler_view);

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadData();
    }

    private void loadData() {
        int cellWidth = (int) (getResources().getDisplayMetrics().widthPixels - (42 * getResources().getDisplayMetrics().density)) / 2;
        int cellHeight = (700 * cellWidth) / 507;

        if (TYPE_POSTERS.equals(type)) {
            txtHeader.setText("Liked Posters");
            ArrayList<Leaflet_PosterImage> likedPosters = Leaflet_FavoritesManager.getLikedPosters(this);
            if (likedPosters.isEmpty()) {
                txtNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                txtNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                
                Leaflet_PosterGroupChildAdapter adapter = new Leaflet_PosterGroupChildAdapter(this, likedPosters, 0, cellWidth, cellHeight, (cat_id, post_id, premium) -> {
                    android.content.Intent intent = new android.content.Intent(this, Leaflet_TemplateSelectionActivity.class);
                    intent.putExtra("auto_load", true);
                    intent.putExtra("cat_id", cat_id);
                    intent.putExtra("post_id", post_id);
                    intent.putExtra("auto_open_premium", premium);
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            }
        } else {
            txtHeader.setText("Liked Backgrounds");
            ArrayList<Leaflet_BgImage> likedBackgrounds = Leaflet_FavoritesManager.getLikedBackgrounds(this);
            if (likedBackgrounds.isEmpty()) {
                txtNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                txtNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                Leaflet_BackgroundChildAdapter adapter = new Leaflet_BackgroundChildAdapter(this, likedBackgrounds, cellWidth, cellWidth, (path, premium) -> {
                    android.content.Intent intent = new android.content.Intent(this, Leaflet_BackgroundSelectionActivity.class);
                    intent.putExtra("auto_load", true);
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass preferenceClass = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(this);
                    String domain = preferenceClass.getDataType("field_1");
                    String fullPath = path.startsWith("http") ? path : domain + "/" + path;
                    intent.putExtra("bg_image", fullPath);
                    intent.putExtra("premium", premium);
                    intent.putExtra("mode", "bg");
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
