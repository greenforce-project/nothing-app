package com.fadlyas07.donothing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public final class MainActivity extends Activity {

    private static final String PREFERENCES_NAME =
        "nothing_statistics";

    private static final String KEY_BEST_TIME =
        "best_time_millis";

    private static final String KEY_ATTEMPTS =
        "attempt_count";

    private static final long TIMER_UPDATE_INTERVAL = 16L;

    private final Handler timerHandler =
        new Handler(Looper.getMainLooper());

    private final Random random = new Random();

    private View rootView;
    private TextView timerText;
    private TextView statusText;
    private TextView bestText;
    private TextView attemptsText;
    private TextView startButton;
    private TextView shareButton;
    private TextView resetButton;

    private SharedPreferences preferences;

    private String[] failureMessages;

    private boolean challengeRunning = false;

    private long challengeStartedAt = 0L;
    private long bestTimeMillis = 0L;
    private long attemptCount = 0L;

    private final Runnable timerRunnable =
        new Runnable() {
            @Override
            public void run() {
                if (!challengeRunning) {
                    return;
                }

                long elapsed =
                    SystemClock.elapsedRealtime()
                        - challengeStartedAt;

                timerText.setText(
                    formatDuration(elapsed)
                );

                timerHandler.postDelayed(
                    this,
                    TIMER_UPDATE_INTERVAL
                );
            }
        };

    @Override
    protected void onCreate(
        Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        configureSystemBars();
        setContentView(R.layout.activity_main);

        bindViews();
        restoreStatistics();
        configureActions();
        renderIdleState();
    }

    @Override
    public boolean dispatchTouchEvent(
        MotionEvent event
    ) {
        if (
            challengeRunning
                && event.getActionMasked()
                    == MotionEvent.ACTION_DOWN
        ) {
            finishChallenge(null);
            return true;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onPause() {
        if (challengeRunning) {
            finishChallenge(
                getString(
                    R.string.left_app_message
                )
            );
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timerHandler.removeCallbacks(
            timerRunnable
        );

        super.onDestroy();
    }

    private void bindViews() {
        rootView =
            findViewById(R.id.root_view);

        timerText =
            findViewById(R.id.timer_text);

        statusText =
            findViewById(R.id.status_text);

        bestText =
            findViewById(R.id.best_text);

        attemptsText =
            findViewById(R.id.attempts_text);

        startButton =
            findViewById(R.id.start_button);

        shareButton =
            findViewById(R.id.share_button);

        resetButton =
            findViewById(R.id.reset_button);

        failureMessages =
            getResources().getStringArray(
                R.array.failure_messages
            );
    }

    private void configureActions() {
        startButton.setOnClickListener(
            view -> startChallenge()
        );

        shareButton.setOnClickListener(
            view -> shareBestResult()
        );

        resetButton.setOnClickListener(
            view -> showResetConfirmation()
        );
    }

    private void startChallenge() {
        challengeRunning = true;

        challengeStartedAt =
            SystemClock.elapsedRealtime();

        timerText.setText(
            formatDuration(0L)
        );

        statusText.setText(
            R.string.running_message
        );

        startButton.setVisibility(
            View.GONE
        );

        shareButton.setVisibility(
            View.GONE
        );

        resetButton.setVisibility(
            View.GONE
        );

        getWindow().addFlags(
            WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON
        );

        timerHandler.removeCallbacks(
            timerRunnable
        );

        timerHandler.post(
            timerRunnable
        );
    }

    private void finishChallenge(
        String forcedMessage
    ) {
        if (!challengeRunning) {
            return;
        }

        long elapsed =
            SystemClock.elapsedRealtime()
                - challengeStartedAt;

        challengeRunning = false;

        timerHandler.removeCallbacks(
            timerRunnable
        );

        getWindow().clearFlags(
            WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON
        );

        attemptCount += 1L;

        boolean newBest =
            elapsed > bestTimeMillis;

        if (newBest) {
            bestTimeMillis = elapsed;
        }

        saveStatistics();

        timerText.setText(
            formatDuration(elapsed)
        );

        if (forcedMessage != null) {
            statusText.setText(
                forcedMessage
            );
        } else if (newBest) {
            statusText.setText(
                R.string.new_best_message
            );
        } else {
            statusText.setText(
                randomFailureMessage()
            );
        }

        rootView.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS
        );

        renderStatistics();

        startButton.setText(
            R.string.try_again
        );

        startButton.setVisibility(
            View.VISIBLE
        );

        shareButton.setVisibility(
            View.VISIBLE
        );

        resetButton.setVisibility(
            View.VISIBLE
        );
    }

    private void renderIdleState() {
        timerText.setText(
            formatDuration(0L)
        );

        statusText.setText(
            R.string.initial_message
        );

        startButton.setText(
            R.string.start_challenge
        );

        startButton.setVisibility(
            View.VISIBLE
        );

        shareButton.setVisibility(
            View.VISIBLE
        );

        resetButton.setVisibility(
            View.VISIBLE
        );

        renderStatistics();
    }

    private void renderStatistics() {
        bestText.setText(
            getString(
                R.string.best_format,
                formatDuration(bestTimeMillis)
            )
        );

        attemptsText.setText(
            getString(
                R.string.attempts_format,
                attemptCount
            )
        );
    }

    private void restoreStatistics() {
        preferences =
            getSharedPreferences(
                PREFERENCES_NAME,
                MODE_PRIVATE
            );

        bestTimeMillis =
            preferences.getLong(
                KEY_BEST_TIME,
                0L
            );

        attemptCount =
            preferences.getLong(
                KEY_ATTEMPTS,
                0L
            );
    }

    private void saveStatistics() {
        preferences
            .edit()
            .putLong(
                KEY_BEST_TIME,
                bestTimeMillis
            )
            .putLong(
                KEY_ATTEMPTS,
                attemptCount
            )
            .apply();
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle(
                R.string.reset_dialog_title
            )
            .setMessage(
                R.string.reset_dialog_message
            )
            .setNegativeButton(
                R.string.cancel,
                null
            )
            .setPositiveButton(
                R.string.reset,
                (dialog, which) -> {
                    bestTimeMillis = 0L;
                    attemptCount = 0L;

                    saveStatistics();
                    renderIdleState();

                    statusText.setText(
                        R.string.statistics_reset
                    );
                }
            )
            .show();
    }

    private void shareBestResult() {
        String shareText =
            getString(
                R.string.share_message,
                formatDuration(bestTimeMillis),
                attemptCount
            );

        Intent shareIntent =
            new Intent(
                Intent.ACTION_SEND
            );

        shareIntent.setType(
            "text/plain"
        );

        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            shareText
        );

        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(
                    R.string.share_title
                )
            )
        );
    }

    private String randomFailureMessage() {
        if (failureMessages.length == 0) {
            return getString(
                R.string.default_failure_message
            );
        }

        return failureMessages[
            random.nextInt(
                failureMessages.length
            )
        ];
    }

    private String formatDuration(
        long milliseconds
    ) {
        long safeMilliseconds =
            Math.max(0L, milliseconds);

        long minutes =
            safeMilliseconds / 60_000L;

        long seconds =
            (
                safeMilliseconds
                    % 60_000L
            ) / 1_000L;

        long millis =
            safeMilliseconds % 1_000L;

        return String.format(
            Locale.US,
            "%02d:%02d.%03d",
            minutes,
            seconds,
            millis
        );
    }

    private void configureSystemBars() {
        Window window = getWindow();

        boolean darkMode =
            (
                getResources()
                    .getConfiguration()
                    .uiMode
                    & Configuration
                        .UI_MODE_NIGHT_MASK
            )
                == Configuration
                    .UI_MODE_NIGHT_YES;

        int background =
            darkMode
                ? Color.BLACK
                : Color.WHITE;

        window.setStatusBarColor(
            background
        );

        window.setNavigationBarColor(
            background
        );

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
            .setSystemUiVisibility(
                visibility
            );

        if (
            Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.Q
        ) {
            window
                .setNavigationBarContrastEnforced(
                    false
                );
        }
    }
}
