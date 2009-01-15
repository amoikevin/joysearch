/*
 * Main.java
 *
 * Created on 2007年11月25日, 下午8:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.rank.core;

import org.joy.rank.core.BigBoolArray;
import org.joy.rank.core.PageRank;

/**
 *
 * @author 海
 */
public class Main2 {

    /** Creates a new instance of Main */
    public Main2() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PageRank pageRank = new PageRank();
        pageRank.setBeta(0.5f);
        pageRank.setPrecision(0.000000001f);
        // initial the array
        int NodesNum = 3;
        BigBoolArray relations = new BigBoolArray(NodesNum * NodesNum);
        relations.set(0, false);
        relations.set(1, true);
        relations.set(2, true);
        relations.set(3, false);
        relations.set(4, false);
        relations.set(5, true);
        relations.set(6, true);
        relations.set(7, false);
        relations.set(8, false);
        pageRank.setLinkArray(relations, 3);
        pageRank.run();
    }
}
