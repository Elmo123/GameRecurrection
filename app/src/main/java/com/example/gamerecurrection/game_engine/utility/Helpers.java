package com.example.gamerecurrection.game_engine.utility;

import android.content.Context;

import com.example.gamerecurrection.game_engine.world.Tile;
import com.example.gamerecurrection.game_engine.world.TileSet;
import com.example.gamerecurrection.game_engine.world.WorldMap;

import java.io.InputStream;

public class Helpers {

    public Helpers () {};

    public static String loadJSON(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isTileSolid(WorldMap world, TileSet tileSet, float px, float py, int tileSize) {
        int tileX = (int)(px / tileSize);
        int tileY = (int)(py / tileSize);

        if (tileX < 0 || tileY < 0 || tileX >= world.width || tileY >= world.height)
            return true; // treat out-of-bounds as solid

        int tileId = world.tiles[tileY][tileX];
        Tile tile = tileSet.get(tileId);

        return tile != null && tile.solid;
    }
}
