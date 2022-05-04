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
    ArrayList<DirNode> children;
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

    DirNode getParent(){
        return this.parent;
    }

    boolean isRoot(){
        return this.root;
    }
}

public class GameFrame extends JFrame implements GameStage, ActionListener {
    public class MyKeyAdapter extends KeyAdapter {
        GameFrame mainGame;
        MyKeyAdapter(GameFrame _mainGame) {
            mainGame = _mainGame;
        }
        @Override
        public void keyPressed(KeyEvent e){
            mainGame.keyPressed("key is pressed");
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
        this.createDirectoryTree();
        this.setTitle("Tetris");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.currDir = rootDir;
        //Temporary part begins
        gameStage = new GameScreen(this);
        JPanel gamePanel = gameStage;
        currentStage = gameStage;
        //Temporary part ends
        this.add(gamePanel);
        this.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setVisible(true);
        this.addKeyListener(new MyKeyAdapter(this));
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    void createDirectoryTree(){
        rootDir = new DirNode("MainMenu");
        currDir = rootDir.addChild("GameScreen");

    }

    @Override
    public void gameLoop() {
        currentStage.gameLoop();
    }

    @Override
    public void keyPressed(String key) {
        System.out.println(key);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            this.gameLoop();
        }
    }
}
