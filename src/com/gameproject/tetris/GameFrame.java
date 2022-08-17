package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


class DirNode {
    private String nodeName;
    private DirNode parent;
    private boolean root;
    private ArrayList<DirNode> children;
    DirNode(String _name){
        nodeName = _name;
        parent = null;
        root = true;
        children = new ArrayList<>();
    }
    DirNode(String name, DirNode _parent){
        nodeName = name;
        parent = _parent;
        root = false;
        children = new ArrayList<>();
    }

    public DirNode addChild(String childName){
        children.add(new DirNode(childName, this));
        return children.get(children.size() - 1);
    }

    public DirNode getChildByName(String childName){
        for (int i = 0; i < this.children.size(); i++){
            if (children.get(i).toString() == childName){
                return children.get(i);
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return this.nodeName;
    }

    public DirNode getParent(){
        if (root) return null;
        return this.parent;
    }

    public boolean isRoot(){
        return this.root;
    }

    public ArrayList<DirNode> getChildren(){
        return this.children;
    }
}

public class GameFrame extends JFrame implements GameStage, ActionListener {
    public class MyKeyAdapter extends KeyAdapter {
        GameFrame mainGame;
        MyKeyAdapter(GameFrame _mainGame) {
            super();
            mainGame = _mainGame;
        }
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_UP:
                    mainGame.keyPressed("UP");
                    break;
                case KeyEvent.VK_DOWN:
                    mainGame.keyPressed("DOWN");
                    break;
                case KeyEvent.VK_LEFT:
                    mainGame.keyPressed("LEFT");
                    break;
                case KeyEvent.VK_RIGHT:
                    mainGame.keyPressed("RIGHT");
                    break;
                case KeyEvent.VK_ENTER:
                    mainGame.keyPressed("ENTER");
                    break;
                case KeyEvent.VK_ESCAPE:
                    mainGame.keyPressed("ESCAPE");
                    break;
                case KeyEvent.VK_SPACE:
                    mainGame.keyPressed("SPACE");
                    break;
            }
        }
    }

    Timer timer;
    boolean running = false;
    HashMap<String, GameStage> gameStages;
    HashMap<String, JPanel> gamePanels;
    GameStage currentStage;
    DirNode rootDir, currDir;
    File saveFileBlocks;
    ArrayList<String> saveDataBlocks;
    File saveFileScore;
    ArrayList<Integer> saveDataScore;

    GameFrame(){
        this.loadSaveFiles();
        this.createDirectories();
        this.setTitle("Tetris");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setFocusable(true);
        this.currDir = rootDir;
        this.currentStage = gameStages.get(currDir.toString());
        this.add(gamePanels.get(currDir.toString()));
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.addKeyListener(new MyKeyAdapter(this));
        this.running = true;
        this.timer = new Timer(DELAY, this);
        this.timer.start();
    }

