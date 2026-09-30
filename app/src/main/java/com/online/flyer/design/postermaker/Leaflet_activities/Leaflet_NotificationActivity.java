package com.online.flyer.design.postermaker.Leaflet_activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_NotificationAdapter;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NotificationDB;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NotificationModel;
import com.online.flyer.design.postermaker.R;

import java.util.List;

public class Leaflet_NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvNoNotifications, tvClearAll;
    private Leaflet_NotificationAdapter adapter;
    private List<Leaflet_NotificationModel> list;
    private Leaflet_NotificationDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_notification);

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        rvNotifications = findViewById(R.id.rv_notifications);
        tvNoNotifications = findViewById(R.id.tv_no_notifications);
        tvClearAll = findViewById(R.id.tv_clear_all);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        db = new Leaflet_NotificationDB(this);

        tvClearAll.setOnClickListener(v -> {
            db.deleteAllNotifications();
            loadNotifications();
        });

        loadNotifications();
    }

    private void loadNotifications() {
        list = db.getAllNotifications();
        if (list.isEmpty()) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvNoNotifications.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            adapter = new Leaflet_NotificationAdapter(this, list, (model, position) -> {
                if (!model.isRead()) {
                    db.markAsRead(model.getId());
                    model.setRead(true);
                    adapter.notifyItemChanged(position);
                }
                if (model.getLaunchUrl() != null && !model.getLaunchUrl().isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getLaunchUrl()));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            rvNotifications.setAdapter(adapter);
        }
    }
}

