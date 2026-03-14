package com.example.gamerecurrection.game_engine.objects;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class GameObjectManager {

    private final List<GameObject> objects = new ArrayList<>();
    private SpatialGrid grid;
    private ObjectEventListener listener;

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
        handleDestructions();
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

    private void handleDestructions() {
        List<GameObject> toRemove = new ArrayList<>();

        for (GameObject obj : objects) {
            if (obj.isDestroyed()) {
                if (listener != null) {
                    listener.onObjectDestroyed(obj);
                }
                toRemove.add(obj);
            }
        }

        objects.removeAll(toRemove);
    }

    public void drawAll(Canvas canvas, int cameraX, int cameraY) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).draw(canvas, cameraX, cameraY);
        }
    }

    public void setEventListener(ObjectEventListener listener) {
        this.listener = listener;
    }

    public List<GameObject>GetObjects () {
        return objects;
    }
}