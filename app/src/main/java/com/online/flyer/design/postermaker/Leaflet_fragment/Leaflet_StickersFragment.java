package com.online.flyer.design.postermaker.Leaflet_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_StickerAdapter;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;

public class Leaflet_StickersFragment extends Fragment {

    private GetSnapListener onGetSnap;

    public interface GetSnapListener {
        void onSnapFilter(String str);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaflet_template_fragment, container, false);
        assert getArguments() != null;
        String catName = getArguments().getString("categoryName");
        this.onGetSnap = (GetSnapListener) getActivity();

        RecyclerView sticker_rv = view.findViewById(R.id.template_rv);

        sticker_rv.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        sticker_rv.setLayoutManager(layoutManager);

        Leaflet_StickerAdapter stickerAdapter = new Leaflet_StickerAdapter(getActivity(), Leaflet_FileUtils.listAssetFiles(getActivity(), "stickers/" + catName), (path) -> onGetSnap.onSnapFilter(path));
        sticker_rv.setAdapter(stickerAdapter);
        return view;
    }
}
