package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.min;


public class GameScreen extends JPanel implements GameStage{
    int GRIDW = 20, GRIDH = 20;
    int ROWS = 20, COLS = 10;
    private class IngameBlock {
        ArrayList<Pair> blockIndices;
        int x, y;
        int blockStates;
        int maxLen;
        int currState = 1;
        int xLeft, xRight, yTop, yBot;
        Pair centerOffset;

        IngameBlock(TetrisBlock tetrisBlock){
            this.blockIndices = new ArrayList<>();
            int miny = 99999;
            for (Pair blockIndex: tetrisBlock.blockIndices){
                blockIndices.add(new Pair(blockIndex.x, blockIndex.y));
                miny = min(miny, blockIndex.y);
            }
            x = gridMap[0].length / 2 - 1;
            y = -miny;
            this.blockStates = tetrisBlock.blockStates;
            this.maxLen = tetrisBlock.maxLen;
            this.centerOffset = tetrisBlock.centerOffset;
            this.defineShape();
        }

        public void defineShape(){
            xLeft = 99999;
            xRight = -99999;
            yTop = 99999;
            yBot = -99999;
            for (Pair index: blockIndices){
                if (index.x < xLeft) xLeft = index.x;
                if (index.x > xRight) xRight = index.x;
                if (index.y < yTop) yTop = index.y;
                if (index.y > yBot) yBot = index.y;
            }
        }

        public void drawBLock(Graphics g){
            g.setColor(Color.decode("#f6d6bd"));
            for (Pair index: blockIndices){
                g.fillRect((x + index.x) * GRIDW + 1, (y + index.y) * GRIDH + 1, GRIDW - 2, GRIDH - 2);
            }
        }

        public boolean move(int dx, int dy){
            if (this.moveAvailable(this.x + dx, this.y + dy, this.blockIndices)){
                this.x += dx;
                this.y += dy;
                return true;
            }
            else if (dx == 0 && dy == 1){
                this.place();
            }
            return false;
        }

        public void place(){
            for (Pair index: this.blockIndices){
                gridMap[this.y + index.y][this.x + index.x] = true;
            }
            postBlockPlacement();
        }

        private boolean moveAvailable(int cx, int cy, ArrayList<Pair> blockShape) {
            for (Pair index: blockShape){
                if (cy + index.y >= ROWS || cy + index.y < 0) return false;
                else if (cx + index.x >= COLS || cx + index.x < 0) return false;
                else if (gridMap[cy + index.y][cx + index.x]) return false;
            }
            return true;
        }

        private Pair offsetedPosition(ArrayList<Pair> rotatedShape){
            if (this.moveAvailable(this.x, this.y, rotatedShape)) return new Pair(this.x, this.y);
            for (int dxy = 1; dxy <= this.maxLen; dxy++){
                if (dxy == 0) continue;
                for (Pair index: new Pair[]{
                        new Pair(dxy, 0),
                        new Pair(0, dxy),
                        new Pair(-dxy, 0),
                        new Pair(0, -dxy)})
                {
                    if (this.moveAvailable(this.x + index.x, this.y + index.y, rotatedShape)){
                        return new Pair(this.x + index.x, this.y + index.y);
                    }
                }
            }
            return new Pair(-1, -1);
        }

        public void rotate(){
            ArrayList<Pair> rotatedShape = new ArrayList<>();
            if (this.currState < this.blockStates){
                for (Pair index: blockIndices){
                    rotatedShape.add(new Pair(index.y, -index.x));
                }
            }
            else {
                for (Pair index: blockIndices){
                    rotatedShape.add(new Pair(index.x, index.y));
                }
                for (int i = 0; i < this.blockStates - 1; i++){
                    for (Pair index: rotatedShape){
                        int temp = index.x;
                        index.x = -index.y;
                        index.y = temp;
                    }
                }
            }
            if (this.moveAvailable(this.x, this.y, rotatedShape)){
                this.blockIndices = rotatedShape;
                this.currState = (this.currState % this.blockStates) + 1;
                this.defineShape();
            }
            else {
                Pair alternateLoc = offsetedPosition(rotatedShape);
                if (alternateLoc.x != -1){
                    this.x = alternateLoc.x;
                    this.y = alternateLoc.y;
                    this.blockIndices = rotatedShape;
                    this.currState = (this.currState % this.blockStates) + 1;
                    this.defineShape();
                }
            }
        }

