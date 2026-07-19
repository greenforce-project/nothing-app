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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

public final class SplashActivity extends LocaleActivity {

    /*
     * Total splash duration is approximately 3 seconds:
     *
     * 0 ms     : dot begins to appear
     * 350 ms   : title begins to appear
     * 850 ms   : subtitle begins to appear
     * 1.300 ms : developer credit begins to appear
     * 1.850 ms : dot pulses
     * 2.700 ms : exit animation begins
     * 3.150 ms : open the menu
     */

    private static final long DOT_DURATION = 550L;

    private static final long TITLE_DELAY = 350L;
    private static final long TITLE_DURATION = 650L;

    private static final long SUBTITLE_DELAY = 850L;
    private static final long SUBTITLE_DURATION = 500L;

    private static final long CREDIT_DELAY = 1_300L;
    private static final long CREDIT_DURATION = 550L;

    private static final long DOT_PULSE_DELAY = 1_850L;

    private static final long OPEN_MENU_DELAY = 2_700L;
    private static final long EXIT_DURATION = 450L;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private View splashRoot;
    private View splashContent;
    private View splashDot;

    private TextView splashTitle;
    private TextView splashSubtitle;
    private TextView splashCredit;

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

        splashCredit =
                findViewById(R.id.splash_credit);
    }

    private void prepareInitialState() {
        splashRoot.setAlpha(1f);

        splashContent.setAlpha(1f);
        splashContent.setScaleX(0.98f);
        splashContent.setScaleY(0.98f);

        splashDot.setAlpha(0f);
        splashDot.setScaleX(0.10f);
        splashDot.setScaleY(0.10f);
        splashDot.setRotation(-45f);

        splashTitle.setAlpha(0f);
        splashTitle.setTranslationY(dpToPx(24f));
        splashTitle.setScaleX(0.94f);
        splashTitle.setScaleY(0.94f);

        splashSubtitle.setAlpha(0f);
        splashSubtitle.setTranslationY(dpToPx(16f));

        splashCredit.setAlpha(0f);
        splashCredit.setTranslationY(dpToPx(12f));
    }

    private void startIntroAnimation() {
        splashContent
                .animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1_000L)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();

        splashDot
                .animate()
                .alpha(1f)
                .scaleX(1.15f)
                .scaleY(1.15f)
                .rotation(0f)
                .setDuration(DOT_DURATION)
                .setInterpolator(
                        new OvershootInterpolator(1.8f)
                )
                .withEndAction(
                        () -> splashDot
                                .animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(180L)
                                .setInterpolator(
                                        new DecelerateInterpolator()
                                )
                                .start()
                )
                .start();

        splashTitle
                .animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
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

        splashCredit
                .animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(CREDIT_DELAY)
                .setDuration(CREDIT_DURATION)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();

        handler.postDelayed(
                this::pulseDot,
                DOT_PULSE_DELAY
        );

        handler.postDelayed(
                this::startExitAnimation,
                OPEN_MENU_DELAY
        );
    }

    private void pulseDot() {
        if (openingMenu) {
            return;
        }

        splashDot
                .animate()
                .scaleX(1.45f)
                .scaleY(1.45f)
                .setDuration(220L)
                .setInterpolator(
                        new AccelerateDecelerateInterpolator()
                )
                .withEndAction(
                        () -> splashDot
                                .animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(280L)
                                .setInterpolator(
                                        new OvershootInterpolator(1.2f)
                                )
                                .start()
                )
                .start();
    }

    private void startExitAnimation() {
        if (openingMenu) {
            return;
        }

        openingMenu = true;

        splashContent
                .animate()
                .alpha(0f)
                .scaleX(0.90f)
                .scaleY(0.90f)
                .translationY(-dpToPx(14f))
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

        cancelAnimation(splashRoot);
        cancelAnimation(splashContent);
        cancelAnimation(splashDot);
        cancelAnimation(splashTitle);
        cancelAnimation(splashSubtitle);
        cancelAnimation(splashCredit);

        super.onDestroy();
    }

    private void cancelAnimation(View view) {
        if (view != null) {
            view.animate().cancel();
        }
    }
}
