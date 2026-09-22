package com.online.flyer.design.postermaker.Leaflet_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Leaflet_StickerViewPagerAdapter extends FragmentPagerAdapter {
    String[] cateName = new String[]{"Love", "Birthday", "Business", "Number", "Education", "Food", "Vehicle", "Icons", "Sales"};

    public Leaflet_StickerViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    public Fragment getItem(int position) {
        String categoryName = this.cateName[position];
        Leaflet_StickersFragment stickersFragment = new Leaflet_StickersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("categoryName", categoryName);
        stickersFragment.setArguments(bundle);
        return stickersFragment;
    }

    public CharSequence getPageTitle(int position) {
        return this.cateName[position];
    }

    public int getCount() {
        return this.cateName.length;
    }
}
