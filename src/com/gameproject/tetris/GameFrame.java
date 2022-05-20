package com.gameproject.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
    //GameScreen gameStage;       //Temporary

    GameFrame(){
        this.createDirectories();
        this.setTitle("Tetris");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setFocusable(true);
        this.currDir = rootDir;
        this.currentStage = gameStages.get(currDir.toString());
        //Temporary part begins
        /*
        gameStage = new GameScreen(this);
        JPanel gamePanel = gameStage;
        currentStage = gameStage;
        */
        //Temporary part ends
        this.add(gamePanels.get(currDir.toString()));
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.addKeyListener(new MyKeyAdapter(this));
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
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

    }

    public void goPreviousDir(){
        DirNode nextDir = currDir.getParent();
        if (nextDir == null) return;
        this.getContentPane().remove(gamePanels.get(currDir.toString()));
        this.add(gamePanels.get(nextDir.toString()));
        currDir = nextDir;
        currentStage = gameStages.get(currDir.toString());
        this.getContentPane().invalidate();
        this.getContentPane().validate();
    }

    public void initializeGame(){
        GameScreen gameScreen = new GameScreen(this);
        gameStages.put("GameScreen", gameScreen);
        gamePanels.put("GameScreen", gameScreen);
    }

    public void setDirectory(String dirName){
         DirNode nextDir = currDir.getChildByName(dirName);
         if (nextDir == null) return;
         else if (gameStages.get(nextDir.toString()) == null || gamePanels.get(nextDir.toString()) == null) return;
         this.getContentPane().remove(gamePanels.get(currDir.toString()));
         this.add(gamePanels.get(nextDir.toString()));
         currDir = nextDir;
         currentStage = gameStages.get(currDir.toString());
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
