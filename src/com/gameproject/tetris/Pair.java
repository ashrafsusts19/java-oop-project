package com.gameproject.tetris;

public class Pair{
    public int x, y;
    Pair(int _first, int _second){
        x = _first;
        y = _second;
    }

    public boolean isGreater(Pair p){
        if (this.x > p.x || (this.x == p.x && this.y > p.y)) return true;
        return false;
    }
}
