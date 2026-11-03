package com.online.flyer.design.postermaker.Leaflet_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_SearchCategoryAdapter;
import com.online.flyer.design.postermaker.Leaflet_model.SearchCategoryItem;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Leaflet_SearchActivity extends AppCompatActivity {

    private enum FilterMode {
        ALL,
        TEMPLATES,
        BACKGROUNDS
    }

    private EditText etSearch;
    private ImageView icClearSearch;
    private TextView chipAll;
    private TextView chipTemplates;
    private TextView chipBackgrounds;
    private TextView tvSearchSummary;
    private RecyclerView rvSearchResults;
    private LinearLayout llEmptyState;
    private ProgressBar pbSearchLoading;

    private Leaflet_SearchCategoryAdapter adapter;
    private final List<SearchCategoryItem> allCategoriesList = new ArrayList<>();
    private final List<SearchCategoryItem> displayedList = new ArrayList<>();
    private FilterMode currentFilter = FilterMode.ALL;
    private Leaflet_PreferenceClass preferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.leaflet_activity_search);

        preferenceClass = new Leaflet_PreferenceClass(this);

        initViews();
        setupRecyclerView();
        setupFilterChips();
        setupSearchInput();
        loadInitialCategories();
        fetchLiveCategoriesIfPossible();

        // Auto Focus & Soft Keyboard auto-popup with 200ms delay
        etSearch.requestFocus();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 200);
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        icClearSearch = findViewById(R.id.ic_clear_search);
        chipAll = findViewById(R.id.chip_all);
        chipTemplates = findViewById(R.id.chip_templates);
        chipBackgrounds = findViewById(R.id.chip_backgrounds);
        tvSearchSummary = findViewById(R.id.tv_search_summary);
        rvSearchResults = findViewById(R.id.rv_search_results);
        llEmptyState = findViewById(R.id.ll_empty_state);
        pbSearchLoading = findViewById(R.id.pb_search_loading);

        findViewById(R.id.ic_back).setOnClickListener(v -> handleBackWithAd());
    }

    private void setupRecyclerView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Leaflet_SearchCategoryAdapter(this, this::onCategoryClicked);
        rvSearchResults.setAdapter(adapter);
    }

    private void setupFilterChips() {
        chipAll.setOnClickListener(v -> {
            if (currentFilter != FilterMode.ALL) {
                currentFilter = FilterMode.ALL;
                updateChipUi();
                filterCategories(etSearch.getText().toString());
            }
        });

        chipTemplates.setOnClickListener(v -> {
            if (currentFilter != FilterMode.TEMPLATES) {
                currentFilter = FilterMode.TEMPLATES;
                updateChipUi();
                filterCategories(etSearch.getText().toString());
            }
        });

        chipBackgrounds.setOnClickListener(v -> {
            if (currentFilter != FilterMode.BACKGROUNDS) {
                currentFilter = FilterMode.BACKGROUNDS;
                updateChipUi();
                filterCategories(etSearch.getText().toString());
            }
        });
    }

    private void updateChipUi() {
        // Reset all to unselected
        chipAll.setBackgroundResource(R.drawable.bg_search_chip_unselected);
        chipAll.setTextColor(Color.parseColor("#64748B"));

        chipTemplates.setBackgroundResource(R.drawable.bg_search_chip_unselected);
        chipTemplates.setTextColor(Color.parseColor("#64748B"));

        chipBackgrounds.setBackgroundResource(R.drawable.bg_search_chip_unselected);
        chipBackgrounds.setTextColor(Color.parseColor("#64748B"));

        // Highlight selected
        if (currentFilter == FilterMode.ALL) {
            chipAll.setBackgroundResource(R.drawable.bg_search_chip_selected);
            chipAll.setTextColor(Color.WHITE);
        } else if (currentFilter == FilterMode.TEMPLATES) {
            chipTemplates.setBackgroundResource(R.drawable.bg_search_chip_selected);
            chipTemplates.setTextColor(Color.WHITE);
        } else {
            chipBackgrounds.setBackgroundResource(R.drawable.bg_search_chip_selected);
            chipBackgrounds.setTextColor(Color.WHITE);
        }
    }

    private void setupSearchInput() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    icClearSearch.setVisibility(View.VISIBLE);
                } else {
                    icClearSearch.setVisibility(View.GONE);
                }
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        icClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    /**
     * 3-Tier Priority Real-Time Filter:
     * 1. startsWith (e.g. "Di" -> "Diwali" on top)
     * 2. Any word starts with query
     * 3. Substring contains
     */
    private void filterCategories(String query) {
        String cleanQuery = (query == null) ? "" : query.trim().toLowerCase();

        List<SearchCategoryItem> poolByFilter = new ArrayList<>();
        for (SearchCategoryItem item : allCategoriesList) {
            if (currentFilter == FilterMode.ALL) {
                poolByFilter.add(item);
            } else if (currentFilter == FilterMode.TEMPLATES && item.isTemplate()) {
                poolByFilter.add(item);
            } else if (currentFilter == FilterMode.BACKGROUNDS && !item.isTemplate()) {
                poolByFilter.add(item);
            }
        }

        if (TextUtils.isEmpty(cleanQuery)) {
            displayedList.clear();
            displayedList.addAll(poolByFilter);
        } else {
            List<SearchCategoryItem> tier1 = new ArrayList<>(); // startsWith
            List<SearchCategoryItem> tier2 = new ArrayList<>(); // word initial match
            List<SearchCategoryItem> tier3 = new ArrayList<>(); // contains
            Set<Integer> addedIds = new HashSet<>();

            for (SearchCategoryItem item : poolByFilter) {
                String name = item.getName().toLowerCase();
                if (name.startsWith(cleanQuery)) {
                    tier1.add(item);
                    addedIds.add(item.getId());
                }
            }

            for (SearchCategoryItem item : poolByFilter) {
                if (addedIds.contains(item.getId())) continue;
                String[] words = item.getName().toLowerCase().split("\\s+");
                boolean wordMatched = false;
                for (String word : words) {
                    if (word.startsWith(cleanQuery)) {
                        wordMatched = true;
                        break;
                    }
                }
                if (wordMatched) {
                    tier2.add(item);
                    addedIds.add(item.getId());
                }
            }

            for (SearchCategoryItem item : poolByFilter) {
                if (addedIds.contains(item.getId())) continue;
                if (item.getName().toLowerCase().contains(cleanQuery)) {
                    tier3.add(item);
                    addedIds.add(item.getId());
                }
            }

            displayedList.clear();
            displayedList.addAll(tier1);
            displayedList.addAll(tier2);
            displayedList.addAll(tier3);
        }

        adapter.updateList(displayedList);

        if (displayedList.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
            tvSearchSummary.setText("0 categories found");
        } else {
            rvSearchResults.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            tvSearchSummary.setText("Showing " + displayedList.size() + " categories");
        }
    }

    private void onCategoryClicked(SearchCategoryItem item) {
        hideKeyboard();
        // Trigger Interstitial Ad with count logic
        Leaflet_MyApplication.showSearchInterstitialAd(this, () -> navigateToCategory(item));
    }

    private void navigateToCategory(SearchCategoryItem item) {
        if (item.isTemplate()) {
            Intent intent = new Intent(this, Leaflet_TemplateSelectionActivity.class);
            intent.putExtra("category_name", item.getName());
            intent.putExtra("category_id", String.valueOf(item.getId()));
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Leaflet_BackgroundSelectionActivity.class);
            intent.putExtra("category_name", item.getName());
            intent.putExtra("category_id", String.valueOf(item.getId()));
            startActivity(intent);
        }
    }

    private void handleBackWithAd() {
        hideKeyboard();
        Leaflet_MyApplication.showSearchInterstitialAd(this, this::finish);
    }

    @Override
    public void onBackPressed() {
        handleBackWithAd();
    }

    
    private boolean isUrl(String text) {
        if (text == null) return false;
        String lower = text.trim().toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://") || lower.contains("cygnux.in") || lower.contains("/uploads/") || lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void loadInitialCategories() {
        allCategoriesList.clear();

        // Default Template Categories
        allCategoriesList.add(new SearchCategoryItem(1, "Festival", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(2, "Birthday", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(3, "Business", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(4, "Sale & Offers", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(5, "Social Media", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(6, "Diwali", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(7, "Holi", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(8, "Raksha Bandhan", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(9, "Christmas", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(10, "New Year", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(11, "Food & Restaurant", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(12, "Education", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(13, "Fashion & Beauty", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(14, "Real Estate", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(15, "Music & Party", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(16, "Gym & Fitness", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(17, "Quotes & Motivation", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(18, "Summer", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(19, "Thank You", SearchCategoryItem.CategoryType.TEMPLATE, null));
        allCategoriesList.add(new SearchCategoryItem(20, "Events", SearchCategoryItem.CategoryType.TEMPLATE, null));

        // Default Background Categories
        allCategoriesList.add(new SearchCategoryItem(101, "Abstract", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(102, "Art", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(103, "Assets", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(104, "Christmas", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(105, "Texture", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(106, "Nature", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(107, "Pattern", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(108, "Gradient", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(109, "Vintage", SearchCategoryItem.CategoryType.BACKGROUND, null));
        allCategoriesList.add(new SearchCategoryItem(110, "Minimal", SearchCategoryItem.CategoryType.BACKGROUND, null));

        enrichFromLocalCache();
        filterCategories("");
    }

    private void enrichFromLocalCache() {
        try {
            String tmplCache = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().getCache(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.KEY_TEMPLATES);
            if (tmplCache != null) {
                JSONObject jsonObject = new JSONObject(tmplCache);
                String field4 = preferenceClass.getDataType("field_4");
                if (jsonObject.has(field4)) {
                    JSONArray array = jsonObject.getJSONArray(field4);
                    Set<String> existingNames = new HashSet<>();
                    for (SearchCategoryItem item : allCategoriesList) {
                        existingNames.add(item.getName().toLowerCase());
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String catName = obj.optString(preferenceClass.getDataType("field_5"), "");
                        if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                            catName = obj.optString(preferenceClass.getDataType("field_20"), "");
                        }
                        if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                            catName = obj.optString("cat_name", "");
                        }
                        if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                            catName = obj.optString("name", "");
                        }
                        int catId = obj.optInt(preferenceClass.getDataType("field_46"), i + 1);
                        if (!TextUtils.isEmpty(catName) && !isUrl(catName) && !existingNames.contains(catName.toLowerCase())) {
                            allCategoriesList.add(new SearchCategoryItem(catId, catName, SearchCategoryItem.CategoryType.TEMPLATE, null));
                            existingNames.add(catName.toLowerCase());
                        }
                    }
                }
            }

            String bgCache = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().getCache(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.KEY_BACKGROUNDS);
            if (bgCache != null) {
                JSONObject jsonObject = new JSONObject(bgCache);
                String field39 = preferenceClass.getDataType("field_39");
                if (jsonObject.has(field39)) {
                    JSONArray array = jsonObject.getJSONArray(field39);
                    Set<String> existingNames = new HashSet<>();
                    for (SearchCategoryItem item : allCategoriesList) {
                        existingNames.add(item.getName().toLowerCase());
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String catName = obj.optString(preferenceClass.getDataType("field_41"), "");
                        if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                            catName = obj.optString("cat_name", "");
                        }
                        if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                            catName = obj.optString("name", "");
                        }
                        int catId = obj.optInt(preferenceClass.getDataType("field_40"), 100 + i);
                        if (!TextUtils.isEmpty(catName) && !isUrl(catName) && !existingNames.contains(catName.toLowerCase())) {
                            allCategoriesList.add(new SearchCategoryItem(catId, catName, SearchCategoryItem.CategoryType.BACKGROUND, null));
                            existingNames.add(catName.toLowerCase());
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Optional live API fetch to enrich categories dynamically if network is ready
     */
    private void fetchLiveCategoriesIfPossible() {
        if (preferenceClass == null) return;
        try {
            String baseUrl = preferenceClass.getDataType("field_1");
            String subPath = preferenceClass.getDataType("field_34");
            String templateEndpoint = preferenceClass.getDataType("field_35");
            String key = preferenceClass.getDataType("field_0");

            if (TextUtils.isEmpty(baseUrl) || TextUtils.isEmpty(templateEndpoint)) return;

            String requestUrl = baseUrl + subPath + templateEndpoint;
            StringRequest request = new StringRequest(Request.Method.POST, requestUrl, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int error = jsonObject.optInt(preferenceClass.getDataType("field_3"), 0);
                    if (error == 1) {
                        JSONArray array = jsonObject.getJSONArray(preferenceClass.getDataType("field_4"));
                        Set<String> existingNames = new HashSet<>();
                        for (SearchCategoryItem item : allCategoriesList) {
                            existingNames.add(item.getName().toLowerCase());
                        }

                        boolean updated = false;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String catName = obj.optString(preferenceClass.getDataType("field_5"), "");
                            if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                                catName = obj.optString(preferenceClass.getDataType("field_20"), "");
                            }
                            if (TextUtils.isEmpty(catName) || isUrl(catName)) {
                                catName = obj.optString("cat_name", "");
                            }
                            int catId = obj.optInt(preferenceClass.getDataType("field_46"), obj.optInt(preferenceClass.getDataType("field_18"), i + 1));
                            if (!TextUtils.isEmpty(catName) && !isUrl(catName) && !existingNames.contains(catName.toLowerCase())) {
                                allCategoriesList.add(new SearchCategoryItem(catId, catName, SearchCategoryItem.CategoryType.TEMPLATE, null));
                                existingNames.add(catName.toLowerCase());
                                updated = true;
                            }
                        }
                        if (updated) {
                            filterCategories(etSearch.getText().toString());
                        }
                    }
                } catch (Exception ignored) {}
            }, error -> {}) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postMap = new HashMap<>();
                    postMap.put(preferenceClass.getDataType("field_47"), key);
                    postMap.put(preferenceClass.getDataType("field_48"), "1");
                    postMap.put(preferenceClass.getDataType("field_46"), "0");
                    postMap.put(preferenceClass.getDataType("field_8"), "0");
                    return postMap;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (Exception ignored) {}
    }
}
