package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;

public class RecordsScreen extends JPanel implements GameStage {

    GameFrame mainGame;
    Font font;
    Font titleFont;
    Font highScoreFont;
    RecordsScreen(GameFrame _mainGame) {
        mainGame = _mainGame;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#4e495f"));
        this.setFocusable(true);
        this.font = new Font(null, Font.BOLD, 35);
        this.titleFont = new Font(null, Font.BOLD, 60);
        this.highScoreFont = new Font(null, Font.BOLD, 50);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        g.setColor(Color.decode("#ffffff"));
        g.setColor(Color.CYAN);
        //g.fillRect(50, 50, 100, 100);
        //drawCenteredText(g,"Score",new Font(null, Font.ITALIC, 30),Color.MAGENTA,50,50);
        this.drawCenteredText(g, "Hall of Fame", this.titleFont, Color.decode("#c2c2d1"),
                SCREEN_WIDTH / 2, 55);
        this.drawCenteredText(g, "" + mainGame.saveDataScore.get(0), this.highScoreFont,
                Color.decode("#f6d6bd"), SCREEN_WIDTH / 2, 125);
        for (int i = 1; i < mainGame.saveDataScore.size(); i++){
            this.drawCenteredText(g,  "" + mainGame.saveDataScore.get(i), this.font,
                    Color.decode("#c3a38a"), SCREEN_WIDTH / 2, 140 + 50 * i);
        }
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

    public void drawCenteredText(Graphics g, String text, Font font, Color color, int x, int y){
        FontMetrics metrics = g.getFontMetrics(font);
        x = x - metrics.stringWidth(text) / 2;
        y = y - metrics.getHeight() / 2;
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y + metrics.getHeight());


    }

}