    private void loadSaveFiles(){
        this.saveFileBlocks = new File("CustomBlocks.txt");
        this.saveDataBlocks = new ArrayList<>();
        if (!this.saveFileBlocks.isFile()){
            try {
                this.saveFileBlocks.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                BufferedReader saveFileReader = new BufferedReader(new FileReader(this.saveFileBlocks));
                String line;
                while((line = saveFileReader.readLine()) != null){
                    this.saveDataBlocks.add(line);
                }
                saveFileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.saveFileScore = new File("ScoreRecords.txt");
        this.saveDataScore = new ArrayList<>();
        if (!this.saveFileScore.isFile()){
            try {
                this.saveFileScore.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++){
                saveDataScore.add(0);
            }
            this.saveScoreData();
        }
        else {
            try {
                BufferedReader saveFileReader = new BufferedReader(new FileReader(this.saveFileScore));
                String line;
                while((line = saveFileReader.readLine()) != null){
                    this.saveDataScore.add(Integer.parseInt(line.strip()));
                    if (saveDataScore.size() >= 10){
                        break;
                    }
                }
                saveFileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e){
                saveDataScore = new ArrayList<>();
                for (int i = 0; i < 10; i++){
                    saveDataScore.add(0);
                }
            }
            while (saveDataScore.size() < 10){
                saveDataScore.add(0);
            }
        }
        Collections.sort(saveDataScore, Collections.reverseOrder());
    }

    public void saveBlockData() {
        try {
            BufferedWriter saveFileWriter = new BufferedWriter(new FileWriter(this.saveFileBlocks));
            for (String line: saveDataBlocks){
                saveFileWriter.write(line + System.lineSeparator());
            }
            saveFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveScoreData() {
        try {
            BufferedWriter saveFileWriter = new BufferedWriter(new FileWriter(this.saveFileScore));
            for (int i =0; i < saveDataScore.size(); i++){
                String line = saveDataScore.get(i) + "";
                saveFileWriter.write(line + System.lineSeparator());
            }
            saveFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateScore(int score){
        for (int i = 0; i < saveDataScore.size(); i++){
            if (saveDataScore.get(i) < score){
                for (int j = saveDataScore.size() - 1; j > i; j--){
                    saveDataScore.set(j, saveDataScore.get(j - 1));
                }
                saveDataScore.set(i, score);
                break;
            }
        }
        this.saveScoreData();
    }

    private void createDirectories(){
        gameStages = new HashMap<>();
        gamePanels = new HashMap<>();

        rootDir = new DirNode("MainMenu");
        MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
        gameStages.put("MainMenu", mainMenuScreen);
        gamePanels.put("MainMenu", mainMenuScreen);

        rootDir.addChild("GameScreen");
        gameStages.put("GameScreen", null);
        gamePanels.put("GameScreen", null);

        rootDir.addChild("RecordScreen");
        RecordsScreen recordsScreen = new RecordsScreen(this);
        gameStages.put("RecordScreen", recordsScreen);
        gamePanels.put("RecordScreen", recordsScreen);

        rootDir.addChild("SettingsScreen");
        SettingsScreen settingsScreen = new SettingsScreen(this);
        gameStages.put("SettingsScreen", settingsScreen);
        gamePanels.put("SettingsScreen", settingsScreen);

        DirNode dir = rootDir.addChild("CustomMenuScreen");
        CustomMenuScreen customMenuScreen = new CustomMenuScreen(this);
        gameStages.put("CustomMenuScreen", customMenuScreen);
        gamePanels.put("CustomMenuScreen", customMenuScreen);

        dir.addChild("BlockBuilderScreen");
        BlockBuilderScreen blockBuilderScreen = new BlockBuilderScreen(this);
        gameStages.put("BlockBuilderScreen", blockBuilderScreen);
        gamePanels.put("BlockBuilderScreen", blockBuilderScreen);

        dir.addChild("GameScreen");
    }

    public void initializeGame(){
        GameScreen gameScreen = new GameScreen(this);
        gameStages.put("GameScreen", gameScreen);
        gamePanels.put("GameScreen", gameScreen);
    }

    public void initializeCustorGame(ArrayList<BlockData> blocksData){
        GameScreen gameScreen = new GameScreen(this, blocksData);
        gameStages.put("GameScreen", gameScreen);
        gamePanels.put("GameScreen", gameScreen);
    }

    public void initializeEditor(BlockData blockData){
        BlockBuilderScreen blockBuilderScreen;
        if (blockData == null) {
            blockBuilderScreen = new BlockBuilderScreen(this);
        }
        else {
            blockBuilderScreen = new BlockBuilderScreen(this, blockData);
        }
        gameStages.put("BlockBuilderScreen", blockBuilderScreen);
        gamePanels.put("BlockBuilderScreen", blockBuilderScreen);
    }

    public void setDirectory(String dirName){
         DirNode nextDir = currDir.getChildByName(dirName);
         if (nextDir == null) return;
         else if (gameStages.get(nextDir.toString()) == null || gamePanels.get(nextDir.toString()) == null) return;
         this.getContentPane().remove(gamePanels.get(currDir.toString()));
         this.add(gamePanels.get(nextDir.toString()));
         currDir = nextDir;
         currentStage = gameStages.get(currDir.toString());
         currentStage.refresh();
         this.getContentPane().invalidate();
         this.getContentPane().validate();
    }

    public void goPreviousDir(){
        DirNode nextDir = currDir.getParent();
        if (nextDir == null) return;
        this.getContentPane().remove(gamePanels.get(currDir.toString()));
        this.add(gamePanels.get(nextDir.toString()));
        currDir = nextDir;
        currentStage = gameStages.get(currDir.toString());
        currentStage.refresh();
        this.getContentPane().invalidate();
        this.getContentPane().validate();
    }

    @Override
    public void gameLoop() {
        currentStage.gameLoop();
    }

    @Override
    public void keyPressed(String key) {
        if (key == "ESCAPE"){
            this.goPreviousDir();
            return;
        }
        currentStage.keyPressed(key);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            this.gameLoop();
        }
    }
}
