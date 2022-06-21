package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class BlockData {
    public String name;
    public ArrayList<Pair> blockIndices;
    public Pair centerOffset;
    public boolean isPrimary;
    public boolean isSelected;
    BlockData(String _name, ArrayList<Pair> _blockIndices, Pair _centerOffset, boolean _isPrimary){
        this.name = _name;
        this.blockIndices = _blockIndices;
        this.centerOffset = _centerOffset;
        this.isPrimary = _isPrimary;
        this.isSelected = false;
    }

    BlockData(String _name, ArrayList<Pair> _blockIndices, Pair _centerOffset){
        this.name = _name;
        this.blockIndices = _blockIndices;
        this.centerOffset = _centerOffset;
        this.isPrimary = false;
        this.isSelected = false;
    }
}

public class CustomMenuScreen extends JPanel implements GameStage {

    private class BlockListMiniScreen extends JPanel{
        public int cursorPosition;
        private int startingPosition;
        private int itemsShown;
        private Font defaultFont = new Font(null, Font.PLAIN, 20);
        BlockListMiniScreen(int x, int y, int width, int height) {
            this.cursorPosition = 0;
            this.startingPosition = 0;
            this.itemsShown = height / 30;
            this.setBackground(Color.decode("#0f2a3f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
        }

        public void cursorIncrement() {
            this.cursorPosition = (this.cursorPosition + 1) % blocksData.size();
            if (this.cursorPosition >= this.startingPosition + this.itemsShown){
                this.startingPosition = this.cursorPosition - this.itemsShown + 1;
            }
        }

        public void cursorDecrement() {
            this.cursorPosition = (this.cursorPosition - 1 + blocksData.size()) % blocksData.size();
            if (this.cursorPosition < this.startingPosition){
                this.startingPosition = this.cursorPosition;
            }
        }

        public void pickBlock() {
            BlockData blockdata = blocksData.get(this.cursorPosition);
            blockdata.isSelected = !blockdata.isSelected;
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            for (int i = 0; i < blocksData.size() - this.startingPosition && i < this.itemsShown; i++){
                if (i + this.startingPosition == this.cursorPosition){
                    if (cursorFocus == 0){
                        g.setColor(Color.decode("#997577"));
                    }
                    else {
                        g.setColor(Color.decode("#4e495f"));
                    }
                    g.fillRect(0, i * 30, this.getWidth(), 30);
                }
                drawCenteredText(g, blocksData.get(i + this.startingPosition).name, this.defaultFont,
                        Color.decode("#c3a38a"), this.getWidth() / 2, 10 + i * 30);
                g.setColor(Color.decode("#08141e"));
                g.fillRect(0, i * 30, 30, 30);
                g.setColor(Color.decode("#f6d6bd"));
                if (blocksData.get(i + this.startingPosition).isSelected){
                    g.fillOval(10, i * 30 + 10, 10, 10);
                }
                else {
                    g.drawOval(10, i * 30 + 10, 10, 10);
                }
            }
        }
    }

    private class BlockViewMiniScreen extends JPanel{
        private Font defaultFont = new Font(null, Font.PLAIN, 20);
        BlockViewMiniScreen(int x, int y, int width, int height) {
            this.setBackground(Color.decode("#0f2a3f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){

        }
    }

    private class MenuMiniScreen extends JPanel{

        public int cursorPosition;
        public ArrayList<String> options;
        private Font defaultFont = new Font(null, Font.PLAIN, 30);
        MenuMiniScreen(int x, int y, int width, int height) {
            this.cursorPosition = 0;
            this.setupOptions();
            this.setBackground(Color.decode("#20394f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
        }

        private void setupOptions() {
            options = new ArrayList<>();
            Collections.addAll(options, "Edit", "Delete", "New", "Start");
        }

        public void cursorIncrement() {
            this.cursorPosition = (this.cursorPosition + 1) % this.options.size();
        }

        public void cursorDecrement() {
            this.cursorPosition = (this.cursorPosition - 1 + this.options.size()) % this.options.size();
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            for (int i = 0; i < options.size(); i++){
                if (i == this.cursorPosition){
                    if (cursorFocus == 1) {
                        drawCenteredText(g, options.get(i), defaultFont, Color.decode("#f6d6bd"),
                                this.getWidth() / 2, 60 + i * 40);
                    }
                    else {
                        drawCenteredText(g, options.get(i), defaultFont, Color.decode("#816271"),
                                this.getWidth() / 2, 60 + i * 40);
                    }
                }
                else {
                    drawCenteredText(g, options.get(i), defaultFont, Color.decode("#4e495f"),
                            this.getWidth() / 2, 60 + i * 40);
                }
            }
        }
    }

    GameFrame mainGame;
    int cursorFocus;
    BlockListMiniScreen blockMenuScreen;
    BlockViewMiniScreen blockViewScreen;
    MenuMiniScreen menuMiniScreen;
    ArrayList<BlockData> blocksData;
    CustomMenuScreen(GameFrame _mainGame) {
        this.mainGame = _mainGame;
        this.cursorFocus = 1;
        this.getBLockData();
        this.blockMenuScreen = new BlockListMiniScreen(50, 50, 250, 300);
        this.add(this.blockMenuScreen);
        this.blockViewScreen = new BlockViewMiniScreen(350, 50, 100,100);
        this.add(this.blockViewScreen);
        this.menuMiniScreen = new MenuMiniScreen(50, 400, 400, 250);
        this.add(this.menuMiniScreen);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.decode("#20394f"));
        this.setFocusable(true);
    }

    public void getBLockData() {
        blocksData = new ArrayList<>();

        ArrayList<Pair> blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0),
                new Pair(-1, 0), new Pair(1, 0), new Pair(2, 0)));
        blocksData.add(new BlockData("I", blockIndices, new Pair(0, 0), true));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(-1, -1)));
        blocksData.add(new BlockData("J", blockIndices, new Pair(0, 0), true));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(1, -1)));
        blocksData.add(new BlockData("L", blockIndices, new Pair(0, 0), true));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(0, -1),
                new Pair(1, 0), new Pair(1, -1)));
        blocksData.add(new BlockData("O", blockIndices, new Pair(0, 0), true));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, -1), new Pair(0, -1)));
        blocksData.add(new BlockData("S", blockIndices, new Pair(0, 0), true));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(0, -1)));
        blocksData.add(new BlockData("T", blockIndices, new Pair(0, 0), true));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, -1),
                new Pair(0, -1), new Pair(1, 0)));
        blocksData.add(new BlockData("Z", blockIndices, new Pair(0, 0), true));

        for (int i = 0; i < blocksData.size(); i++){
            blocksData.get(i).isSelected = true;
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

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        g.setColor(Color.decode("#08141e"));
        g.drawRect(49, 49, 251, 301);
        g.drawRect(349, 49, 101,101);
    }

    @Override
    public void gameLoop() {
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch (key){
            case "LEFT":
                cursorFocus = 0;
                break;
            case "RIGHT":
                cursorFocus = 1;
                break;
            case "UP":
                switch (cursorFocus){
                    case 0:
                        this.blockMenuScreen.cursorDecrement();
                        break;
                    case 1:
                        this.menuMiniScreen.cursorDecrement();
                        break;
                }
                break;
            case "DOWN":
                switch (cursorFocus){
                    case 0:
                        this.blockMenuScreen.cursorIncrement();
                        break;
                    case 1:
                        this.menuMiniScreen.cursorIncrement();;
                        break;
                }
                break;
            case "ENTER":
                switch (cursorFocus){
                    case 0:
                        this.blockMenuScreen.pickBlock();
                        break;
                    case 1:
                        break;
                }
        }
    }

}

