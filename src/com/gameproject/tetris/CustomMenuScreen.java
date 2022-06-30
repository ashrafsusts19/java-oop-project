package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

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
            else if (this.cursorPosition == 0){
                this.startingPosition = this.cursorPosition;
            }
        }

        public void cursorDecrement() {
            this.cursorPosition = (this.cursorPosition - 1 + blocksData.size()) % blocksData.size();
            if (this.cursorPosition < this.startingPosition){
                this.startingPosition = this.cursorPosition;
            }
            else if (this.cursorPosition == blocksData.size() - 1){
                this.startingPosition = Integer.max(0, this.cursorPosition - this.itemsShown + 1);
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
            if (cursorFocus == 0){
                g.setColor(Color.decode("#997577"));
            }
            else {
                g.setColor(Color.decode("#4e495f"));
            }
            g.fillRect(0, (this.cursorPosition - this.startingPosition) * 30, this.getWidth(), 30);
            g.setColor(Color.decode("#08141e"));
            g.fillRect(0, 0, 30, this.getHeight());
            for (int i = 0; i < blocksData.size() - this.startingPosition && i < this.itemsShown; i++){
                drawCenteredText(g, blocksData.get(i + this.startingPosition).name, this.defaultFont,
                        Color.decode("#c3a38a"), this.getWidth() / 2, 10 + i * 30);
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
        int gridWidth, gridHeight;
        Pair center;
        BlockViewMiniScreen(int x, int y, int width, int height, int rows, int cols) {
            this.setBackground(Color.decode("#0f2a3f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
            this.gridWidth = width / cols;
            this.gridHeight = height / rows;
            center = new Pair(cols / 2, rows / 2);
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            BlockData currentBlock = blocksData.get(blockMenuScreen.cursorPosition);
            Pair offset = currentBlock.centerOffset;
            g.setColor(Color.decode("#c3a38a"));
            for (Pair gridIndex: currentBlock.blockIndices){
                g.fillRect((gridIndex.x + center.x + offset.x) * gridWidth + 1,
                        (gridIndex.y + center.y + offset.y) * gridHeight + 1,
                        gridWidth - 2, gridHeight - 2);
            }
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
            Collections.addAll(options, "Start", "Edit", "Delete", "New");
        }

        public String getCurrentOption() {
            return this.options.get(this.cursorPosition);
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
                Color textColor;
                if (blocksData.get(blockMenuScreen.cursorPosition).isPrimary &&
                        ((options.get(i) == "Edit") || options.get(i) == "Delete")){
                    if (i == this.cursorPosition){
                        if (cursorFocus == 1) {
                            textColor = Color.decode("#c3a38a");
                        }
                        else {
                            textColor = Color.decode("#0f2a3f");
                        }
                    }
                    else {
                        textColor = Color.decode("#08141e");
                    }
                }
                else if (i == this.cursorPosition){
                    if (cursorFocus == 1) {
                        textColor = Color.decode("#f6d6bd");
                    }
                    else {
                        textColor = Color.decode("#816271");
                    }
                }
                else {
                    textColor = Color.decode("#4e495f");
                }
                drawCenteredText(g, options.get(i), defaultFont, textColor,
                        this.getWidth() / 2, 60 + i * 40);
            }
        }
    }

    GameFrame mainGame;
    int cursorFocus;
    BlockListMiniScreen blockMenuScreen;
    BlockViewMiniScreen blockViewScreen;
    MenuMiniScreen menuMiniScreen;
    ArrayList<BlockData> blocksData;
    int blockRows = 5, blockCols = 5;
    CustomMenuScreen(GameFrame _mainGame) {
        this.mainGame = _mainGame;
        this.cursorFocus = 0;
        this.getBLockData();
        this.blockMenuScreen = new BlockListMiniScreen(50, 50, 250, 300);
        this.add(this.blockMenuScreen);
        this.blockViewScreen = new BlockViewMiniScreen(350, 50, 100,100, this.blockRows, this.blockCols);
        this.add(this.blockViewScreen);
        this.menuMiniScreen = new MenuMiniScreen(50, 400, 400, 250);
        this.add(this.menuMiniScreen);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.decode("#20394f"));
        this.setFocusable(true);
    }

    public void refresh() {
        this.remove(this.blockMenuScreen);
        this.remove(this.blockViewScreen);
        this.remove(this.menuMiniScreen);
        this.cursorFocus = 0;
        this.getBLockData();
        this.blockMenuScreen = new BlockListMiniScreen(50, 50, 250, 300);
        this.add(this.blockMenuScreen);
        this.blockViewScreen = new BlockViewMiniScreen(350, 50, 100,100, this.blockRows, this.blockCols);
        this.add(this.blockViewScreen);
        this.menuMiniScreen = new MenuMiniScreen(50, 400, 400, 250);
        this.add(this.menuMiniScreen);
        this.revalidate();
        this.repaint();
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
        for (String encodedData: mainGame.saveDataBlocks){
            StringTokenizer st = new StringTokenizer(encodedData, "-");
            String name, sCenter, sBlockMap;
            name = st.nextToken();
            sCenter = st.nextToken();
            sBlockMap = st.nextToken();

            StringTokenizer st2 = new StringTokenizer(sCenter, " ");
            int centerx, centery;
            centerx = Integer.parseInt(st2.nextToken());
            centery = Integer.parseInt(st2.nextToken());
            Pair offset = new Pair(centerx - (this.blockCols / 2), centery - (this.blockRows / 2));
            ArrayList<Pair> savedBlockIndices = new ArrayList<>();
            for (int i = 0; i < this.blockRows; i++){
                for (int j = 0; j < this.blockCols; j++){
                    if (sBlockMap.charAt(i * this.blockCols + j) == '1'){
                        savedBlockIndices.add(new Pair(j - centerx, i - centery));
                    }
                }
            }
            this.blocksData.add(new BlockData(name, savedBlockIndices, offset, false));
        }
    }

    private void displayError(String errorText){
        Font font = new Font(null, Font.BOLD, 15);
        JLabel errorlabel = new JLabel(errorText);
        errorlabel.setOpaque(true);
        errorlabel.setForeground(Color.decode("#f6d6bd"));
        errorlabel.setBackground(Color.decode("#0f2a3f"));
        errorlabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorlabel.setFont(font);
        errorlabel.setBounds(350, 200, 100, 30);
        this.add(errorlabel);
        JPanel currentPanel = this;
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPanel.remove(errorlabel);
                currentPanel.revalidate();
                currentPanel.repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
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
                        switch (this.menuMiniScreen.getCurrentOption()){
                            case "Start":
                                ArrayList<BlockData> selectedBLocks = new ArrayList<>();
                                for (BlockData blockData: this.blocksData){
                                    if (blockData.isSelected){
                                        selectedBLocks.add(blockData);
                                    }
                                }
                                if (selectedBLocks.size() == 0){
                                    this.displayError("Must Select at least one Block");
                                }
                                else {
                                    mainGame.initializeCustorGame(selectedBLocks);
                                    mainGame.setDirectory("GameScreen");
                                }
                                break;
                            case "Edit":
                                {
                                    BlockData activeBlock = this.blocksData.get(blockMenuScreen.cursorPosition);
                                    if (!activeBlock.isPrimary) {
                                        mainGame.initializeEditor(activeBlock);
                                        mainGame.setDirectory("BlockBuilderScreen");
                                    }
                                }
                                break;
                            case "Delete":
                                {
                                    BlockData activeBlock = this.blocksData.get(this.blockMenuScreen.cursorPosition);
                                    if (!activeBlock.isPrimary) {
                                        ArrayList<String> saveData = mainGame.saveDataBlocks;
                                        for (int i = 0; i < saveData.size(); i++){
                                            if (saveData.get(i).startsWith(activeBlock.name)){
                                                saveData.remove(i);
                                                break;
                                            }
                                        }
                                        this.blocksData.remove(this.blockMenuScreen.cursorPosition);
                                        if (this.blockMenuScreen.cursorPosition == this.blocksData.size()){
                                            this.blockMenuScreen.cursorPosition--;
                                        }
                                        mainGame.saveBlockData();
                                    }
                                }
                                break;
                            case "New":
                                mainGame.initializeEditor(null);
                                mainGame.setDirectory("BlockBuilderScreen");
                                break;
                        }
                        break;
                }
        }
    }

}

