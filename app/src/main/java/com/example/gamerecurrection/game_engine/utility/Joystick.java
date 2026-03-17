package com.example.gamerecurrection.game_engine.utility;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Joystick {

    private float centerX, centerY;
    private float baseRadius, knobRadius;

    private float knobX, knobY;
    private boolean active = false;

    public float dx = 0;
    public float dy = 0;
    public boolean visible = false;

    private Paint basePaint = new Paint();
    private Paint knobPaint = new Paint();

    public Joystick(float x, float y, float baseR, float knobR, boolean visible) {
        centerX = x;
        centerY = y;
        baseRadius = baseR;
        knobRadius = knobR;

        knobX = x;
        knobY = y;

        basePaint.setColor(Color.GRAY);
        basePaint.setAlpha(120);

        knobPaint.setColor(Color.WHITE);
        knobPaint.setAlpha(180);

        this.visible = visible;
    }

    public void setPosition(int x, int y) {
        centerX = x;
        centerY = y;

        knobX = x;
        knobY = y;

        visible = true;
    }

    public void draw(Canvas canvas) {
        if (visible) {
            canvas.drawCircle(centerX, centerY, baseRadius, basePaint);
            canvas.drawCircle(knobX, knobY, knobRadius, knobPaint);
        }
    }

    public void onTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (distance(x, y, centerX, centerY) < baseRadius) {
                    active = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (active) {
                    float dxRaw = x - centerX;
                    float dyRaw = y - centerY;

                    float dist = (float)Math.sqrt(dxRaw*dxRaw + dyRaw*dyRaw);

                    if (dist < baseRadius) {
                        knobX = x;
                        knobY = y;
                    } else {
                        knobX = centerX + (dxRaw / dist) * baseRadius;
                        knobY = centerY + (dyRaw / dist) * baseRadius;
                    }

                    dx = (knobX - centerX) / baseRadius;
                    dy = (knobY - centerY) / baseRadius;
                }
                break;

            case MotionEvent.ACTION_UP:
                active = false;
                knobX = centerX;
                knobY = centerY;
                dx = dy = 0;
                break;
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float)Math.sqrt(dx*dx + dy*dy);
    }
}