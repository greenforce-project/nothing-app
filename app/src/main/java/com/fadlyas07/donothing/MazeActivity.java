package com.fadlyas07.donothing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

public final class MazeActivity extends Activity {

    private static final String PREFERENCES_NAME =
            "nothing_maze_statistics";

    private static final String KEY_BEST_TIME =
            "maze_best_time_millis";

    private static final String KEY_BEST_MOVES =
            "maze_best_moves";

    private static final long TIMER_UPDATE_INTERVAL =
            16L;

    private static final int MAZE_ROWS = 11;
    private static final int MAZE_COLUMNS = 11;

    private final Handler timerHandler =
            new Handler(Looper.getMainLooper());

    private MazeView mazeView;

    private TextView timerText;
    private TextView movesText;
    private TextView bestText;
    private TextView statusText;
    private TextView restartButton;

    private SharedPreferences preferences;

    private long startedAt;
    private long finalElapsed;
    private long bestTime;

    private int bestMoves;

    private boolean running;

    private final Runnable timerRunnable =
            new Runnable() {

                @Override
                public void run() {
                    if (!running) {
                        return;
                    }

                    timerText.setText(
                            formatDuration(
                                    getElapsedTime()
                            )
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

        setContentView(R.layout.activity_maze);

        bindViews();
        restoreStatistics();
        configureMazeCallbacks();
        configureActions();
        renderBest();

        mazeView.post(this::startNewGame);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (running) {
            timerHandler.removeCallbacks(
                    timerRunnable
            );

            timerHandler.post(
                    timerRunnable
            );
        }
    }

    @Override
    protected void onPause() {
        timerHandler.removeCallbacks(
                timerRunnable
        );

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timerHandler.removeCallbacks(
                timerRunnable
        );

        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        super.onDestroy();
    }

    private void bindViews() {
        mazeView =
                findViewById(R.id.maze_view);

        timerText =
                findViewById(R.id.maze_timer_text);

        movesText =
                findViewById(R.id.maze_moves_text);

        bestText =
                findViewById(R.id.maze_best_text);

        statusText =
                findViewById(R.id.maze_status_text);

        restartButton =
                findViewById(R.id.restart_maze_button);
    }

    private void configureMazeCallbacks() {
        mazeView.setMazeListener(
                new MazeView.MazeListener() {

                    @Override
                    public void onMove(int moves) {
                        movesText.setText(
                                getString(
                                        R.string.maze_moves_format,
                                        moves
                                )
                        );

                        statusText.setText(
                                R.string.maze_running_message
                        );
                    }

                    @Override
                    public void onWallHit() {
                        statusText.setText(
                                R.string.maze_wall_message
                        );
                    }

                    @Override
                    public void onSolved(int moves) {
                        finishGame(moves);
                    }
                }
        );
    }

    private void configureActions() {
        restartButton.setOnClickListener(
                view -> startNewGame()
        );

        findViewById(R.id.return_button)
                .setOnClickListener(
                        view -> finish()
                );
    }

    private void startNewGame() {
        mazeView.newMaze(
                MAZE_ROWS,
                MAZE_COLUMNS
        );

        startedAt =
                SystemClock.elapsedRealtime();

        finalElapsed = 0L;
        running = true;

        timerText.setText(
                formatDuration(0L)
        );

        movesText.setText(
                getString(
                        R.string.maze_moves_format,
                        0
                )
        );

        statusText.setText(
                R.string.maze_initial_message
        );

        restartButton.setText(
                R.string.maze_new_maze
        );

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        timerHandler.removeCallbacks(
                timerRunnable
        );

        timerHandler.post(
                timerRunnable
        );
    }

    private void finishGame(int moves) {
        if (!running) {
            return;
        }

        finalElapsed = getElapsedTime();
        running = false;

        timerHandler.removeCallbacks(
                timerRunnable
        );

        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        boolean newBestTime =
                bestTime == 0L
                        || finalElapsed < bestTime;

        boolean newBestMoves =
                bestMoves == 0
                        || moves < bestMoves;

        if (newBestTime) {
            bestTime = finalElapsed;
        }

        if (newBestMoves) {
            bestMoves = moves;
        }

        saveStatistics();
        renderBest();

        timerText.setText(
                formatDuration(finalElapsed)
        );

        statusText.setText(
                newBestTime || newBestMoves
                        ? R.string.maze_new_best_message
                        : R.string.maze_finished_message
        );

        restartButton.setText(
                R.string.maze_try_again
        );
    }

    private long getElapsedTime() {
        if (!running) {
            return finalElapsed;
        }

        return Math.max(
                0L,
                SystemClock.elapsedRealtime()
                        - startedAt
        );
    }

    private void restoreStatistics() {
        preferences = getSharedPreferences(
                PREFERENCES_NAME,
                MODE_PRIVATE
        );

        bestTime = preferences.getLong(
                KEY_BEST_TIME,
                0L
        );

        bestMoves = preferences.getInt(
                KEY_BEST_MOVES,
                0
        );
    }

    private void saveStatistics() {
        preferences
                .edit()
                .putLong(
                        KEY_BEST_TIME,
                        bestTime
                )
                .putInt(
                        KEY_BEST_MOVES,
                        bestMoves
                )
                .apply();
    }

    private void renderBest() {
        String bestTimeText =
                bestTime == 0L
                        ? "--:--.---"
                        : formatDuration(bestTime);

        String bestMovesText =
                bestMoves == 0
                        ? "--"
                        : String.valueOf(bestMoves);

        bestText.setText(
                getString(
                        R.string.maze_best_format,
                        bestTimeText,
                        bestMovesText
                )
        );
    }

    private String formatDuration(
            long milliseconds
    ) {
        long safeMilliseconds =
                Math.max(
                        0L,
                        milliseconds
                );

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
