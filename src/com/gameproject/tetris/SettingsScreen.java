package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsScreen extends JPanel implements GameStage{

    GameFrame mainGame;
    int cursorPosition;
    ArrayList<String> options;
    String[] GAMESIZE = new String[]{"Medium", "Large"};
    Font optionFont, titleFont;
    SettingsScreen(GameFrame _mainGame ){
        this.mainGame= _mainGame;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#4e495f"));
        this.setFocusable(true);
        this.cursorPosition = 0;
        this.setupOptions();
        this.optionFont = new Font(null, Font.BOLD, 30);
        this.titleFont = new Font(null, Font.BOLD, 50);
    }

    private void setupOptions(){
        options = new ArrayList<>();
        Collections.addAll(options, "Board Size: " + this.GAMESIZE[mainGame.GAMESIZEOPTION],
                "Score Reset", "Block Reset");
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        this.drawCenteredText(g, "Options", this.titleFont, Color.decode("#c2c2d1"),
                250, 100);
        for (int i = 0; i < this.options.size(); i++){
            if (i == this.cursorPosition) {
                this.drawCenteredText(g, options.get(i), optionFont, Color.decode("#f6d6bd"),
                        250, 200 + i * 45);
            }
            else {
                this.drawCenteredText(g, options.get(i), optionFont, Color.decode("#c3a38a"),
                        250, 200 + i * 45);
            }
        }
        this.highlightCursor(g);
    }

    private void highlightCursor(Graphics g){
        this.drawCenteredText(g, "=>", optionFont, Color.decode("#f6d6bd"), 75,
                200 + cursorPosition * 45);
        FontMetrics metrics = g.getFontMetrics(optionFont);

    }

    @Override
    public void gameLoop() {
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch(key){
            case "UP":
                cursorPosition = (cursorPosition - 1 + options.size()) % options.size();
                break;
            case "DOWN":
                cursorPosition = (cursorPosition + 1) % options.size();
                break;
            case "LEFT":
                if (this.options.get(this.cursorPosition).startsWith("Board Size:")){
                    mainGame.GAMESIZEOPTION = (mainGame.GAMESIZEOPTION - 1) % 2;
                    this.options.set(this.cursorPosition, "Board Size: " + this.GAMESIZE[mainGame.GAMESIZEOPTION]);
                }
                break;
            case "RIGHT":
                if (this.options.get(this.cursorPosition).startsWith("Board Size:")){
                    mainGame.GAMESIZEOPTION = (mainGame.GAMESIZEOPTION + 1) % 2;
                    this.options.set(this.cursorPosition, "Board Size: " + this.GAMESIZE[mainGame.GAMESIZEOPTION]);
                }
                break;
            case "ENTER":
                if (this.options.get(this.cursorPosition).startsWith("Board Size:")){
                    mainGame.GAMESIZEOPTION = (mainGame.GAMESIZEOPTION + 1) % 2;
                    this.options.set(this.cursorPosition, "Board Size: " + this.GAMESIZE[mainGame.GAMESIZEOPTION]);
                }
                else {
                    switch (this.options.get(this.cursorPosition)){
                        case "Block Reset":
                            mainGame.blockReset();
                            break;
                        case "Score Reset":
                            mainGame.scoreReset();
                            break;
                    }
                }
                break;

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
