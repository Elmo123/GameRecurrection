package com.example.gamerecurrection.game_engine.objects;

import android.graphics.Canvas;

public class LightObject extends GameObject {

    public float radius;
    public int color;
    public boolean flicker;

    private float flickerAmount = 5f;

    public LightObject(float x, float y, float radius, int color, boolean flicker) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.flicker = flicker;

        destroy();
    }

    public float getCurrentRadius() {
        if (!flicker) return radius;
        return radius + (float)(Math.random() * flickerAmount - flickerAmount / 2f);
    }

    @Override
    public void update() {
        // Lights can move if you want
        // Or attach them to other objects
    }

    @Override
    public void draw(Canvas canvas, int cameraX, int cameraY) {
        // Lights do NOT draw themselves here
        // They are drawn by the LightingEngine
    }
}
