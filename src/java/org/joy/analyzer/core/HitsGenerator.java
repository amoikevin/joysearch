/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import org.joy.index.db.entry.Hit;
import org.joy.index.db.entry.Hits;
import org.joy.index.db.entry.Pos;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Lamfeeling
 */
public class HitsGenerator {

    //负索引表
    //private static Hashtable<Integer, Integer> negTable = new Hashtable<Integer, Integer>();
    private String text;
    private String destURL;

    public HitsGenerator(String text) {
        this.text = text;
    }

    public HitsGenerator(String text, String destURL) {
        this.text = text;
        this.destURL = destURL;
    }

    public int getNoisePos(String destURL, String text) {
//        int noisePos = 0;
//        Integer negPos = 
        return -Math.abs((new Random()).nextInt());

//        if (negPos != null) {
//            if (negPos < Integer.MIN_VALUE + text.length()) {
//                noisePos = (short) (-1 - text.length());
//                negTable.put(destURL.hashCode(), noisePos);
//                System.out.println(destURL + "有了太多连接关键字");
//                System.out.println("Opps");
//            } else {
//                noisePos = negPos - text.length();
//                negTable.put(destURL.hashCode(), noisePos);
//            }
//        } else {
//            noisePos = -text.length();
//            negTable.put(destURL.hashCode(), noisePos);
//        }
//        return noisePos;
    }

    public Hits genHits(String[] words, int pWeight, int begin, int end) {
        ArrayList<Hit> hitLst = new ArrayList<Hit>();
        //设置噪声扰动，当引用链接时，噪声位置为负
        int noisePos = 0;
        if (destURL != null) {
            noisePos = getNoisePos(destURL, text);
        }
        
        //分析关键字、频率
        for (String word : words) {
            if (word.trim().equals("")) {
                continue;
            }

            Vector<Pos> vecHitPos = new Vector<Pos>();

            int hitPos;
            hitPos = text.toLowerCase().indexOf(word, begin);
            while (hitPos != -1 && hitPos <= end) {
                //关键字太多就不是关键字了
                if (vecHitPos.size() > 32 || hitPos > 4096) {
                    break;
                }
                Pos p = new Pos((hitPos + noisePos),
                        noisePos == 0 ? pWeight * (1 - hitPos / (float) text.length()) : //非引用的权重算法
                        pWeight); //引用的权重算法

                vecHitPos.add(p);
                hitPos = text.toLowerCase().indexOf(word, hitPos + 1);
            }
            //添加
            if (vecHitPos.size() != 0) {
                hitLst.add(new Hit(word, vecHitPos.toArray(new Pos[0])));
            }
        }
        if (hitLst.size() != 0) {
            return new Hits(hitLst.toArray(new Hit[0]));
        } else {
            return null;
        }
    }

    public Hits genHits(String[] words, int pWeight) {
        return genHits(words, pWeight, 0, text.length());
    }
}   
