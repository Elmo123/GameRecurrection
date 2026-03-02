package com.example.gamerecurrection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TileSet {
    private Map<Integer, Tile> tiles = new HashMap<>();

    public TileSet(Context context, JSONObject json, int tileW, int tileH) {
        try {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject tileObj = json.getJSONObject(key);
                String texture = tileObj.getString("texture");
                boolean solid = tileObj.optBoolean("solid", false);

                Bitmap bmp = BitmapFactory.decodeStream(
                        context.getAssets().open(texture)
                );

                if (bmp == null) {
                    System.out.println("FAILED to decode bitmap: " + texture);
                }

                tiles.put(Integer.parseInt(key), new Tile(bmp, tileW, tileH, solid));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("TileSet failed to load");
        }
    }

    public Tile get(int id) {
        return tiles.get(id);
    }
}
