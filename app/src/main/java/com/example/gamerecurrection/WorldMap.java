package com.example.gamerecurrection;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorldMap {

    public int width;
    public int height;
    public JSONArray spawnPoint;
    public int[][] tiles;

    public WorldMap(JSONObject json) {
        try {
            width = json.getInt("width");
            height = json.getInt("height");
            spawnPoint = json.getJSONArray("spawn");

            JSONArray rows = json.getJSONArray("tiles");
            tiles = new int[rows.length()][];

            for (int y = 0; y < rows.length(); y++) {
                JSONArray row = rows.getJSONArray(y);
                tiles[y] = new int[row.length()];

                for (int x = 0; x < row.length(); x++) {
                    tiles[y][x] = row.getInt(x);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse world.json");
        }
    }
}