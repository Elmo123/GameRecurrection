package com.example.gamerecurrection.game_engine.objects;

import android.graphics.Canvas;

import com.example.gamerecurrection.GameObjectType;

public abstract class GameObject {

    // Sprite position (top-left)
    protected float x, y;

    // Sprite size (for drawing)
    protected int spriteWidth = 32;
    protected int spriteHeight = 32;

    // Hitbox size
    protected int hitboxWidth = 32;
    protected int hitboxHeight = 32;

    // Hitbox offset from sprite origin
    protected float hitboxOffsetX = 0;
    protected float hitboxOffsetY = 0;

    // Type
    protected ObjectType type;
    private boolean destroyed = false;

    public GameObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Hitbox getters
    public float getHitboxX() { return x + hitboxOffsetX; }
    public float getHitboxY() { return y + hitboxOffsetY; }
    public int getHitboxWidth() { return hitboxWidth; }
    public int getHitboxHeight() { return hitboxHeight; }
    public ObjectType getObjectType() { return type; }
    public void destroy() { destroyed = true; }
    public boolean isDestroyed() { return destroyed; }

    public boolean intersects(GameObject other) {
        return getHitboxX() < other.getHitboxX() + other.getHitboxWidth() &&
                getHitboxX() + getHitboxWidth() > other.getHitboxX() &&
                getHitboxY() < other.getHitboxY() + other.getHitboxHeight() &&
                getHitboxY() + getHitboxHeight() > other.getHitboxY();
    }

    public abstract void update();
    public abstract void draw(Canvas canvas, int cameraX, int cameraY);

    public void onCollision(GameObject other) {}
    }