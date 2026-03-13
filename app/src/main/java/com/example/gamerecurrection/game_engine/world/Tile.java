package com.example.gamerecurrection.game_engine.world;

import android.graphics.Bitmap;

public class Tile {
    public Bitmap bitmap;
    public boolean solid;

    public Tile(Bitmap bitmap, int width, int height, boolean solid) {
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        this.solid = solid;
    }
}
