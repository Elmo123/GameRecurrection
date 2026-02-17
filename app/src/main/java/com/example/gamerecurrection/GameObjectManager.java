package com.example.gamerecurrection;

import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

public class GameObjectManager {

    private final List<GameObject> objects = new ArrayList<>();
    private SpatialGrid grid;

    public void initSpatialGrid (int worldWidth, int worldHeight, int cellSize) {
        grid = new SpatialGrid(worldWidth, worldHeight, cellSize); // or TILE_SIZE
    }
    public void add(GameObject obj) {
        objects.add(obj);
    }

    public void updateAll() {
        grid.clear();

        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            obj.update();
            grid.insert(obj);
        }

        handleCollisions();
    }

    private void handleCollisions() {
        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);

            List<GameObject> nearby = grid.getNearby(obj);

            for (GameObject other : nearby) {
                if (obj == other) continue;

                if (obj.intersects(other)) {
                    obj.onCollision(other);
                    other.onCollision(obj);
                }
            }
        }
    }

    public void drawAll(Canvas canvas, int cameraX, int cameraY) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).draw(canvas, cameraX, cameraY);
        }
    }
}