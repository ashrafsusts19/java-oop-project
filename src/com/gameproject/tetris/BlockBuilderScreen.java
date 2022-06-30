package com.gameproject.tetris;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

public class BlockBuilderScreen extends JPanel implements GameStage {

    class BLockBuilder extends JPanel implements MouseListener{
        private int gridWidth, gridHeight;
        private int margin;
        private boolean isMouseIn;
        BLockBuilder (int x, int y, int width, int height){
            this.addMouseListener(this);
            this.setBackground(Color.decode("#0f2a3f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
            this.gridWidth = this.getWidth() / cols;
            this.gridHeight = this.getHeight() / rows;
            this.margin = 1;
            this.isMouseIn = false;
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        private void draw(Graphics g){
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++){
                    if (blockMap[i][j]){
                        if (centerBlock.x == j && centerBlock.y == i){
                            g.setColor(Color.decode("#f6d6bd"));
                        }
                        else {
                            g.setColor(Color.decode("#c3a38a"));
                        }
                        g.fillRect(j * this.gridWidth + this.margin, i * this.gridHeight + this.margin,
                                this.gridWidth - 2 * this.margin, this.gridHeight - 2 * this.margin);
                    }
                }
            }
            if (this.getMousePosition() != null){
                Point mousePosition = this.getMousePosition();
                int col, row;
                col = mousePosition.x / this.gridWidth;
                row = mousePosition.y / this.gridHeight;
                //System.out.println("x: " + mousePosition.x + " y: " + mousePosition.y);
                g.setColor(Color.decode("#f6d6bd"));
                g.drawRect(col * this.gridWidth, row * this.gridHeight, this.gridWidth - 1,
                        this.gridHeight - 1);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int x, y;
            x = e.getX();
            y = e.getY();
            int rowNo, colNo;
            colNo = x / this.gridWidth;
            rowNo = y / this.gridHeight;
            if (rowNo >= rows || rowNo < 0 || colNo >= cols || colNo < 0){
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON1){
                //If Left Button is Pressed
                if (centerBlock.x != colNo || centerBlock.y != rowNo){
                    blockMap[rowNo][colNo] = !blockMap[rowNo][colNo];
                }
            }
            else if (e.getButton() == MouseEvent.BUTTON3){
                //If Right Button is Pressed
                blockMap[rowNo][colNo] = true;
                centerBlock.x = colNo;
                centerBlock.y = rowNo;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    class MenuMiniScreen extends JPanel{
        public int cursorPosition;
        public ArrayList<String> options;
        private Font optionFont = new Font(null, Font.PLAIN, 25);
        MenuMiniScreen(int x, int y, int width, int height) {
            this.cursorPosition = 0;
            this.setupOptions();
            this.setBackground(Color.decode("#0f2a3f"));
            this.setFocusable(true);
            this.setBounds(x, y, width, height);
        }

        private void setupOptions(){
            this.options = new ArrayList<>();
            Collections.addAll(this.options, "Change Name", "Save", "Exit");
        }

        public void cursorDecrement(){
            this.cursorPosition = (this.cursorPosition - 1 + this.options.size()) % this.options.size();
        }

        public void cursorIncrement(){
            this.cursorPosition = (this.cursorPosition + 1) % this.options.size();
        }

        public String getCurrentOption(){
            return this.options.get(this.cursorPosition);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.draw(g);
        }

        private void draw(Graphics g){
            drawCenteredText(g, blockName, optionFont, Color.decode("#816271"),
                    this.getWidth() / 2, 20);
            for (int i = 0; i < options.size(); i++){
                if (i == cursorPosition) {
                    drawCenteredText(g, options.get(i), optionFont, Color.decode("#f6d6bd"),
                            this.getWidth() / 2, 60 + i * 30);
                }
                else {
                    drawCenteredText(g, options.get(i), optionFont, Color.decode("#4e495f"),
                            this.getWidth() / 2, 60 + i * 30);
                }
            }
        }
    }

    class InputHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String newName = nameTextField.getText();
            if (checkNameValidity(newName)){
                blockName = newName;
            }
            nameTextField.setFocusable(false);
        }

        private boolean checkNameValidity(String name){
            if (name.length() == 0){
                displayError("Name Can't be Empty");
                return false;
            }
            else if (name.length() > 9){
                displayError("Length Limit Exceeded");
                return false;
            }
            for (int i = 0; i < name.length(); i++){
                char c = name.charAt(i);
                if ((c < '0' || c > '9') && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z')){
                    displayError("Alphanumeric Only");
                    return false;
                }
            }
            return true;
        }
    }

    GameFrame mainGame;
    boolean[][] blockMap;
    boolean isNewBlock;
    String previousName;
    String[] fixedNames = {"I", "J", "L", "O", "S", "T", "Z"};
    Pair centerBlock;
    int rows = 5, cols = 5;
    BLockBuilder bLockBuilder;
    MenuMiniScreen menuMiniScreen;
    String blockName;
    JTextField nameTextField;
    InputHandler inputHandler;
    BlockBuilderScreen (GameFrame _mainGame) {
        this.mainGame = _mainGame;
        this.bLockBuilder = new BLockBuilder(50, 50, 400, 400);
        this.add(this.bLockBuilder);
        this.menuMiniScreen = new MenuMiniScreen(50, 500, 200, 150);
        this.add(this.menuMiniScreen);
        this.setupTextField();
        this.initializeNewBlock();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.decode("#20394f"));
        this.setFocusable(true);
    }

    BlockBuilderScreen (GameFrame _mainGame, BlockData blockData) {
        this.mainGame = _mainGame;
        this.bLockBuilder = new BLockBuilder(50, 50, 400, 400);
        this.add(this.bLockBuilder);
        this.menuMiniScreen = new MenuMiniScreen(50, 500, 200, 150);
        this.add(this.menuMiniScreen);
        this.setupTextField();
        this.initializeExistingBlock(blockData);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.decode("#20394f"));
        this.setFocusable(true);
    }

