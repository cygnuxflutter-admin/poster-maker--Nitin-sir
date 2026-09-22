package com.online.flyer.design.postermaker.Leaflet_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterModel;

import java.util.ArrayList;

public class Leaflet_TemplatePagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Leaflet_PosterModel> posterModels;

    public Leaflet_TemplatePagerAdapter(@NonNull FragmentManager fm, ArrayList<Leaflet_PosterModel> posterModels) {
        super(fm);
        this.posterModels = posterModels;
    }
    @NonNull
    public Fragment getItem(int position) {
        int catId = posterModels.get(position).getCat_id();
        Leaflet_TemplateFragment templateFragment = new Leaflet_TemplateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("catId", catId);
        bundle.putSerializable("posterDataLists", posterModels.get(position).getPoster_list());
        templateFragment.setArguments(bundle);
        return templateFragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public CharSequence getPageTitle(int position) {
        return this.posterModels.get(position).getCat_name();
    }

    public int getCount() {
        return this.posterModels.size();
    }

}
