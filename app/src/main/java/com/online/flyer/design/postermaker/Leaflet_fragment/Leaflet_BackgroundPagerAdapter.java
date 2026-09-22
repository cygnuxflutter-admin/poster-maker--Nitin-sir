package com.online.flyer.design.postermaker.Leaflet_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel;

import java.util.ArrayList;

public class Leaflet_BackgroundPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Leaflet_BgModel> posterDataLists;

    public Leaflet_BackgroundPagerAdapter(FragmentManager fm, ArrayList<Leaflet_BgModel> posterDataLists) {
        super(fm);
        this.posterDataLists = posterDataLists;
    }

    @NonNull
    public Fragment getItem(int position) {
        Leaflet_BackgroundFragment backgroundFragment = new Leaflet_BackgroundFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("posterDataLists", posterDataLists.get(position).getCategory_list());
        backgroundFragment.setArguments(bundle);
        return backgroundFragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public CharSequence getPageTitle(int position) {
        return this.posterDataLists.get(position).getCategory_name();
    }

    public int getCount() {
        return this.posterDataLists.size();
    }
}
