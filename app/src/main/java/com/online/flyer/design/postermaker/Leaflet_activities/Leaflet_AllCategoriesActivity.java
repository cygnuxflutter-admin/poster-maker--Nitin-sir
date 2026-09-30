package com.online.flyer.design.postermaker.Leaflet_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterModel;
import com.online.flyer.design.postermaker.R;

import java.util.ArrayList;

public class Leaflet_AllCategoriesActivity extends AppCompatActivity {

    public static ArrayList<String> allCategoriesList;
    public static String currentlySelectedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_all_categories);

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.rv_all_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (allCategoriesList != null) {
            filteredCategoriesList = new ArrayList<>(allCategoriesList);
            adapter = new CategoriesAdapter();
            recyclerView.setAdapter(adapter);
        }

        View lyTitle = findViewById(R.id.ly_title);
        View lySearch = findViewById(R.id.ly_search);
        View icSearch = findViewById(R.id.ic_search);
        View icSearchClose = findViewById(R.id.ic_search_close);
        android.widget.EditText etSearch = findViewById(R.id.et_search);

        icSearch.setOnClickListener(v -> {
            lyTitle.setVisibility(View.GONE);
            icSearch.setVisibility(View.GONE);
            lySearch.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });

        icSearchClose.setOnClickListener(v -> {
            etSearch.setText("");
            lySearch.setVisibility(View.GONE);
            lyTitle.setVisibility(View.VISIBLE);
            icSearch.setVisibility(View.VISIBLE);
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }
        });

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (allCategoriesList == null) return;
                filteredCategoriesList.clear();
                String query = s.toString().toLowerCase().trim();
                if (query.isEmpty()) {
                    filteredCategoriesList.addAll(allCategoriesList);
                } else {
                    for (String cat : allCategoriesList) {
                        if (cat.toLowerCase().contains(query)) {
                            filteredCategoriesList.add(cat);
                        }
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Load Banner Ad
        android.widget.RelativeLayout rl_ad = findViewById(R.id.rl_ad);
        if (rl_ad != null) {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(this);
            if (pref.getInt("show_banner_templates", 1) == 1 && pref.getAdsId("BannerAdunitID") != null) {
                rl_ad.setVisibility(android.view.View.VISIBLE);
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils.isNetworkAvailable(this)) {
                    com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd(this, rl_ad);
                }
            } else {
                rl_ad.setVisibility(android.view.View.GONE);
            }
        }
    }

    private CategoriesAdapter adapter;
    private ArrayList<String> filteredCategoriesList = new ArrayList<>();

    private class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_item_all_categories, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String catName = filteredCategoriesList.get(position);
            
            holder.tvCatName.setText(catName);

            boolean isSelected = catName.equalsIgnoreCase(currentlySelectedCategory);
            
            if (isSelected) {
                holder.bgSelected.setVisibility(View.VISIBLE);
                holder.tvCatName.setTextColor(0xFFFFFFFF); // White text
            } else {
                holder.bgSelected.setVisibility(View.GONE);
                holder.tvCatName.setTextColor(0xFF111827); // Dark grey text
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("selected_category_name", catName);
                setResult(RESULT_OK, intent);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return filteredCategoriesList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCatName;
            View bgSelected;

            ViewHolder(View itemView) {
                super(itemView);
                tvCatName = itemView.findViewById(R.id.tv_cat_name);
                bgSelected = itemView.findViewById(R.id.bg_selected);
            }
        }
    }
}
