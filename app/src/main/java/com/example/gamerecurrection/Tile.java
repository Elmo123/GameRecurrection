package com.example.gamerecurrection;

import android.graphics.Bitmap;

public class Tile {
    public Bitmap bitmap;

    public Tile(Bitmap bitmap, int width, int height) {
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
}
