package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel implements GameStage{

    GameFrame mainGame;
    GameScreen(GameFrame _mainGame) {
        mainGame = _mainGame;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#311a30"));
        this.setFocusable(true);
        this.startGame();
    }

    private void startGame(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    private void draw(Graphics g){
        g.setColor(Color.GRAY);
        g.drawRect(100, 100, 20, 20);
    }

    @Override
    public void gameLoop() {
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch (key){
            case "BACKSPACE":
                break;

        }
    }
}
