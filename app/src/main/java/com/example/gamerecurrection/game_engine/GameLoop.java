package com.example.gamerecurrection.game_engine;

import com.example.gamerecurrection.GameView;

public class GameLoop implements Runnable {

    private Thread thread;
    private boolean running = false;

    private final GameView gameView;

    private static final int UPS = 60;
    private static final double UPDATE_INTERVAL = 1_000_000_000.0 / UPS;

    public GameLoop(GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    public void run() {
        long previousTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - previousTime) / UPDATE_INTERVAL;
            previousTime = currentTime;

            while (delta >= 1) {
                gameView.update();
                delta--;
            }

            gameView.render();
        }
    }

    public void start() {
        if (running) return;

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if (!running) return;

        running = false;
        try {
            thread.join();
        } catch (InterruptedException ignored) {}
    }
}