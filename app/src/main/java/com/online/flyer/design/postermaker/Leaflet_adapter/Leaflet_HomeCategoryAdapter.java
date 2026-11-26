package com.online.flyer.design.postermaker.Leaflet_adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_HomeCategory;
import com.online.flyer.design.postermaker.R;

import java.util.List;

/**
 * Horizontal adapter for the Home "Browse by Category" section.
 * Renders backend-provided pastel tiles and icons while preserving local fallback styling.
 */
public class Leaflet_HomeCategoryAdapter extends RecyclerView.Adapter<Leaflet_HomeCategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Leaflet_HomeCategory category, int position);
    }

    private final Context context;
    private final List<Leaflet_HomeCategory> categoryList;
    private OnCategoryClickListener listener;

    public Leaflet_HomeCategoryAdapter(Context context, List<Leaflet_HomeCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Leaflet_HomeCategory> newList) {
        if (this.categoryList != newList) {
            this.categoryList.clear();
            this.categoryList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaflet_item_home_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Leaflet_HomeCategory item = categoryList.get(position);

        holder.tvTitle.setText(item.getName());
        holder.tvTitle.setTextColor(item.isMoreTile()
                ? Color.parseColor("#4F46E5")
                : Color.parseColor("#1F2937"));

        // Adjust right margin: tighter on final "+ More" tile
        if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            int dpEnd = item.isMoreTile() ? 4 : 12;
            lp.setMarginEnd(dp(dpEnd));
            holder.itemView.setLayoutParams(lp);
        }

        // Tile background: prefer backend hex color, else local drawable fallback
        boolean bgApplied = false;
        if (!TextUtils.isEmpty(item.getBgColor())) {
            try {
                int fill = Color.parseColor(item.getBgColor().trim());
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(dp(18));
                shape.setColor(fill);
                if (!TextUtils.isEmpty(item.getBorderColor())) {
                    try {
                        shape.setStroke(dp(1), Color.parseColor(item.getBorderColor().trim()));
                    } catch (IllegalArgumentException ignored) {}
                } else {
                    shape.setStroke(dp(1), darkenSlightly(fill));
                }
                holder.tileBg.setBackground(shape);
                bgApplied = true;
            } catch (IllegalArgumentException ignored) {}
        }
        if (!bgApplied) {
            int bgRes = item.getLocalBgRes() != 0
                    ? item.getLocalBgRes()
                    : (item.isMoreTile() ? R.drawable.bg_cat_tile_more : R.drawable.bg_cat_tile_festival);
            holder.tileBg.setBackgroundResource(bgRes);
        }

        // Icon size: 28dp for More tile, 30dp for category tiles
        int iconSizePx = dp(item.isMoreTile() ? 28 : 30);
        ViewGroup.LayoutParams iconLp = holder.ivIcon.getLayoutParams();
        if (iconLp != null && (iconLp.width != iconSizePx || iconLp.height != iconSizePx)) {
            iconLp.width = iconSizePx;
            iconLp.height = iconSizePx;
            holder.ivIcon.setLayoutParams(iconLp);
        }

        int fallbackIcon = item.getLocalIconRes() != 0
                ? item.getLocalIconRes()
                : (item.isMoreTile() ? R.drawable.ic_cat_more_grid : R.drawable.ic_cat_diya_festival);

        if (!TextUtils.isEmpty(item.getIconUrl())) {
            Glide.with(context)
                    .load(item.getIconUrl().trim())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(fallbackIcon)
                    .error(fallbackIcon)
                    .into(holder.ivIcon);
        } else {
            Glide.with(context).clear(holder.ivIcon);
            holder.ivIcon.setImageResource(fallbackIcon);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(item, position);
            }
        });

        holder.itemView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100)
                            .setInterpolator(new DecelerateInterpolator()).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(2.5f)).start();
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private int dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, context.getResources().getDisplayMetrics());
    }

    private int darkenSlightly(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = Math.min(1f, hsv[1] * 1.4f);
        hsv[2] = Math.max(0f, hsv[2] * 0.92f);
        return Color.HSVToColor(Color.alpha(color), hsv);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout tileBg;
        final ImageView ivIcon;
        final TextView tvTitle;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tileBg = itemView.findViewById(R.id.cat_tile_bg);
            ivIcon = itemView.findViewById(R.id.cat_icon);
            tvTitle = itemView.findViewById(R.id.cat_title);
        }
    }
}
