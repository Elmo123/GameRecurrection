package com.example.gamerecurrection.game_engine.objects;

import java.util.ArrayList;
import java.util.List;

public class SpatialGrid {

    private final int cellSize;
    private final int cols;
    private final int rows;

    private final List<GameObject>[][] grid;

    @SuppressWarnings("unchecked")
    public SpatialGrid(int worldWidth, int worldHeight, int cellSize) {
        this.cellSize = cellSize;
        this.cols = (worldWidth / cellSize) + 1;
        this.rows = (worldHeight / cellSize) + 1;

        grid = new ArrayList[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x] = new ArrayList<>();
            }
        }
    }

    public void clear() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].clear();
            }
        }
    }

    public void insert(GameObject obj) {
        int cx = (int)(obj.getHitboxX() / cellSize);
        int cy = (int)(obj.getHitboxY() / cellSize);

        if (cx >= 0 && cy >= 0 && cx < cols && cy < rows) {
            grid[cy][cx].add(obj);
        }
    }

    public List<GameObject> getNearby(GameObject obj) {
        List<GameObject> result = new ArrayList<>();

        int cx = (int)(obj.getHitboxX() / cellSize);
        int cy = (int)(obj.getHitboxY() / cellSize);

        for (int y = cy - 1; y <= cy + 1; y++) {
            for (int x = cx - 1; x <= cx + 1; x++) {
                if (x >= 0 && y >= 0 && x < cols && y < rows) {
                    result.addAll(grid[y][x]);
                }
            }
        }

        return result;
    }
}