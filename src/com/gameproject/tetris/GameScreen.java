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
    class IngameBlock {
        ArrayList<Pair> blockIndices;
        int x, y;
        int blockStates;
        int maxLen;
        int currState = 1;

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
        }

        private boolean moveAvailable(int cx, int cy, ArrayList<Pair> blockShape) {
            if (cy >= gmScreen.rows || cx >= gmScreen.cols) return false;
            for (Pair index: blockShape){
                if (cy + index.y >= gmScreen.rows || cy + index.y < 0) return false;
                else if (cx + index.x >= gmScreen.cols || cx + index.x < 0) return false;
                else if (gridMap[cy + index.y][cx + index.x]) return false;
            }
            return true;
        }

        private Pair alternateRotate(ArrayList<Pair> rotatedShape){
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
            }
            else {
                Pair alternateLoc = alternateRotate(rotatedShape);
                if (alternateLoc.x != -1){
                    this.x = alternateLoc.x;
                    this.y = alternateLoc.y;
                    this.blockIndices = rotatedShape;
                    this.currState = (this.currState % this.blockStates) + 1;
                }
            }
        }

        private int projectY(){
            return gridMap.length - 1;
        }

        public void drawProjection(Graphics g){
            g.setColor(Color.decode("#6200e3"));

        }

        public void smackDown(){

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
        timer = (timer + 1) % 40;
        this.gmScreen.repaint();
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch (key){

        }
    }
}
