package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;

public class SettingsScreen extends JPanel implements GameStage{

    GameFrame _mainGame;
    SettingsScreen(GameFrame __mainGame ){
        _mainGame=__mainGame;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#3f468f"));
        this.setFocusable(true);
    }

    public void paintComponent(Graphics gg){
        super.paintComponent(gg);
    }
    @Override
    public void gameLoop() {
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch(key){

        }
    }
}
