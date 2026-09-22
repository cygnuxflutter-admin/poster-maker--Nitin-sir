package com.online.flyer.design.postermaker.Leaflet_controller;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterEditActivity;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_EffectAdapter;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;

public class Leaflet_EffectController {

    public SeekBar effect_alpha_seekbar;
    private final Activity activity;
    private final ImageView btn_clear_effect;
    private final RecyclerView effect_rv;
    private final LinearLayout ll_texture, ll_water, ll_blur;
    private Leaflet_EffectAdapter effectAdapter;

    public Leaflet_EffectController(Activity activity) {
        this.activity = activity;

        effect_alpha_seekbar = activity.findViewById(R.id.effect_alpha_seekbar);
        btn_clear_effect = activity.findViewById(R.id.btn_clear_effect);
        effect_rv = activity.findViewById(R.id.effect_rv);
        ll_texture = activity.findViewById(R.id.ll_texture);
        ll_water = activity.findViewById(R.id.ll_water);
        ll_blur = activity.findViewById(R.id.ll_blur);

        setAlphaLy();
        setEffectAdapter();
    }

    private void setEffectAdapter() {
        effect_rv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        effect_rv.setHasFixedSize(true);
        effectAdapter = new Leaflet_EffectAdapter(activity, Leaflet_FileUtils.listAssetFiles(activity, "texture"), (path) -> ((Leaflet_PosterEditActivity) activity).setOverlayImg(path));
        effect_rv.setAdapter(effectAdapter);

        ll_texture.setOnClickListener(v -> effectAdapter.updateRv(Leaflet_FileUtils.listAssetFiles(activity, "texture")));
        ll_water.setOnClickListener(v -> effectAdapter.updateRv(Leaflet_FileUtils.listAssetFiles(activity, "water_color")));
        ll_blur.setOnClickListener(v -> effectAdapter.updateRv(Leaflet_FileUtils.listAssetFiles(activity, "blur")));
    }

    public void notifyAdapter() {
        if (effectAdapter != null)
            effectAdapter.notifyDataSetChanged();
    }

    private void setAlphaLy() {
        effect_alpha_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((Leaflet_PosterEditActivity) activity).setOverlayOpacity(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btn_clear_effect.setOnClickListener(v -> effect_alpha_seekbar.setProgress(0));
    }
}