        private int projectYPosition(){
            for (int yLoc = this.y; yLoc < ROWS; yLoc++){
                if (!this.moveAvailable(this.x, yLoc, this.blockIndices)){
                    return yLoc - 1;
                }
            }
            return ROWS - 1;
        }

        public void drawProjection(Graphics g){
            int yPosition = projectYPosition();
            g.setColor(Color.decode("#f6d6bd"));
            for (Pair index: blockIndices){
                g.drawRect((this.x + index.x)* GRIDW, (yPosition + index.y) * GRIDH, GRIDW, GRIDH);
            }

        }

        public void smackDown(){
            this.y = projectYPosition();
            this.place();
        }
    }

    private class GameMiniScreen extends JPanel{
        GameMiniScreen(int x, int y){
            this.setBackground(Color.decode("#816271"));
            this.setFocusable(true);
            this.setBounds(x, y, COLS * GRIDW, ROWS * GRIDH);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            blockQueue[0].drawBLock(g);
            blockQueue[0].drawProjection(g);
            g.setColor(Color.decode("#c3a38a"));
            for (int i = 0; i < ROWS; i++){
                for (int j = 0; j < COLS; j++){
                    if (gridMap[i][j]){
                        g.fillRect(j * GRIDW + 1, i * GRIDH + 1, GRIDW - 2, GRIDH - 2);
                    }
                }
            }
        }
    }

    private class NextBlockMiniScreen extends JPanel{
        private int rows, cols;
        int centerX, centerY;
        NextBlockMiniScreen(int x, int y, int _rows, int _cols){
            rows = _rows;
            cols = _cols;
            this.setBackground(Color.decode("#0f2a3f"));
            centerX = cols / 2;
            centerY = rows / 2;
            this.setFocusable(true);
            this.setBounds(x, y, this.cols * GRIDW, this.rows * GRIDH);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            g.setColor(Color.decode("#c3a38a"));
            Pair centerOffset = blockQueue[1].centerOffset;
            for (Pair index: blockQueue[1].blockIndices){
                g.fillRect((centerX + index.x + centerOffset.x) * GRIDW + 1,
                        (centerY + index.y + centerOffset.y) * GRIDH + 1,
                        GRIDW - 2, GRIDH - 2);
            }
        }
    }

    private class StatsMiniScreen extends JPanel{
        private int centerX, centerY;
        private Font defaultFont = new Font(null, Font.PLAIN, 20);
        StatsMiniScreen(int x, int y, int width, int height) {
            centerX = width / 2;
            centerY = height / 2;
            this.setBackground(Color.decode("#0f2a3f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            drawCenteredText(g, "Score: " + score, defaultFont, Color.decode("#c3a38a"),
                    centerX, centerY);
        }

    }

    int score;
    int timer;
    boolean isGameover;
    Font defaultFont = new Font(null, Font.PLAIN, 20);

    boolean[][] gridMap;
    GameFrame mainGame;
    GameMiniScreen gmScreen;
    NextBlockMiniScreen nextScreen;
    StatsMiniScreen statsScreen;
    IngameBlock[] blockQueue = new IngameBlock[2];
    ArrayList<TetrisBlock> chosenBlocks;

    GameScreen(GameFrame _mainGame) {
        mainGame = _mainGame;
        this.setLayout(null);
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.gmScreen = new GameMiniScreen(50 + (400 - COLS * 20) / 2, 50);
        this.add(this.gmScreen);
        this.nextScreen = new NextBlockMiniScreen(50, 500, 5, 5);
        this.add(this.nextScreen);
        this.statsScreen = new StatsMiniScreen(300, 500, 150, 100);
        this.add(this.statsScreen);
        this.setBackground(Color.decode("#20394f"));
        this.setFocusable(true);
        this.setupDefaultBLocks();
        this.startGame();
    }

