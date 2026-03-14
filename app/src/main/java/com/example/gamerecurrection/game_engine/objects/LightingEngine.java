package com.example.gamerecurrection.game_engine.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.List;

public class LightingEngine {

    private Paint darknessPaint;
    private Paint lightPaint;

    public LightingEngine() {

        darknessPaint = new Paint();
        darknessPaint.setColor(Color.argb(220, 0, 0, 0));

        lightPaint = new Paint();
        lightPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    public void draw(Canvas canvas, List<GameObject> objects, int cameraX, int cameraY) {

        // 0. Create an offscreen layer for lighting
        int saveCount = canvas.saveLayer(
                0, 0,
                canvas.getWidth(),
                canvas.getHeight(),
                null
        );

        // 1. Draw darkness layer ON THE LAYER
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), darknessPaint);

        // 2. Cut holes with lights (DST_OUT)
        for (GameObject obj : objects) {
            if (obj instanceof LightObject) {
                LightObject light = (LightObject) obj;

                float r = light.getCurrentRadius();

                RadialGradient gradient = new RadialGradient(
                        light.x - /* cameraX if needed */ 0,
                        light.y - /* cameraY if needed */ 0,
                        r,
                        new int[] {
                                light.color,
                                Color.argb(0,
                                        Color.red(light.color),
                                        Color.green(light.color),
                                        Color.blue(light.color))
                        },
                        new float[] { 0f, 1f },
                        Shader.TileMode.CLAMP
                );

                lightPaint.setShader(gradient);
                canvas.drawCircle(
                        light.x - /*cameraX*/ 0,
                        light.y - /*cameraY*/ 0,
                        r,
                        lightPaint
                );
            }
        }

        // 3. Composite the lighting layer back onto the main canvas
        canvas.restoreToCount(saveCount);
    }
}