    private void initializeNewBlock(){
        this.isNewBlock = true;
        this.previousName = null;
        this.blockName = "NewBlock";
        this.blockMap = new boolean[this.rows][this.cols];
        this.blockMap[this.rows / 2][this.cols / 2] = true;
        this.centerBlock = new Pair(this.cols / 2, this.rows / 2);
        this.nameTextField.setText(this.blockName);
    }

    private void initializeExistingBlock(BlockData blockData){
        this.isNewBlock = false;
        this.previousName = blockData.name;
        this.blockName = blockData.name;
        this.blockMap = new boolean[this.rows][this.cols];
        this.centerBlock = new Pair((this.cols / 2) + blockData.centerOffset.x,
                (this.rows / 2) + blockData.centerOffset.y);
        for (Pair blockIndex: blockData.blockIndices){
            int y = (this.rows / 2) + blockData.centerOffset.y + blockIndex.y;
            int x = (this.cols / 2) + blockData.centerOffset.x + blockIndex.x;
            this.blockMap[y][x] = true;
        }
        this.nameTextField.setText(this.blockName);
    }

    private void setupTextField() {
        this.inputHandler = new InputHandler();
        this.nameTextField = new JTextField();
        this.nameTextField.setBackground(Color.decode("#4e495f"));
        this.nameTextField.setForeground(Color.decode("#f6d6bd"));
        this.nameTextField.setBounds(275, 550, 175, 40);
        this.nameTextField.setFocusable(false);
        this.nameTextField.setFont(new Font(null, Font.PLAIN, 15));
        this.nameTextField.setHorizontalAlignment(SwingConstants.CENTER);
        this.nameTextField.addActionListener(inputHandler);
        this.add(nameTextField);
    }

    private void displayError(String errorText){
        Font font = new Font(null, Font.BOLD, 15);
        JLabel errorlabel = new JLabel(errorText);
        errorlabel.setOpaque(true);
        errorlabel.setForeground(Color.decode("#f6d6bd"));
        errorlabel.setBackground(Color.decode("#0f2a3f"));
        errorlabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorlabel.setFont(font);
        errorlabel.setBounds(275, 500, 175, 30);
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

    private void saveData(){
        for (String fixedName: fixedNames){
            if (this.blockName == fixedName){
                this.displayError("Name not available!");
                return;
            }
        }
        ArrayList<String> saveDataBlocks = mainGame.saveDataBlocks;
        int replaceIndex = -1;
        for (int i = 0; i < saveDataBlocks.size(); i++){
            if (saveDataBlocks.get(i).startsWith(this.blockName)){
                if (this.isNewBlock || this.previousName != this.blockName){
                    this.displayError("Name must be unique!");
                    return;
                }
                else {
                    replaceIndex = i;
                }
            }
            else if (!this.isNewBlock && saveDataBlocks.get(i).startsWith(this.previousName)) {
                replaceIndex = i;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.rows; i++){
            for (int j = 0; j < this.cols; j++) {
                if (this.blockMap[i][j]){
                    stringBuilder.append("1");
                }
                else {
                    stringBuilder.append("0");
                }
            }
        }
        String encodedBlock = this.blockName + "-" + this.centerBlock.x + " " + this.centerBlock.y + "-" +
                stringBuilder.toString();
        if (replaceIndex != -1){
            saveDataBlocks.set(replaceIndex, encodedBlock);
        }
        else {
            saveDataBlocks.add(encodedBlock);
        }
        mainGame.saveBlockData();
        this.previousName = this.blockName;
        this.isNewBlock = false;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }


    private void draw(Graphics g){

    }

    public void drawCenteredText(Graphics g, String text, Font font, Color color, int x, int y){
        FontMetrics metrics = g.getFontMetrics(font);
        x = x - metrics.stringWidth(text) / 2;
        y = y - metrics.getHeight() / 2;
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y + metrics.getHeight());

    }

    @Override
    public void gameLoop() {
        this.repaint();
    }

    @Override
    public void keyPressed(String key) {
        switch (key){
            case "UP":
                this.menuMiniScreen.cursorDecrement();
                break;
            case "DOWN":
                this.menuMiniScreen.cursorIncrement();
                break;
            case "ENTER":
                switch (this.menuMiniScreen.getCurrentOption()) {
                    case "Change Name":
                        this.nameTextField.setFocusable(true);
                        this.nameTextField.requestFocus();
                        break;
                    case "Save":
                        this.saveData();
                        break;
                    case "Exit":
                        this.mainGame.goPreviousDir();
                        break;
                }
                break;

        }
    }

}
