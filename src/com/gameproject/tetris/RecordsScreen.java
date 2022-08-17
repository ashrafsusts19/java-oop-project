package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;

public class RecordsScreen extends JPanel implements GameStage {

    GameFrame mainGame;
    RecordsScreen(GameFrame _mainGame) {
        mainGame = _mainGame;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#4e495f"));
        this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        g.setColor(Color.decode("#ffffff"));
        g.setColor(Color.GREEN);
        g.fillRect(50, 50, 100, 100);
    }

    @Override
    public void gameLoop() {
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch (key){

        }
    }
}