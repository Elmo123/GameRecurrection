package com.example.gamerecurrection.game_engine.world;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WorldMap {

    public int width;
    public int height;
    public JSONArray spawnPoint;
    public int[][] tiles;
    private JSONArray jsonTiles; // store original JSON reference


    public WorldMap(JSONObject json) throws JSONException {
        try {
            spawnPoint = json.getJSONArray("spawn");

            JSONArray rows = json.getJSONArray("tiles");
            height = rows.length();
            width = rows.getJSONArray(0).length();
            jsonTiles = rows;

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

    public void setTile(int x, int y, int value) throws JSONException {
        if (x < 0 || y < 0 || x >= width || y >= height) return;

        tiles[y][x] = value;                       // update internal map
        jsonTiles.getJSONArray(y).put(x, value);   // update JSON map
    }

}