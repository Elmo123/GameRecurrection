package com.example.gamerecurrection;

import android.graphics.Canvas;

public abstract class GameObject {

    protected float x, y;
    protected int width = 32;
    protected int height = 32;


    public GameObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update();
    public abstract void draw(Canvas canvas, int cameraX, int cameraY);

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean intersects(GameObject other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    public void onCollision(GameObject other) {
        // default: do nothing
    }
}