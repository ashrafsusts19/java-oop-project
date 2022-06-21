package com.gameproject.tetris;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.max;


public class TetrisBlock {
    public ArrayList<Pair> blockIndices;
    public int blockStates;
    public int maxLen = 0;

    TetrisBlock(ArrayList<Pair> _blockIndices){
        blockIndices = (ArrayList) _blockIndices.clone();
        findStates();
        for (Pair blockIndex: blockIndices){
            maxLen = max(maxLen, max(abs(blockIndex.y), abs(blockIndex.x)));
        }
    }

    private void sortPairs(ArrayList<Pair> pairs){
        for (int i = 1; i < pairs.size(); i++){
            Pair value = pairs.get(i);
            int hole = i;
            while (hole > 0 && pairs.get(hole - 1).isGreater(value)){
                pairs.set(hole, pairs.get(hole - 1));
                hole--;
            }
            pairs.set(hole, value);
        }
    }

    private void findStates(){
        ArrayList<Pair> indices = new ArrayList<>();
        Pair lowest = new Pair(99999, 99999);
        for (Pair blockIndex : blockIndices) {
            Pair tempPair;
            tempPair = blockIndex;
            indices.add(new Pair(tempPair.x, tempPair.y));
            if (tempPair.y < lowest.y || (tempPair.y == lowest.y && tempPair.x < lowest.x)) {
                lowest.y = tempPair.y;
                lowest.x = tempPair.x;
            }
        }
        ArrayList<Pair> mainVariation = new ArrayList<>();
        for (Pair eachPair: blockIndices){
            mainVariation.add(new Pair(eachPair.x - lowest.x, eachPair.y - lowest.y));
        }
        sortPairs(mainVariation);
        for (int i = 0; i < 4; i++){
            Pair rotLowest = new Pair(99999, 99999);
            for (Pair p: indices){
                int temp = p.x;
                p.x = p.y;
                p.y = -temp;
                if (p.y < rotLowest.y || (p.y == rotLowest.y && p.x < rotLowest.x)) {
                    rotLowest.y = p.y;
                    rotLowest.x = p.x;
                }
            }
            ArrayList<Pair> rotVariation = new ArrayList<>();
            for (Pair eachPair: indices){
                rotVariation.add(new Pair(eachPair.x - rotLowest.x, eachPair.y - rotLowest.y));
            }
            sortPairs(rotVariation);
            boolean isIsomorphic = true;
            for (int ii = 0; ii < mainVariation.size(); ii++){
                if (mainVariation.get(ii).y != rotVariation.get(ii).y || mainVariation.get(ii).x != rotVariation.get(ii).x){
                    isIsomorphic = false;
                    break;
                }
            }
            if (isIsomorphic){
                this.blockStates = i + 1;
                return;
            }
        }
    }
}

