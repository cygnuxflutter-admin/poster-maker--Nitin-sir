package com.online.flyer.design.postermaker.Leaflet_utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Global celebration helper that triggers dynamic fireworks, confetti particles,
 * haptic vibration, and a celebratory badge when user unlocks rewards or templates.
 */
public class Leaflet_CelebrationHelper {

    public static void showCelebration(@Nullable final Activity activity) {
        showCelebration(activity, "🎉 Reward Unlocked!");
    }

    public static void showTemplateUnlocked(@Nullable final Activity activity) {
        showCelebration(activity, "🎉 Template Unlocked!");
    }

    public static void showBackgroundUnlocked(@Nullable final Activity activity) {
        showCelebration(activity, "✨ Background Unlocked!");
    }

    public static void showTimeUnlocked(@Nullable final Activity activity) {
        showCelebration(activity, "⏱️ Editing Time Unlocked!");
    }

    public static void showSaveCelebration(@Nullable final Activity activity) {
        showCelebration(activity, "🎉 Poster Saved Successfully!");
    }

    public static void showCelebration(@Nullable final Activity activity, @Nullable final String message) {
        if (activity == null || activity.isFinishing()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) return;

        activity.runOnUiThread(() -> {
            try {
                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                if (decorView == null) return;

                // Trigger subtle haptic vibration
                triggerHaptic(activity);

                // Create and attach celebration overlay view
                CelebrationOverlayView overlay = new CelebrationOverlayView(activity, message);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                decorView.addView(overlay, lp);
                overlay.startAnimation();
            } catch (Exception ignored) {}
        });
    }

