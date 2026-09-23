package com.online.flyer.design.postermaker.Leaflet_controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Matrix;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterEditActivity;
import com.online.flyer.design.postermaker.Leaflet_color_palette.Leaflet_LineColorPicker;
import com.online.flyer.design.postermaker.Leaflet_color_palette.Leaflet_OnColorChangedListener;
import com.online.flyer.design.postermaker.Leaflet_components.Leaflet_ResizableStickerView;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_RepeatListener;
import com.online.flyer.design.postermaker.Leaflet_view.Leaflet_GridLine;
import com.online.flyer.design.postermaker.Leaflet_view.Leaflet_HorizontalProgressWheelView;

import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterEditActivity.txt_stkr_rel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.graphics.Bitmap;

public class Leaflet_StickerController {

    private final Activity activity;
    private final LinearLayout ly_controlsShow, lay_colorOpacity, ly_stk_rotation;
    private final ImageView pick_hue_color;
    private final RelativeLayout sti_btn_up, sti_btnLeft, sti_btnRight, sti_btnDown, lay_duplicateSticker, lay_str_delete;
    private final LinearLayout lay_remove_bg;
    private final TextView stk_view_rotate;
    private final SeekBar alpha_seekBar;
    private final Leaflet_LineColorPicker hue_seekBar;
    private final float[] mMatrixValues = new float[9];
    protected Matrix mCurrentImageMatrix = new Matrix();
    private Leaflet_ResizableStickerView resizableStickerView;
    private Leaflet_GridLine gridLine;

    public Leaflet_StickerController(Activity activity, Leaflet_GridLine gridLine) {
        this.activity = activity;
        this.gridLine = gridLine;

        RelativeLayout btn_controlStkr = activity.findViewById(R.id.btn_controlStkr);
        RelativeLayout btn_colorOpacity = activity.findViewById(R.id.btn_colorOpacity);
        RelativeLayout btn_stk_rotation = activity.findViewById(R.id.btn_stk_rotation);

        Leaflet_HorizontalProgressWheelView stk_rotate_scroll_wheel = activity.findViewById(R.id.stk_rotate_scroll_wheel);
        FrameLayout stk_wrapper_reset_rotate = activity.findViewById(R.id.stk_wrapper_reset_rotate);
        FrameLayout stk_wrapper_rotate_by_angle = activity.findViewById(R.id.stk_wrapper_rotate_by_angle);

        ly_controlsShow = activity.findViewById(R.id.ly_controlsShow);
        lay_colorOpacity = activity.findViewById(R.id.lay_colorOpacity);
        ly_stk_rotation = activity.findViewById(R.id.ly_stk_rotation);

        pick_hue_color = activity.findViewById(R.id.pick_hue_color);

        stk_view_rotate = activity.findViewById(R.id.stk_view_rotate);

        sti_btn_up = activity.findViewById(R.id.sti_btn_up);
        sti_btnLeft = activity.findViewById(R.id.sti_btnLeft);
        sti_btnRight = activity.findViewById(R.id.sti_btnRight);
        sti_btnDown = activity.findViewById(R.id.sti_btnDown);
        lay_duplicateSticker = activity.findViewById(R.id.lay_duplicateSticker);
        lay_str_delete = activity.findViewById(R.id.lay_str_delete);
        lay_remove_bg = activity.findViewById(R.id.lay_remove_bg);

        alpha_seekBar = activity.findViewById(R.id.alpha_seekBar);
        hue_seekBar = activity.findViewById(R.id.hue_seekBar);

        btn_controlStkr.setOnClickListener(v -> showLayout(ly_controlsShow));
        btn_colorOpacity.setOnClickListener(v -> showLayout(lay_colorOpacity));
        btn_stk_rotation.setOnClickListener(v -> showLayout(ly_stk_rotation));

        setControlLy();
        setColorAndOpaLy();

        stk_rotate_scroll_wheel.setScrollingListener(new Leaflet_HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                float deltaAngle = delta / 42;
                postRotate(deltaAngle, false);
            }

            @Override
            public void onScrollEnd() {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "move", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        });

