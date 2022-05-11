package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.min;


public class GameScreen extends JPanel implements GameStage{
    int GRIDW = 20, GRIDH = 20;
    int ROWS = 20, COLS = 10;
    class IngameBlock {
        ArrayList<Pair> blockIndices;
        int x, y;
        int blockStates;
        int maxLen;
        int currState = 1;
        int xLeft, xRight, yTop, yBot;

        IngameBlock(TetrisBlock blockData){
            blockIndices = new ArrayList<>();
            int miny = 99999;
            for (Pair blockIndex: blockData.blockIndices){
                blockIndices.add(new Pair(blockIndex.x, blockIndex.y));
                miny = min(miny, blockIndex.y);
            }
            x = gridMap[0].length / 2 - 1;
            y = -miny;
            blockStates = blockData.blockStates;
            maxLen = blockData.maxLen;
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
            g.setColor(Color.decode("#4a00a4"));
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
            if (cy >= gmScreen.rows || cy < 0 || cx >= gmScreen.cols || cx < 0) return false;
            for (Pair index: blockShape){
                if (cy + index.y >= gmScreen.rows || cy + index.y < 0) return false;
                else if (cx + index.x >= gmScreen.cols || cx + index.x < 0) return false;
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
            g.setColor(Color.decode("#6200e3"));
            for (Pair index: blockIndices){
                g.drawRect((this.x + index.x)* GRIDW, (yPosition + index.y) * GRIDH, GRIDW, GRIDH);
            }

        }

        public void smackDown(){
            this.y = projectYPosition();
            this.place();
        }
    }

    class GameMiniScreen extends JPanel{
        public int rows, cols;
        GameMiniScreen(int x, int y, int _rows, int _cols){
            rows = _rows;
            cols = _cols;
            this.setBackground(Color.decode("#190025"));
            this.setFocusable(true);
            this.setBounds(x, y, cols * GRIDW, rows * GRIDH);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        public void draw(Graphics g){
            blockQueue[0].drawBLock(g);
            blockQueue[0].drawProjection(g);
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++){
                    if (gridMap[i][j]){
                        g.fillRect(j * GRIDW + 1, i * GRIDH + 1, GRIDW - 2, GRIDH - 2);
                    }
                }
            }
        }
    }

    int score;
    int timer;
    boolean isGameover;
    Font font = new Font(null, Font.PLAIN, 20);

    boolean[][] gridMap;
    GameFrame mainGame;
    GameMiniScreen gmScreen;
    IngameBlock[] blockQueue = new IngameBlock[2];
    ArrayList<TetrisBlock> chosenBlocks;

    GameScreen(GameFrame _mainGame) {
        mainGame = _mainGame;
        this.setLayout(null);
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.gmScreen = new GameMiniScreen(50 + (400 - COLS * 20) / 2, 50, ROWS, COLS);
        this.add(gmScreen);
        this.setBackground(Color.decode("#311a30"));
        this.setFocusable(true);
        this.setupBLocks();
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

    private void setupBLocks(){
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
        }
        this.updateScore();
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
            for (int j = 0; j < COLS; j++){
                gridMap[0][j] = false;
            }
        }
        if (totalClears > 0){
            this.score += 40 * (Math.pow(3, totalClears - 1));
            System.out.println(this.score);
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    private void draw(Graphics g){
        g.setColor(Color.decode("#3e2347"));
        g.fillRect(50, 50, 400, 400);
        g.setColor(Color.decode("#141414"));
        g.drawRect(49, 49, 402, 402);
        gmScreen.repaint();
    }

    @Override
    public void gameLoop() {
        timer = (timer + 1) % 35;
        if (timer == 0 && !isGameover){
            blockQueue[0].move(0, 1);
        }
        this.gmScreen.repaint();
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
                blockQueue[0].smackDown();
                break;
        }
    }
}
