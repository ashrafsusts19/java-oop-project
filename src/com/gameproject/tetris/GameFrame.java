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
    @Override
    public String toString() {
        return this.nodeName;
    }

    public DirNode getParent(){
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
            }
        }
    }

    Timer timer;
    boolean running = false;
    HashMap<String, GameStage> gameStages;
    HashMap<String, JPanel> gamePanels;
    GameStage currentStage;
    DirNode rootDir, currDir;
    GameScreen gameStage;       //Temporary

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

    void createDirectories(){
        gameStages = new HashMap<>();
        gamePanels = new HashMap<>();

        rootDir = new DirNode("MainMenu");
        MainMenu mainMenu = new MainMenu(this);
        gameStages.put("MainMenu", mainMenu);
        gamePanels.put("MainMenu", mainMenu);

        rootDir.addChild("GameScreen");
        GameScreen gameScreen = new GameScreen(this);
        gameStages.put("GameScreen", gameScreen);
        gamePanels.put("GameScreen", gameScreen);

    }

    @Override
    public void gameLoop() {
        currentStage.gameLoop();
    }

    @Override
    public void keyPressed(String key) {
        currentStage.keyPressed(key);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            this.gameLoop();
        }
    }
}
