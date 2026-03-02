package com.example.gamerecurrection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.IOException;
import java.io.InputStream;

public class Player extends GameObject {

    private Bitmap sprite;
    private Bitmap[] frames;   // 4 animation frames
    private WorldMap world;
    private TileSet tileSet;
    private int tileSize;
    private Joystick joystick;
    private static final float CORNER_ALLOWANCE = 6f;
    float speedX, speedY;
    private final int frameCnt = 4;

    public Player(Context ctx, float x, float y, WorldMap world, TileSet tileSet, int tileSize, Joystick joystick) {
        super(x, y);
        this.spriteWidth = 192;
        this.spriteHeight = 256;

        this.hitboxWidth = 96;
        this.hitboxHeight = 96;

        // Center horizontally, align bottom
        this.hitboxOffsetX = (spriteWidth - hitboxWidth) / 2f;
        this.hitboxOffsetY = spriteHeight - hitboxHeight;

        try (InputStream is = ctx.getAssets().open("rec_ghost_idle.png")) {
            Bitmap sheet = BitmapFactory.decodeStream(is);

            frames = new Bitmap[frameCnt];
            int frameWidth = sheet.getWidth() / frameCnt;
            // Slice into 4 frames
            for (int i = 0; i < frameCnt; i++) {
                var bitmap = Bitmap.createBitmap(sheet, i * frameWidth, 0, frameWidth, sheet.getHeight());
                frames[i] = Bitmap.createScaledBitmap(bitmap, (int)spriteWidth, (int)spriteHeight, true);
            }

            // Use first frame
            sprite = frames[0];

        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }

        this.world = world;
        this.tileSet = tileSet;
        this.tileSize = tileSize;
        this.joystick = joystick;
    }

    @Override
    public void update() {
        speedX = joystick.dx * 5f;
        speedY = joystick.dy * 5f;

        move(world, tileSet, tileSize);
    }

    @Override
    public void draw(Canvas canvas, int cameraX, int cameraY) {
        if (sprite == null) return;

        float drawX = x - cameraX;
        float drawY = y - cameraY;

        canvas.drawBitmap(sprite, drawX, drawY, null);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Player collided with " + other);
        // Push player
        resolveCollision(other);
    }

    public void resolveCollision(GameObject other) {
        // NPC / Enemy
        // Simple push-back resolution
        // Outdated

        /*
        if (x < other.x) x = other.x - width;
        else if (x > other.x) x = other.x + other.width;

        if (y < other.y) y = other.y - height;
        else if (y > other.y) y = other.y + other.height;
         */
    }

    private boolean isTileSolid(WorldMap world, TileSet tileSet, float px, float py, int tileSize) {
        int tileX = (int)(px / tileSize);
        int tileY = (int)(py / tileSize);

        if (tileX < 0 || tileY < 0 || tileX >= world.width || tileY >= world.height)
            return true; // treat out-of-bounds as solid

        int tileId = world.tiles[tileY][tileX];
        Tile tile = tileSet.get(tileId);

        return tile != null && tile.solid;
    }

    public void move(WorldMap world, TileSet tileSet, int tileSize) {

        float left   = getHitboxX();
        float right  = getHitboxX() + getHitboxWidth();
        float top    = getHitboxY();
        float bottom = getHitboxY() + getHitboxHeight();

        // --- 1. Horizontal movement ---
        if (speedX > 0) {
            boolean hit =
                    isTileSolid(world, tileSet, right + speedX, top + CORNER_ALLOWANCE, tileSize) ||
                            isTileSolid(world, tileSet, right + speedX, bottom - CORNER_ALLOWANCE, tileSize);

            if (!hit) {
                x += speedX;
                left   += speedX;
                right  += speedX;
            }

        } else if (speedX < 0) {
            boolean hit =
                    isTileSolid(world, tileSet, left + speedX, top + CORNER_ALLOWANCE, tileSize) ||
                            isTileSolid(world, tileSet, left + speedX, bottom - CORNER_ALLOWANCE, tileSize);

            if (!hit) {
                x += speedX;
                left   += speedX;
                right  += speedX;
            }
        }

        // --- 2. Vertical movement ---
        if (speedY > 0) {
            boolean hit =
                    isTileSolid(world, tileSet, left + CORNER_ALLOWANCE, bottom + speedY, tileSize) ||
                            isTileSolid(world, tileSet, right - CORNER_ALLOWANCE, bottom + speedY, tileSize);

            if (!hit) {
                y += speedY;
            }

        } else if (speedY < 0) {
            boolean hit =
                    isTileSolid(world, tileSet, left + CORNER_ALLOWANCE, top + speedY, tileSize) ||
                            isTileSolid(world, tileSet, right - CORNER_ALLOWANCE, top + speedY, tileSize);

            if (!hit) {
                y += speedY;
            }
        }
    }
}