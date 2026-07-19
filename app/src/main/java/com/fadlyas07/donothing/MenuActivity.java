package com.fadlyas07.donothing;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public final class MenuActivity extends LocaleActivity {

    private TextView indonesianButton;
    private TextView englishButton;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        configureSystemBars();
        setContentView(R.layout.activity_menu);

        bindViews();
        configureNavigation();
        configureLanguageSwitch();
        renderLanguageSwitch();
    }

    private void bindViews() {
        indonesianButton =
                findViewById(
                        R.id.language_indonesian_button
                );

        englishButton =
                findViewById(
                        R.id.language_english_button
                );
    }

    private void configureNavigation() {
        findViewById(
                R.id.do_nothing_button
        ).setOnClickListener(
                view -> startActivity(
                        new Intent(
                                this,
                                MainActivity.class
                        )
                )
        );

        findViewById(
                R.id.escape_nothing_button
        ).setOnClickListener(
                view -> startActivity(
                        new Intent(
                                this,
                                MazeActivity.class
                        )
                )
        );
    }

    private void configureLanguageSwitch() {
        indonesianButton.setOnClickListener(
                view -> changeLanguage(
                        AppLocale.LANGUAGE_INDONESIAN
                )
        );

        englishButton.setOnClickListener(
                view -> changeLanguage(
                        AppLocale.LANGUAGE_ENGLISH
                )
        );
    }

    private void changeLanguage(
            String language
    ) {
        String currentLanguage =
                AppLocale.getLanguage(this);

        if (language.equals(currentLanguage)) {
            return;
        }

        AppLocale.setLanguage(
                this,
                language
        );

        recreate();

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }

    private void renderLanguageSwitch() {
        String currentLanguage =
                AppLocale.getLanguage(this);

        boolean indonesianSelected =
                AppLocale.LANGUAGE_INDONESIAN.equals(
                        currentLanguage
                );

        styleLanguageButton(
                indonesianButton,
                indonesianSelected
        );

        styleLanguageButton(
                englishButton,
                !indonesianSelected
        );
    }

    private void styleLanguageButton(
            TextView button,
            boolean selected
    ) {
        button.setBackgroundResource(
                selected
                        ? R.drawable.language_option_selected
                        : R.drawable.language_option_unselected
        );

        button.setTextColor(
                getResources().getColor(
                        selected
                                ? R.color.nothing_background
                                : R.color.nothing_foreground
                )
        );

        button.setSelected(selected);
    }

    private void configureSystemBars() {
        Window window = getWindow();

        boolean darkMode = (
                getResources()
                        .getConfiguration()
                        .uiMode
                        & Configuration.UI_MODE_NIGHT_MASK
        ) == Configuration.UI_MODE_NIGHT_YES;

        int background =
                darkMode
                        ? Color.BLACK
                        : Color.WHITE;

        window.setStatusBarColor(background);
        window.setNavigationBarColor(background);

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
            window.setNavigationBarContrastEnforced(
                    false
            );
        }
    }
}
