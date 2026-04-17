package com.example.gamerecurrection.game_objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.example.gamerecurrection.game_engine.objects.GameObject;
import com.example.gamerecurrection.GameObjectType;
import com.example.gamerecurrection.game_engine.objects.LightObject;

import java.io.IOException;
import java.io.InputStream;

public class Orb extends GameObject {
    private Bitmap sprite;
    private Bitmap[] frames;   // 4 animation frames
    private int currentFrame = 0;
    private final int frameCnt = 2;
    private final int framesPerAnim = 4;
    private int frameWait = 0;
    public LightObject light;

    public Orb(Context ctx, float x, float y) {
        super(x, y);
        type = GameObjectType.ORB;

        try (InputStream is = ctx.getAssets().open("rec_orb_sheet.png")) {
            Bitmap sheet = BitmapFactory.decodeStream(is);

            frames = new Bitmap[frameCnt];
            int frameWidth = sheet.getWidth() / frameCnt;
            // Slice into 4 frames
            for (int i = 0; i < frameCnt; i++) {
                var bitmap = Bitmap.createBitmap(sheet, i * frameWidth, 0, frameWidth, sheet.getHeight());
                frames[i] = Bitmap.createScaledBitmap(bitmap, (int)spriteWidth, (int)spriteHeight, true);
            }

            // Use first frame
            sprite = frames[0];
        } catch (IOException e) {
            e.printStackTrace();
            sprite = null;
        }

        light = new LightObject(x, y, 500, Color.argb(255, 255, 200, 150), true);
    }

    @Override
    public void update() {
        updateAnimation();
    }

    @Override
    public void draw(Canvas canvas, int cameraX, int cameraY) {
        if (sprite == null) return;

        float drawX = x - cameraX;
        float drawY = y - cameraY;

        canvas.drawBitmap(sprite, drawX, drawY, null);
    }

    @Override
    public void onCollision (GameObject other) {
        if (other.getObjectType() == GameObjectType.PLAYER) {
            destroy();
            if (light != null) {
                light.destroy();
            }
        }
    }

    public void updateAnimation () {
        frameWait += 1;
        if (frameWait >= framesPerAnim) {
            frameWait = 0;
            sprite = frames[currentFrame];
            currentFrame += 1;
            if (currentFrame >= frameCnt) {
                currentFrame = 0;
            }
        }
    }
}