    GameScreen(GameFrame _mainGame, ArrayList<BlockData> bLocksData) {
        mainGame = _mainGame;
        this.setLayout(null);
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.gmScreen = new GameMiniScreen(50 + (400 - COLS * 20) / 2, 50);
        this.add(this.gmScreen);
        this.nextScreen = new NextBlockMiniScreen(50, 500, 5, 5);
        this.add(this.nextScreen);
        this.statsScreen = new StatsMiniScreen(300, 500, 150, 100);
        this.add(this.statsScreen);
        this.setBackground(Color.decode("#20394f"));
        this.setFocusable(true);
        this.setupBLocks(bLocksData);
        this.startGame();
    }

    public void startGame(){
        score = 0;
        timer = 0;
        isGameover = false;
        gridMap = new boolean[ROWS][COLS];
        Random rand = new Random();
        blockQueue[0] = new IngameBlock(chosenBlocks.get(rand.nextInt(chosenBlocks.size())));
        blockQueue[1] = new IngameBlock(chosenBlocks.get(rand.nextInt(chosenBlocks.size())));
    }

    private void setupBLocks(ArrayList<BlockData> blocksData){
        chosenBlocks = new ArrayList<>();
        for (BlockData blockData: blocksData){
            chosenBlocks.add(new TetrisBlock(blockData.blockIndices, blockData.centerOffset));
        }
    }

    private void setupDefaultBLocks(){
        chosenBlocks = new ArrayList<>();

        ArrayList<Pair> blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(2, 0)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(-1, -1)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(1, -1)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(0, -1),
                new Pair(1, 0), new Pair(1, -1)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, -1), new Pair(0, -1)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, 0),
                new Pair(1, 0), new Pair(0, -1)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

        blockIndices = new ArrayList<>(Arrays.asList(new Pair(0, 0), new Pair(-1, -1),
                new Pair(0, -1), new Pair(1, 0)));
        chosenBlocks.add(new TetrisBlock(blockIndices));

    }

    public void postBlockPlacement() {
        timer = 0;
        blockQueue[0] = blockQueue[1];
        Random rand = new Random();
        blockQueue[1] = new IngameBlock(chosenBlocks.get(rand.nextInt(chosenBlocks.size())));
        if (!blockQueue[0].moveAvailable(blockQueue[0].x, blockQueue[0].y, blockQueue[0].blockIndices)){
            isGameover = true;
            this.callGameOver();
        }
        this.updateScore();
    }

    public void callGameOver() {
        mainGame.updateScore(this.score);
    }

    public void updateScore()
    {
        int totalClears = 0;
        for (int i = 0; i < ROWS; i++){
            boolean flag = true;
            for (int j = 0; j < COLS; j++){
                flag &= gridMap[i][j];
            }
            if (flag){
                totalClears++;
                for (int revIndex = i; revIndex > 0; revIndex--){
                    gridMap[revIndex] = gridMap[revIndex - 1];
                }
            }
            gridMap[0] = new boolean[COLS];
            for (int j = 0; j < COLS; j++){
                gridMap[0][j] = false;
            }
        }
        if (totalClears > 0){
            this.score += 40 * (Math.pow(3, totalClears - 1));
            //System.out.println(this.score);
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

    private void draw(Graphics g){
        g.setColor(Color.decode("#4e495f"));
        g.fillRect(50, 50, 400, 400);
        g.setColor(Color.decode("#0f2a3f"));
        g.drawRect(49, 49, 402, 402);
        //g.drawRect(49, 499, 102, 102);
        if (isGameover){
            this.drawCenteredText(g, "Game Over, press Space to restart", defaultFont,
                    Color.decode("#c2c2d1"), SCREEN_WIDTH / 2, 470);
        }

    }

    @Override
    public void gameLoop() {
        timer = (timer + 1) % 35;
        if (timer == 0 && !isGameover){
            blockQueue[0].move(0, 1);
        }
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch (key){
            case "UP":
                blockQueue[0].rotate();
                break;
            case "DOWN":
                blockQueue[0].move(0, 1);
                timer = 0;
                break;
            case "LEFT":
                blockQueue[0].move(-1, 0);
                break;
            case "RIGHT":
                blockQueue[0].move(1, 0);
                break;
            case "SPACE":
                if (!isGameover) blockQueue[0].smackDown();
                else this.startGame();
                break;
        }
    }
}
