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
    final float width = 192;
    final float height = 256;
    float speedX, speedY;
    private final int frameCnt = 4;

    public Player(Context ctx, float x, float y, WorldMap world, TileSet tileSet, int tileSize, Joystick joystick) {
        super(x, y);

        try (InputStream is = ctx.getAssets().open("rec_ghost_idle.png")) {
            Bitmap sheet = BitmapFactory.decodeStream(is);

            frames = new Bitmap[frameCnt];
            int frameWidth = sheet.getWidth() / frameCnt;
            // Slice into 4 frames
            for (int i = 0; i < frameCnt; i++) {
                var bitmap = Bitmap.createBitmap(sheet, i * frameWidth, 0, frameWidth, sheet.getHeight());
                frames[i] = Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, true);
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
        // Simple push-back resolution
        if (x < other.x) x = other.x - width;
        else if (x > other.x) x = other.x + other.width;

        if (y < other.y) y = other.y - height;
        else if (y > other.y) y = other.y + other.height;
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

        // --- 1. Horizontal movement ---
        float newX = x + speedX;

        boolean hitX =
                isTileSolid(world, tileSet, newX, y + CORNER_ALLOWANCE, tileSize) ||
                        isTileSolid(world, tileSet, newX, y + height - CORNER_ALLOWANCE, tileSize);

        if (!hitX) {
            x = newX;
        }

        // --- 2. Vertical movement ---
        float newY = y + speedY;

        boolean hitY =
                isTileSolid(world, tileSet, x + CORNER_ALLOWANCE, newY, tileSize) ||
                        isTileSolid(world, tileSet, x + width - CORNER_ALLOWANCE, newY, tileSize);

        if (!hitY) {
            y = newY;
        }
    }
}