package com.fadlyas07.donothing;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

public final class SplashActivity extends Activity {

    private static final long DOT_DURATION = 380L;
    private static final long TITLE_DELAY = 230L;
    private static final long TITLE_DURATION = 430L;
    private static final long SUBTITLE_DELAY = 520L;
    private static final long SUBTITLE_DURATION = 350L;
    private static final long OPEN_MENU_DELAY = 1_250L;
    private static final long EXIT_DURATION = 280L;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private View splashRoot;
    private View splashContent;
    private View splashDot;
    private TextView splashTitle;
    private TextView splashSubtitle;

    private boolean openingMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureSystemBars();
        setContentView(R.layout.activity_splash);

        bindViews();
        prepareInitialState();
        startIntroAnimation();
    }

    private void bindViews() {
        splashRoot =
                findViewById(R.id.splash_root);

        splashContent =
                findViewById(R.id.splash_content);

        splashDot =
                findViewById(R.id.splash_dot);

        splashTitle =
                findViewById(R.id.splash_title);

        splashSubtitle =
                findViewById(R.id.splash_subtitle);
    }

    private void prepareInitialState() {
        splashContent.setAlpha(1f);
        splashContent.setScaleX(1f);
        splashContent.setScaleY(1f);

        splashDot.setAlpha(0f);
        splashDot.setScaleX(0.15f);
        splashDot.setScaleY(0.15f);

        splashTitle.setAlpha(0f);
        splashTitle.setTranslationY(dpToPx(16f));

        splashSubtitle.setAlpha(0f);
        splashSubtitle.setTranslationY(dpToPx(10f));
    }

    private void startIntroAnimation() {
        splashDot
                .animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(DOT_DURATION)
                .setInterpolator(
                        new OvershootInterpolator(1.6f)
                )
                .start();

        splashTitle
                .animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(TITLE_DELAY)
                .setDuration(TITLE_DURATION)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();

        splashSubtitle
                .animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(SUBTITLE_DELAY)
                .setDuration(SUBTITLE_DURATION)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();

        handler.postDelayed(
                this::startExitAnimation,
                OPEN_MENU_DELAY
        );
    }

    private void startExitAnimation() {
        if (openingMenu) {
            return;
        }

        openingMenu = true;

        splashContent
                .animate()
                .alpha(0f)
                .scaleX(0.94f)
                .scaleY(0.94f)
                .translationY(-dpToPx(8f))
                .setDuration(EXIT_DURATION)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .withEndAction(this::openMenu)
                .start();
    }

    private void openMenu() {
        Intent intent =
                new Intent(
                        SplashActivity.this,
                        MenuActivity.class
                );

        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();
    }

    private float dpToPx(float dp) {
        return dp
                * getResources()
                .getDisplayMetrics()
                .density;
    }

    private void configureSystemBars() {
        Window window = getWindow();

        boolean darkMode = (
                getResources()
                        .getConfiguration()
                        .uiMode
                        & Configuration.UI_MODE_NIGHT_MASK
        ) == Configuration.UI_MODE_NIGHT_YES;

        int backgroundColor =
                darkMode
                        ? Color.BLACK
                        : Color.WHITE;

        window.setStatusBarColor(backgroundColor);
        window.setNavigationBarColor(backgroundColor);

        int visibility = 0;

        if (
                !darkMode
                        && Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.M
        ) {
            visibility |=
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        if (
                !darkMode
                        && Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.O
        ) {
            visibility |=
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        window
                .getDecorView()
                .setSystemUiVisibility(visibility);

        if (
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.Q
        ) {
            window.setNavigationBarContrastEnforced(false);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (splashDot != null) {
            splashDot.animate().cancel();
        }

        if (splashTitle != null) {
            splashTitle.animate().cancel();
        }

        if (splashSubtitle != null) {
            splashSubtitle.animate().cancel();
        }

        if (splashContent != null) {
            splashContent.animate().cancel();
        }

        super.onDestroy();
    }
}
