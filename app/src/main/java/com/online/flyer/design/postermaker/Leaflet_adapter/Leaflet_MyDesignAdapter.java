package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_components.Leaflet_StickerInfo;
import com.online.flyer.design.postermaker.Leaflet_components.Leaflet_TemplateInfo;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DatabaseHandler;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;

import java.io.File;
import java.util.ArrayList;

import static com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils.deleteRecursive;

public class Leaflet_MyDesignAdapter extends RecyclerView.Adapter<Leaflet_MyDesignAdapter.MyViewHolder> {

    private final Activity activity;
    private final ArrayList<Leaflet_TemplateInfo> templateInfoArrayList;
    private final int cellWidth, cellHeight;
    private final MyDesignClickListener designClickListener;
    private final ArrayList<File> fileArrayList = new ArrayList<>();

    public Leaflet_MyDesignAdapter(Activity activity, ArrayList<Leaflet_TemplateInfo> templateInfoArrayList, int cellWidth, int cellHeight, MyDesignClickListener designClickListener) {
        this.activity = activity;
        this.templateInfoArrayList = templateInfoArrayList;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.designClickListener = designClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaflet_design_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.imageView.getLayoutParams().width = cellWidth;
        holder.imageView.getLayoutParams().height = cellHeight;
        holder.imageView.invalidate();

        Glide.with(this.activity)
                .load(templateInfoArrayList.get(position).getTHUMB_URI())
                .thumbnail(0.1f).dontAnimate()
                .placeholder(R.drawable.leaflet_no_image)
                .error(R.drawable.leaflet_no_image)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(view -> {
            Leaflet_DatabaseHandler dh = Leaflet_DatabaseHandler.getDbHandler(activity);
            ArrayList<Leaflet_TemplateInfo> templateList = dh.getTemplateListDes("USER");
            ArrayList<Leaflet_StickerInfo> stickerInfoList = dh.getComponentInfoList(templateList.get(position).getTEMPLATE_ID(), "STICKER");
            fileArrayList.clear();
            fileArrayList.add(new File(templateInfoArrayList.get(position).getTHUMB_URI()));
            fileArrayList.add(new File(templateInfoArrayList.get(position).getFRAME_NAME()));
            for (int i = 0; i < stickerInfoList.size(); i++) {
                fileArrayList.add(new File(stickerInfoList.get(i).getSTKR_PATH()));
            }

            if (!Leaflet_FileUtils.checkFileExistOrNot(fileArrayList)) {
                Toast.makeText(activity, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                return;
            }

            designClickListener.onPostClick(templateInfoArrayList.get(position).getTEMPLATE_ID(), position);

        });
        holder.btn_del.setOnClickListener(view -> Leaflet_MaterialDialogUtils.getInstance().DeleteDialog(activity, dialog -> {
            File folder = Leaflet_FileUtils.getSaveFileLocation(activity, "MyDesigns/" + templateInfoArrayList.get(position).getTEMP_PATH());
            File folder2 = Leaflet_FileUtils.getSaveFileLocation(activity, "category1/" + templateInfoArrayList.get(position).getTEMP_PATH());
            if (folder.exists()) {
                deleteRecursive(folder);
            }
            if (folder2.exists()) {
                deleteRecursive(folder2);
            }
            Leaflet_DatabaseHandler dh = Leaflet_DatabaseHandler.getDbHandler(activity);
            dh.deleteTemplateInfo(templateInfoArrayList.get(position).getTEMPLATE_ID());
            templateInfoArrayList.remove(position);
            notifyDataSetChanged();
            if (templateInfoArrayList.size() == 0) {
                designClickListener.onEmptyAdapter();
            }

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }));
    }

    @Override
    public int getItemCount() {
        return templateInfoArrayList.size();
    }

    public interface MyDesignClickListener {
        void onPostClick(int post_id, int position);

        void onEmptyAdapter();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageView btn_del;

        public MyViewHolder(View v) {
            super(v);
            imageView = itemView.findViewById(R.id.kenBurnsView);
            btn_del = itemView.findViewById(R.id.btn_del);
        }
    }
}
