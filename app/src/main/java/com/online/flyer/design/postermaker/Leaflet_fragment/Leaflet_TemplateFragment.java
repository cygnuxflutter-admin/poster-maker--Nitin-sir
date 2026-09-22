package com.online.flyer.design.postermaker.Leaflet_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_PosterGroupChildAdapter;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage;

import java.util.ArrayList;
import java.util.Objects;

public class Leaflet_TemplateFragment extends Fragment {

    private RecyclerView template_rv;
    private int catId;
    private GetPosterListener getPosterListener;
    private ArrayList<Leaflet_PosterImage> posterThumbFulls;

    public interface GetPosterListener {
        void onPosterClick(int cat_id, int post_id, boolean premium);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaflet_template_fragment, container, false);
        assert getArguments() != null;
        this.catId = getArguments().getInt("catId");
        //noinspection unchecked
        posterThumbFulls = (ArrayList<Leaflet_PosterImage>) getArguments().getSerializable("posterDataLists");
        this.getPosterListener = (GetPosterListener) getActivity();
        template_rv = view.findViewById(R.id.template_rv);
        template_rv.setAdapter(null);
        setPosterAdapter();
        return view;
    }

    public void setPosterAdapter() {
        template_rv.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);

        template_rv.setLayoutManager(layoutManager);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float screenDensity = getResources().getDisplayMetrics().density;
        int screenWidth = (displayMetrics).widthPixels;
        int cellWidth = (int) (screenWidth - (42 * screenDensity)) / 2;
        int cellHeight = (700 * cellWidth) / 507;

        Leaflet_PosterGroupChildAdapter posterGroupChildAdapter = new Leaflet_PosterGroupChildAdapter(getActivity(), posterThumbFulls, catId, cellWidth, cellHeight, (cat_id, post_id, premium) -> getPosterListener.onPosterClick(cat_id, post_id, premium));
        template_rv.setAdapter(posterGroupChildAdapter);
        template_rv.invalidate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}
