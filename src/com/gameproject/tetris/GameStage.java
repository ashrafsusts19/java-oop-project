package com.gameproject.tetris;

public interface GameStage {
    int SCREEN_WIDTH = 500;
    int SCREEN_HEIGHT = 700;
    int DELAY = 20;

    public void gameLoop();
    public void keyPressed(String key);
    default public void refresh(){

    }
}
