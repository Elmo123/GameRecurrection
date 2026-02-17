package com.example.gamerecurrection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.json.JSONObject;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoop gameLoop;

    private WorldMap world;
    private TileSet tileSet;

    private int cameraX = 0;
    private int cameraY = 0;
    private GameObjectManager objectManager = new GameObjectManager();
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

            String worldStr = helpers.loadJSON(context, "world.json");
            String tilesStr = helpers.loadJSON(context, "tileset.json");

            joystick = new Joystick(200, 200, 150, 60);
            world = new WorldMap(worldJson);
            tileSet = new TileSet(context, tilesJson, TILE_SIZE, TILE_SIZE);
            player = new Player(context,300, 300, world, tileSet, TILE_SIZE, joystick);

            objectManager.initSpatialGrid(world.width, world.height, TILE_SIZE);
            objectManager.add(player);
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
        // cameraX = (int)(player.getX() - getWidth() / 2);
        // cameraY = (int)(player.getY() - getHeight() / 2);
    }

    public void render() {
        if (!getHolder().getSurface().isValid()) return;

        Canvas canvas = getHolder().lockCanvas();
        drawWorld(canvas);
        objectManager.drawAll(canvas, cameraX, cameraY);

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
                    Log.d("22:", "Tile or tile.bitmap == null");
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
}