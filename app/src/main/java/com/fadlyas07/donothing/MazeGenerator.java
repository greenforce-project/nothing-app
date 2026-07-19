package com.fadlyas07.donothing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

final class MazeGenerator {

    private final Random random = new Random();

    MazeCell[][] generate(int rows, int columns) {
        if (rows < 2 || columns < 2) {
            throw new IllegalArgumentException(
                    "Maze must be at least 2 x 2 cells."
            );
        }

        MazeCell[][] cells = createCells(rows, columns);
        Deque<Position> stack = new ArrayDeque<>();

        Position current = new Position(0, 0);

        cells[0][0].visited = true;

        int visitedCount = 1;
        int totalCells = rows * columns;

        while (visitedCount < totalCells) {
            List<Position> neighbours = findUnvisitedNeighbours(
                    cells,
                    current.row,
                    current.column
            );

            if (!neighbours.isEmpty()) {
                Collections.shuffle(neighbours, random);

                Position next = neighbours.get(0);

                removeWallBetween(
                        cells,
                        current,
                        next
                );

                stack.push(current);

                current = next;

                cells[current.row][current.column].visited = true;

                visitedCount++;
            } else if (!stack.isEmpty()) {
                current = stack.pop();
            }
        }

        clearVisitedFlags(cells);

        return cells;
    }

    private MazeCell[][] createCells(
            int rows,
            int columns
    ) {
        MazeCell[][] cells = new MazeCell[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new MazeCell();
            }
        }

        return cells;
    }

    private List<Position> findUnvisitedNeighbours(
            MazeCell[][] cells,
            int row,
            int column
    ) {
        int rows = cells.length;
        int columns = cells[0].length;

        List<Position> neighbours = new ArrayList<>(4);

        if (
                row > 0
                        && !cells[row - 1][column].visited
        ) {
            neighbours.add(
                    new Position(row - 1, column)
            );
        }

        if (
                column < columns - 1
                        && !cells[row][column + 1].visited
        ) {
            neighbours.add(
                    new Position(row, column + 1)
            );
        }

        if (
                row < rows - 1
                        && !cells[row + 1][column].visited
        ) {
            neighbours.add(
                    new Position(row + 1, column)
            );
        }

        if (
                column > 0
                        && !cells[row][column - 1].visited
        ) {
            neighbours.add(
                    new Position(row, column - 1)
            );
        }

        return neighbours;
    }

    private void removeWallBetween(
            MazeCell[][] cells,
            Position current,
            Position next
    ) {
        int rowDifference =
                next.row - current.row;

        int columnDifference =
                next.column - current.column;

        MazeCell currentCell =
                cells[current.row][current.column];

        MazeCell nextCell =
                cells[next.row][next.column];

        if (rowDifference == -1) {
            currentCell.topWall = false;
            nextCell.bottomWall = false;
        } else if (rowDifference == 1) {
            currentCell.bottomWall = false;
            nextCell.topWall = false;
        } else if (columnDifference == -1) {
            currentCell.leftWall = false;
            nextCell.rightWall = false;
        } else if (columnDifference == 1) {
            currentCell.rightWall = false;
            nextCell.leftWall = false;
        }
    }

    private void clearVisitedFlags(
            MazeCell[][] cells
    ) {
        for (MazeCell[] row : cells) {
            for (MazeCell cell : row) {
                cell.visited = false;
            }
        }
    }

    private static final class Position {

        final int row;
        final int column;

        Position(
                int row,
                int column
        ) {
            this.row = row;
            this.column = column;
        }
    }
}
