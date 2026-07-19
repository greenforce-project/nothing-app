package com.fadlyas07.donothing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public final class MazeView extends View {

    public interface MazeListener {

        void onMove(int moves);

        void onWallHit();

        void onSolved(int moves);
    }

    private static final int DEFAULT_ROWS = 11;
    private static final int DEFAULT_COLUMNS = 11;

    private final Paint wallPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint playerPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint exitPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final MazeGenerator generator =
            new MazeGenerator();

    private MazeCell[][] cells;
    private MazeListener listener;

    private int rows = DEFAULT_ROWS;
    private int columns = DEFAULT_COLUMNS;

    private int playerRow = 0;
    private int playerColumn = 0;

    private int moves = 0;

    private boolean solved = false;

    private float touchDownX;
    private float touchDownY;
    private float swipeThreshold;

    public MazeView(Context context) {
        super(context);
        initialise();
    }

    public MazeView(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);
        initialise();
    }

    public MazeView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    private void initialise() {
        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        swipeThreshold = 24f * density;

        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(2f * density);
        wallPaint.setStrokeCap(Paint.Cap.SQUARE);
        wallPaint.setColor(
                getResources().getColor(
                        R.color.nothing_foreground
                )
        );

        playerPaint.setStyle(Paint.Style.FILL);
        playerPaint.setColor(
                getResources().getColor(
                        R.color.nothing_foreground
                )
        );

        exitPaint.setStyle(Paint.Style.STROKE);
        exitPaint.setStrokeWidth(2f * density);
        exitPaint.setColor(
                getResources().getColor(
                        R.color.nothing_muted
                )
        );

        setClickable(true);

        newMaze(
                DEFAULT_ROWS,
                DEFAULT_COLUMNS
        );
    }

    public void setMazeListener(
            MazeListener listener
    ) {
        this.listener = listener;
    }

    public void newMaze(
            int rows,
            int columns
    ) {
        this.rows = rows;
        this.columns = columns;

        cells = generator.generate(
                rows,
                columns
        );

        playerRow = 0;
        playerColumn = 0;
        moves = 0;
        solved = false;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (
                cells == null
                        || getWidth() == 0
                        || getHeight() == 0
        ) {
            return;
        }

        float contentWidth =
                getWidth()
                        - getPaddingLeft()
                        - getPaddingRight();

        float contentHeight =
                getHeight()
                        - getPaddingTop()
                        - getPaddingBottom();

        float cellSize = Math.min(
                contentWidth / columns,
                contentHeight / rows
        );

        float mazeWidth = cellSize * columns;
        float mazeHeight = cellSize * rows;

        float startX =
                getPaddingLeft()
                        + (contentWidth - mazeWidth) / 2f;

        float startY =
                getPaddingTop()
                        + (contentHeight - mazeHeight) / 2f;

        drawExit(
                canvas,
                startX,
                startY,
                cellSize
        );

        drawWalls(
                canvas,
                startX,
                startY,
                cellSize
        );

        drawPlayer(
                canvas,
                startX,
                startY,
                cellSize
        );
    }

    private void drawWalls(
            Canvas canvas,
            float startX,
            float startY,
            float cellSize
    ) {
        for (int row = 0; row < rows; row++) {
            for (
                    int column = 0;
                    column < columns;
                    column++
            ) {
                MazeCell cell =
                        cells[row][column];

                float left =
                        startX + column * cellSize;

                float top =
                        startY + row * cellSize;

                float right =
                        left + cellSize;

                float bottom =
                        top + cellSize;

                if (cell.topWall) {
                    canvas.drawLine(
                            left,
                            top,
                            right,
                            top,
                            wallPaint
                    );
                }

                if (cell.rightWall) {
                    canvas.drawLine(
                            right,
                            top,
                            right,
                            bottom,
                            wallPaint
                    );
                }

                if (cell.bottomWall) {
                    canvas.drawLine(
                            left,
                            bottom,
                            right,
                            bottom,
                            wallPaint
                    );
                }

                if (cell.leftWall) {
                    canvas.drawLine(
                            left,
                            top,
                            left,
                            bottom,
                            wallPaint
                    );
                }
            }
        }
    }

    private void drawPlayer(
            Canvas canvas,
            float startX,
            float startY,
            float cellSize
    ) {
        float centerX =
                startX
                        + (playerColumn + 0.5f)
                        * cellSize;

        float centerY =
                startY
                        + (playerRow + 0.5f)
                        * cellSize;

        canvas.drawCircle(
                centerX,
                centerY,
                cellSize * 0.22f,
                playerPaint
        );
    }

    private void drawExit(
            Canvas canvas,
            float startX,
            float startY,
            float cellSize
    ) {
        float margin = cellSize * 0.24f;

        float left =
                startX
                        + (columns - 1)
                        * cellSize
                        + margin;

        float top =
                startY
                        + (rows - 1)
                        * cellSize
                        + margin;

        float right =
                startX
                        + columns
                        * cellSize
                        - margin;

        float bottom =
                startY
                        + rows
                        * cellSize
                        - margin;

        canvas.drawRect(
                new RectF(
                        left,
                        top,
                        right,
                        bottom
                ),
                exitPaint
        );
    }

    @Override
    public boolean onTouchEvent(
            MotionEvent event
    ) {
        if (solved) {
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                performClick();

                handleSwipe(
                        event.getX() - touchDownX,
                        event.getY() - touchDownY
                );

                return true;

            default:
                return true;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void handleSwipe(
            float deltaX,
            float deltaY
    ) {
        if (
                Math.abs(deltaX) < swipeThreshold
                        && Math.abs(deltaY) < swipeThreshold
        ) {
            return;
        }

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            attemptMove(
                    deltaX > 0
                            ? Direction.RIGHT
                            : Direction.LEFT
            );
        } else {
            attemptMove(
                    deltaY > 0
                            ? Direction.DOWN
                            : Direction.UP
            );
        }
    }

    private void attemptMove(
            Direction direction
    ) {
        MazeCell current =
                cells[playerRow][playerColumn];

        int nextRow = playerRow;
        int nextColumn = playerColumn;

        boolean blocked;

        switch (direction) {
            case UP:
                blocked = current.topWall;
                nextRow--;
                break;

            case RIGHT:
                blocked = current.rightWall;
                nextColumn++;
                break;

            case DOWN:
                blocked = current.bottomWall;
                nextRow++;
                break;

            case LEFT:
                blocked = current.leftWall;
                nextColumn--;
                break;

            default:
                return;
        }

        if (
                blocked
                        || nextRow < 0
                        || nextRow >= rows
                        || nextColumn < 0
                        || nextColumn >= columns
        ) {
            performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP
            );

            if (listener != null) {
                listener.onWallHit();
            }

            return;
        }

        playerRow = nextRow;
        playerColumn = nextColumn;
        moves++;

        invalidate();

        if (listener != null) {
            listener.onMove(moves);
        }

        if (
                playerRow == rows - 1
                        && playerColumn == columns - 1
        ) {
            solved = true;

            performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS
            );

            if (listener != null) {
                listener.onSolved(moves);
            }
        }
    }

    private enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}
