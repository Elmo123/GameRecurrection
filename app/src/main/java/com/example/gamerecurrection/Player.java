package com.example.gamerecurrection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.IOException;
import java.io.InputStream;

public class Player extends GameObject {

    private Bitmap sprite;
    private WorldMap world;
    private TileSet tileSet;
    private int tileSize;

    float x, y;
    float width = 64;
    float height = 64;
    float speedX, speedY;


    public Player(Context ctx, float x, float y, WorldMap world, TileSet tileSet, int tileSize) {
        super(x, y);

        try (InputStream is = ctx.getAssets().open("rec_ghost_idle.png")) {
            sprite = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }
        this.world = world;
        this.tileSet = tileSet;
        this.tileSize = tileSize;
    }

    @Override
    public void update() {
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
                isTileSolid(world, tileSet, newX, y, tileSize) ||
                        isTileSolid(world, tileSet, newX + width, y, tileSize) ||
                        isTileSolid(world, tileSet, newX, y + height, tileSize) ||
                        isTileSolid(world, tileSet, newX + width, y + height, tileSize);

        if (!hitX) {
            x = newX;
        }

        // --- 2. Vertical movement ---
        float newY = y + speedY;

        boolean hitY =
                isTileSolid(world, tileSet, x, newY, tileSize) ||
                        isTileSolid(world, tileSet, x + width, newY, tileSize) ||
                        isTileSolid(world, tileSet, x, newY + height, tileSize) ||
                        isTileSolid(world, tileSet, x + width, newY + height, tileSize);

        if (!hitY) {
            y = newY;
        }
    }
}