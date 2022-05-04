package com.gameproject.tetris;

public interface GameStage {
    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 600;
    int DELAY = 20;

    public void gameLoop();
    public void keyPressed(String key);
}