    private static void triggerHaptic(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Custom full-screen overlay view rendering physics-based fireworks and confetti bursts.
     */
    public static class CelebrationOverlayView extends View {

        private static final int PARTICLE_COUNT = 150;
        private static final long DURATION_MS = 2400;

        private final String message;
        private final List<Particle> particles = new ArrayList<>();
        private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint badgeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint badgeStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF badgeRect = new RectF();
        private final Path starPath = new Path();

        private ValueAnimator animator;
        private float animProgress = 0f;
        private boolean isInitialized = false;

        private static final int[] COLORS = new int[]{
                Color.parseColor("#FFD700"), // Gold
                Color.parseColor("#FF3366"), // Vibrant Rose
                Color.parseColor("#00E5FF"), // Electric Cyan
                Color.parseColor("#10B981"), // Emerald Green
                Color.parseColor("#A855F7"), // Purple
                Color.parseColor("#FF6D00"), // Sunset Orange
                Color.parseColor("#3B82F6"), // Royal Blue
                Color.parseColor("#FFEB3B"), // Bright Yellow
                Color.parseColor("#EC4899")  // Pink
        };

        public CelebrationOverlayView(Context context, @Nullable String message) {
            super(context);
            this.message = message;
            init();
        }

        public CelebrationOverlayView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.message = null;
            init();
        }

        private void init() {
            setClickable(false);
            setFocusable(false);

            badgeBgPaint.setStyle(Paint.Style.FILL);
            badgeBgPaint.setColor(Color.parseColor("#1E293B")); // Modern dark slate

            badgeStrokePaint.setStyle(Paint.Style.STROKE);
            badgeStrokePaint.setColor(Color.parseColor("#38BDF8")); // Cyan glow stroke
            badgeStrokePaint.setStrokeWidth(dpToPx(1.5f));

            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(spToPx(15));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setFakeBoldText(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Allow all touches to pass through to underlying views
            return false;
        }

        private void initParticles(int w, int h) {
            particles.clear();
            Random rng = new Random();

            // 3 Firework explosion centers
            float c1X = w * 0.5f;
            float c1Y = h * 0.35f;

            float c2X = w * 0.25f;
            float c2Y = h * 0.38f;

            float c3X = w * 0.75f;
            float c3Y = h * 0.38f;

            for (int i = 0; i < PARTICLE_COUNT; i++) {
                float originX;
                float originY;
                if (i < PARTICLE_COUNT * 0.5f) {
                    originX = c1X;
                    originY = c1Y;
                } else if (i < PARTICLE_COUNT * 0.75f) {
                    originX = c2X;
                    originY = c2Y;
                } else {
                    originX = c3X;
                    originY = c3Y;
                }

                Particle p = new Particle();
                p.x = originX + (rng.nextFloat() - 0.5f) * dpToPx(20);
                p.y = originY + (rng.nextFloat() - 0.5f) * dpToPx(20);

                // Angle and speed
                double angle = rng.nextDouble() * 2 * Math.PI;
                float speed = dpToPx(rng.nextFloat() * 16f + 6f);
                p.vx = (float) (Math.cos(angle) * speed);
                p.vy = (float) (Math.sin(angle) * speed) - dpToPx(rng.nextFloat() * 6f + 2f); // Slight upward bias

                p.color = COLORS[rng.nextInt(COLORS.length)];
                p.type = rng.nextInt(3); // 0 = Rect confetti, 1 = Circle spark, 2 = Star sparkle
                p.size = dpToPx(rng.nextFloat() * 8f + 6f);
                p.gravity = dpToPx(rng.nextFloat() * 0.35f + 0.25f);
                p.drag = rng.nextFloat() * 0.03f + 0.95f;

                p.rotation = rng.nextFloat() * 360f;
                p.rotationSpeed = (rng.nextFloat() - 0.5f) * 12f;
                p.flutterAngle = rng.nextFloat() * 360f;
                p.flutterSpeed = rng.nextFloat() * 10f + 5f;

                particles.add(p);
            }
            isInitialized = true;
        }

        public void startAnimation() {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(DURATION_MS);
            animator.setInterpolator(new DecelerateInterpolator(1.2f));
            animator.addUpdateListener(animation -> {
                animProgress = (float) animation.getAnimatedValue();
                updateParticlePhysics();
                invalidate();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (getParent() instanceof ViewGroup) {
                        ((ViewGroup) getParent()).removeView(CelebrationOverlayView.this);
                    }
                }
            });
            animator.start();
        }

        private void updateParticlePhysics() {
            for (Particle p : particles) {
                p.x += p.vx;
                p.y += p.vy;
                p.vy += p.gravity;
                p.vx *= p.drag;
                p.vy *= p.drag;

                p.rotation += p.rotationSpeed;
                p.flutterAngle += p.flutterSpeed;
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w > 0 && h > 0 && !isInitialized) {
                initParticles(w, h);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!isInitialized) return;

            // Overall fade-out in last 25% of duration
            float globalAlpha = 1f;
            if (animProgress > 0.75f) {
                globalAlpha = 1f - ((animProgress - 0.75f) / 0.25f);
            }

            // 1. Draw all particles
            for (Particle p : particles) {
                particlePaint.setColor(p.color);
                int alpha = (int) (255 * globalAlpha);
                if (alpha <= 0) continue;
                particlePaint.setAlpha(alpha);

                canvas.save();
                canvas.translate(p.x, p.y);
                canvas.rotate(p.rotation);

                if (p.type == 0) {
                    // Rectangular tumbling confetti ribbon
                    float scaleY = (float) Math.cos(Math.toRadians(p.flutterAngle));
                    canvas.scale(1f, scaleY);
                    canvas.drawRect(-p.size, -p.size * 0.5f, p.size, p.size * 0.5f, particlePaint);
                } else if (p.type == 1) {
                    // Circular firework spark
                    canvas.drawCircle(0, 0, p.size * 0.4f, particlePaint);
                } else {
                    // Star sparkle
                    drawStar(canvas, p.size * 0.6f, particlePaint);
                }
                canvas.restore();
            }

            // 2. Draw Celebratory Badge if message is provided
            if (message != null && !message.isEmpty()) {
                drawCelebrationBadge(canvas, globalAlpha);
            }
        }

        private void drawStar(Canvas canvas, float radius, Paint paint) {
            starPath.reset();
            float inner = radius * 0.35f;
            for (int i = 0; i < 4; i++) {
                double outerAngle = Math.PI * i / 2.0;
                double innerAngle = outerAngle + Math.PI / 4.0;
                if (i == 0) {
                    starPath.moveTo((float) (Math.cos(outerAngle) * radius), (float) (Math.sin(outerAngle) * radius));
                } else {
                    starPath.lineTo((float) (Math.cos(outerAngle) * radius), (float) (Math.sin(outerAngle) * radius));
                }
                starPath.lineTo((float) (Math.cos(innerAngle) * inner), (float) (Math.sin(innerAngle) * inner));
            }
            starPath.close();
            canvas.drawPath(starPath, paint);
        }

        private void drawCelebrationBadge(Canvas canvas, float globalAlpha) {
            float badgeScale;
            float badgeAlpha;

            // Bounce in over first 15% of animation
            if (animProgress < 0.15f) {
                float t = animProgress / 0.15f;
                // Overshoot interpolation
                badgeScale = 0.4f + 0.75f * (float) (Math.sin(t * Math.PI * 0.6f) * 1.15f);
                badgeAlpha = t;
            } else if (animProgress > 0.75f) {
                badgeScale = 1f;
                badgeAlpha = globalAlpha;
            } else {
                badgeScale = 1f;
                badgeAlpha = 1f;
            }

            float textWidth = textPaint.measureText(message);
            float padX = dpToPx(24);
            float padY = dpToPx(12);
            float badgeWidth = textWidth + padX * 2;
            float badgeHeight = spToPx(15) + padY * 2;

            float centerX = getWidth() * 0.5f;
            float centerY = getHeight() * 0.28f - (animProgress * dpToPx(15)); // Gentle float upward

            badgeRect.set(
                    centerX - badgeWidth * 0.5f,
                    centerY - badgeHeight * 0.5f,
                    centerX + badgeWidth * 0.5f,
                    centerY + badgeHeight * 0.5f
            );

            canvas.save();
            canvas.scale(badgeScale, badgeScale, centerX, centerY);

            badgeBgPaint.setAlpha((int) (240 * badgeAlpha));
            badgeStrokePaint.setAlpha((int) (180 * badgeAlpha));
            textPaint.setAlpha((int) (255 * badgeAlpha));

            float cornerRadius = badgeHeight * 0.5f;
            canvas.drawRoundRect(badgeRect, cornerRadius, cornerRadius, badgeBgPaint);
            canvas.drawRoundRect(badgeRect, cornerRadius, cornerRadius, badgeStrokePaint);

            // Center text vertically
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float textY = centerY - (fm.ascent + fm.descent) * 0.5f;
            canvas.drawText(message, centerX, textY, textPaint);

            canvas.restore();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }

        private float dpToPx(float dp) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        }

        private float spToPx(float sp) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, dm);
        }

        private static class Particle {
            float x, y;
            float vx, vy;
            float gravity;
            float drag;
            int color;
            int type;
            float size;
            float rotation;
            float rotationSpeed;
            float flutterAngle;
            float flutterSpeed;
        }
    }
}
