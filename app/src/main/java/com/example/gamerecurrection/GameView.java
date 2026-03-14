package com.example.gamerecurrection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.gamerecurrection.game_engine.GameLoop;
import com.example.gamerecurrection.game_engine.objects.GameObject;
import com.example.gamerecurrection.game_engine.objects.GameObjectManager;
import com.example.gamerecurrection.game_engine.objects.LightObject;
import com.example.gamerecurrection.game_engine.objects.LightingEngine;
import com.example.gamerecurrection.game_engine.objects.ObjectEventListener;
import com.example.gamerecurrection.game_objects.Orb;
import com.example.gamerecurrection.game_objects.Player;
import com.example.gamerecurrection.game_engine.utility.Helpers;
import com.example.gamerecurrection.game_engine.utility.Joystick;
import com.example.gamerecurrection.game_engine.world.Tile;
import com.example.gamerecurrection.game_engine.world.TileSet;
import com.example.gamerecurrection.game_engine.world.WorldMap;

import org.json.JSONObject;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, ObjectEventListener {

    private GameLoop gameLoop;

    private WorldMap world;
    private TileSet tileSet;

    private int cameraX = 0;
    private int cameraY = 0;
    private GameObjectManager objectManager = new GameObjectManager();
    private LightingEngine lighting = new LightingEngine();
    private Player player;
    private Helpers helpers = new Helpers();
    private final int TILE_SIZE = 256;
    private Joystick joystick;

    public GameView(Context context) {
        super(context);

        // 1. Load JSON FIRST (blocking, safe)
        try {
            JSONObject worldJson = new JSONObject(helpers.loadJSON(context, "world.json"));
            JSONObject tilesJson = new JSONObject(helpers.loadJSON(context, "tileset.json"));

            /* Edit Json
            JSONArray row = worldJson.getJSONArray("tiles");
            row.put(32, 1);
            /* */

            // objectManager.add(new LightObject(800, 600, 300,
            //        Color.argb(255, 200, 200, 255), false));


            joystick = new Joystick(200, 200, 150, 60);
            world = new WorldMap(worldJson);
            tileSet = new TileSet(context, tilesJson, TILE_SIZE, TILE_SIZE);

            int spawnPointX = world.spawnPoint.getInt(0) * TILE_SIZE + (TILE_SIZE / 2);
            int spawnPointY = world.spawnPoint.getInt(1) * TILE_SIZE + (TILE_SIZE / 2);

            player = new Player(context,spawnPointX, spawnPointY, world, tileSet, TILE_SIZE, joystick);


            objectManager.initSpatialGrid(world.width * TILE_SIZE
                    , world.height * TILE_SIZE
                    , TILE_SIZE);
            objectManager.add(player);

            // Testing
            spawnNewOrb();

            objectManager.setEventListener(this);

            /* Set tile helper
            world.setTile(32, 32, 1);
            /* */
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Create loop AFTER world is guaranteed to exist
        gameLoop = new GameLoop(this);

        // 3. Register callback LAST
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Safety check
        if (world == null) {
            throw new RuntimeException("World failed to load before loop start");
        }

        gameLoop.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameLoop.stop();
    }

    //@Override
    public void update() {
        objectManager.updateAll();

        // Camera moves with player
         cameraX = (int)(player.getHitboxX() - getWidth() / 2);
         cameraY = (int)(player.getHitboxY() - getHeight() / 2);
    }

    public void render() {
        if (!getHolder().getSurface().isValid()) return;

        Canvas canvas = getHolder().lockCanvas();
        drawWorld(canvas);
        objectManager.drawAll(canvas, cameraX, cameraY);

        objectManager.add(new LightObject((int)(getWidth()/2), (int)(getHeight()/2), 500,
                Color.argb(255, 255, 200, 150), true));

        lighting.draw(canvas, objectManager.GetObjects(), cameraX, cameraY);

        joystick.draw(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawWorld(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        int screenW = canvas.getWidth();
        int screenH = canvas.getHeight();

        int startX = cameraX / TILE_SIZE;
        int startY = cameraY / TILE_SIZE;

        int endX = (cameraX + screenW) / TILE_SIZE + 1;
        int endY = (cameraY + screenH) / TILE_SIZE + 1;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {

                if (x < 0 || y < 0 || x >= world.width - 1 || y >= world.height - 1)
                    continue;
                int tileId = world.tiles[y][x];
                Tile tile = tileSet.get(tileId);

                if (tile == null || tile.bitmap == null) {
                    continue;
                }

                int drawX = x * TILE_SIZE - cameraX;
                int drawY = y * TILE_SIZE - cameraY;

                canvas.drawBitmap(tile.bitmap, drawX, drawY, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        joystick.onTouch(event);
        return true;
    }

    @Override
    public void onObjectDestroyed(GameObject obj) {
        if (obj.getObjectType() == GameObjectType.ORB) {
            spawnNewOrb();
        }
    }

    public void spawnNewOrb() {
        var obj = new Orb(this.getContext(),player.getHitboxX() - 258, player.getHitboxY() - 258);
        objectManager.add(obj);
    }
}