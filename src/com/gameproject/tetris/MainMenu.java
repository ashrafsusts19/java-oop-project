package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class MainMenu extends JPanel implements GameStage{
    GameFrame mainGame;
    Font optionFont, titleFont;
    ArrayList<String> options;
    int cursorPosition;
    MainMenu(GameFrame _mainGame) {
        mainGame = _mainGame;
        optionFont = new Font(null, Font.BOLD, 40);
        titleFont = new Font(null, Font.BOLD, 60);
        this.cursorPosition = 0;
        this.setupOptions();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.decode("#464780"));
        this.setFocusable(true);
    }

    private void setupOptions(){
        options = new ArrayList<>();
        Collections.addAll(options, "Start", "Quit");
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    private void draw(Graphics g){
        this.drawCenteredText(g, "TeTrIS", titleFont, Color.decode("#303030"), 250, 150);
        for (int i = 0; i < options.size(); i++){
            this.drawCenteredText(g, options.get(i), optionFont, Color.decode("#7a7fc5"),
                    250, 250 + i * 60);
        }
        this.highlightCursor(g);
    }

    public void drawCenteredText(Graphics g, String text, Font font, Color color, int x, int y){
        FontMetrics metrics = g.getFontMetrics(font);
        x = x - metrics.stringWidth(text) / 2;
        y = y - metrics.getHeight() / 2;
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y);
    }

    private void highlightCursor(Graphics g){
        this.drawCenteredText(g, "=>", optionFont, Color.decode("#ba9880"), 100,
                250 + cursorPosition * 60);
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
        }
    }
}