        stk_wrapper_reset_rotate.setOnClickListener(v -> {
            if (resizableStickerView.getRotation() != 0.0f || resizableStickerView.getRotation() != -0.0f) {
                postRotate(0f, true);
            }
        });
        stk_wrapper_rotate_by_angle.setOnClickListener(v -> postRotate(90f, true));
    }

    public void postRotate(float deltaAngle, boolean update) {
        if (deltaAngle != 0) {
            mCurrentImageMatrix.postRotate(deltaAngle);
            setRotation(getMatrixAngle(mCurrentImageMatrix), update);
        } else {
            mCurrentImageMatrix.reset();
            setRotation(0.0f, update);
        }
    }

    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X), getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    protected float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = 9) int valueIndex) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    public void setRotation(float rotation, boolean update) {
        resizableStickerView.setRotation(rotation);
        if (rotation == -0.0f) {
            stk_view_rotate.setText(String.format(Locale.getDefault(), "%.1f°", 0.0f));
        } else {
            stk_view_rotate.setText(String.format(Locale.getDefault(), "%.1f°", rotation));
        }

        if (update) {
            ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "nothing", txt_stkr_rel.indexOfChild(resizableStickerView));
        }
    }

    private void setColorAndOpaLy() {
        alpha_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (resizableStickerView != null)
                    resizableStickerView.setAlphaProg(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "nothing", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        });

        pick_hue_color.setOnClickListener(v -> new AmbilWarnaDialog(activity, resizableStickerView.getHueProg(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onOk(AmbilWarnaDialog dialog, int color) {
                resizableStickerView.setHueProg(color);
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "nothing", txt_stkr_rel.indexOfChild(resizableStickerView));
            }

            public void onCancel(AmbilWarnaDialog dialog) {
            }
        }).show());

        hue_seekBar.setColors(activity.getResources().getIntArray(R.array.palette2));
        hue_seekBar.setOnColorChangedListener(new Leaflet_OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                resizableStickerView.setHueProg(color);
            }

            @Override
            public void onFinish() {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "nothing", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setControlLy() {
        sti_btn_up.setOnTouchListener(new Leaflet_RepeatListener(200, 100, gridLine, new Leaflet_RepeatListener.RepeatInterface() {
            @Override
            public void onClick(View view) {
                resizableStickerView.decY();
            }

            @Override
            public void onFinish() {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "move", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        }));

        sti_btnLeft.setOnTouchListener(new Leaflet_RepeatListener(200, 100, gridLine, new Leaflet_RepeatListener.RepeatInterface() {
            @Override
            public void onClick(View view) {
                resizableStickerView.decX();
            }

            @Override
            public void onFinish() {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "move", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        }));

        sti_btnRight.setOnTouchListener(new Leaflet_RepeatListener(200, 100, gridLine, new Leaflet_RepeatListener.RepeatInterface() {
            @Override
            public void onClick(View view) {
                resizableStickerView.incrX();
            }

            @Override
            public void onFinish() {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "move", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        }));

        sti_btnDown.setOnTouchListener(new Leaflet_RepeatListener(200, 100, gridLine, new Leaflet_RepeatListener.RepeatInterface() {
            @Override
            public void onClick(View view) {
                resizableStickerView.incrY();
            }

            @Override
            public void onFinish() {
                ((Leaflet_PosterEditActivity) activity).notifyUndoRedo("sticker", "move", txt_stkr_rel.indexOfChild(resizableStickerView));
            }
        }));

        lay_duplicateSticker.setOnClickListener(v -> {
            if (activity instanceof Leaflet_PosterEditActivity) {
                Leaflet_ResizableStickerView resizableStickerView = new Leaflet_ResizableStickerView(activity);
                ((Leaflet_PosterEditActivity) activity).addNewResizableStickerView(resizableStickerView, this.resizableStickerView.getComponentInfo(), false);
            }
        });

        lay_str_delete.setOnClickListener(v -> {
            if (activity instanceof Leaflet_PosterEditActivity && resizableStickerView != null) {
                resizableStickerView.deleteSticker();
            }
        });

        lay_remove_bg.setOnClickListener(v -> {
            if (activity instanceof Leaflet_PosterEditActivity && resizableStickerView != null) {
                Bitmap bitmap = resizableStickerView.getMainImageBitmap();
                if (bitmap != null) {
                    ProgressDialog pd = new ProgressDialog(activity);
                    pd.setMessage("Removing Background...");
                    pd.setCancelable(false);
                    pd.show();

                    SubjectSegmenterOptions options = new SubjectSegmenterOptions.Builder()
                            .enableForegroundBitmap()
                            .build();
                    SubjectSegmenter segmenter = SubjectSegmentation.getClient(options);
                    InputImage image = InputImage.fromBitmap(bitmap, 0);

                    segmenter.process(image)
                            .addOnSuccessListener(result -> {
                                Bitmap foregroundBitmap = result.getForegroundBitmap();
                                if (foregroundBitmap != null) {
                                    resizableStickerView.setMainImageBitmap(foregroundBitmap);
                                } else {
                                    Toast.makeText(activity, "No subject found", Toast.LENGTH_SHORT).show();
                                }
                                pd.dismiss();
                                segmenter.close();
                            })
                            .addOnFailureListener(e -> {
                                pd.dismiss();
                                Toast.makeText(activity, "Failed to remove background", Toast.LENGTH_SHORT).show();
                                segmenter.close();
                            });
                }
            }
        });

    }

    private void showLayout(View view) {
        ly_controlsShow.setVisibility(View.GONE);
        lay_colorOpacity.setVisibility(View.GONE);
        ly_stk_rotation.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    public void setResizableStickerView(Leaflet_ResizableStickerView resizableStickerView, Leaflet_GridLine gridLine) {
        this.resizableStickerView = resizableStickerView;
        this.gridLine = gridLine;
        this.alpha_seekBar.setProgress(resizableStickerView.getAlphaProg());
        mCurrentImageMatrix.postRotate(resizableStickerView.getRotation());
        stk_view_rotate.setText(String.format(Locale.getDefault(), "%.1f°", resizableStickerView.getRotation()));
    }
}

