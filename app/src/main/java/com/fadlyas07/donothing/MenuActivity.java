package com.fadlyas07.donothing;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public final class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureSystemBars();
        setContentView(R.layout.activity_menu);

        findViewById(R.id.do_nothing_button).setOnClickListener(
                view -> startActivity(
                        new Intent(this, MainActivity.class)
                )
        );

        findViewById(R.id.escape_nothing_button).setOnClickListener(
                view -> startActivity(
                        new Intent(this, MazeActivity.class)
                )
        );
    }

    private void configureSystemBars() {
        Window window = getWindow();

        boolean darkMode = (
                getResources()
                        .getConfiguration()
                        .uiMode
                        & Configuration.UI_MODE_NIGHT_MASK
        ) == Configuration.UI_MODE_NIGHT_YES;

        int background = darkMode
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
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        if (
                !darkMode
                        && Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.O
        ) {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        window.getDecorView().setSystemUiVisibility(visibility);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
        }
    }
}
