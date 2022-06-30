package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class MainMenuScreen extends JPanel implements GameStage{
    GameFrame mainGame;
    Font optionFont, titleFont;
    ArrayList<String> options;
    int cursorPosition;
    MainMenuScreen(GameFrame _mainGame) {
        mainGame = _mainGame;
        optionFont = new Font(null, Font.BOLD, 40);
        titleFont = new Font(null, Font.BOLD, 60);
        this.cursorPosition = 0;
        this.setupOptions();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#4e495f"));
        this.setFocusable(true);
    }

    private void setupOptions(){
        options = new ArrayList<>();
        Collections.addAll(options, "Start", "Custom", "Records", "Settings", "Quit");
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    private void draw(Graphics g){
        this.drawCenteredText(g, "TeTrIS", titleFont, Color.decode("#c2c2d1"), 250, 100);
        for (int i = 0; i < options.size(); i++){
            if (i == cursorPosition) {
                this.drawCenteredText(g, options.get(i), optionFont, Color.decode("#f6d6bd"),
                        250, 200 + i * 60);
            }
            else {
                this.drawCenteredText(g, options.get(i), optionFont, Color.decode("#c3a38a"),
                        250, 200 + i * 60);
            }
        }
        this.highlightCursor(g);
    }

    public void drawCenteredText(Graphics g, String text, Font font, Color color, int x, int y){
        FontMetrics metrics = g.getFontMetrics(font);
        x = x - metrics.stringWidth(text) / 2;
        y = y - metrics.getHeight() / 2;
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y + metrics.getHeight());

    }

    private void highlightCursor(Graphics g){
        this.drawCenteredText(g, "=>", optionFont, Color.decode("#f6d6bd"), 100,
                200 + cursorPosition * 60);
        FontMetrics metrics = g.getFontMetrics(optionFont);

    }

    @Override
    public void gameLoop() {
        this.repaint();

    }

    @Override
    public void keyPressed(String key) {
        switch (key){
            case "UP":
                cursorPosition = (cursorPosition - 1 + options.size()) % options.size();
                break;
            case "DOWN":
                cursorPosition = (cursorPosition + 1) % options.size();
                break;
            case "ENTER":
                switch (this.options.get(this.cursorPosition)) {
                    case "Start":
                        mainGame.initializeGame();
                        mainGame.setDirectory("GameScreen");
                        break;
                    case "Custom":
                        mainGame.setDirectory("CustomMenuScreen");
                        break;
                    case "Records":
                        mainGame.setDirectory("RecordScreen");
                        break;
                    case "Settings":
                        mainGame.setDirectory("SettingsScreen");
                        break;
                    case "Quit":
                        System.exit(0);
                        break;
                }
                break;

        }
    }
}
