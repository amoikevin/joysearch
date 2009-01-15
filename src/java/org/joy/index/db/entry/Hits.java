/*
 * KeywordsEntry.java
 *
 * Created on 2007年12月11日, 下午1:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db.entry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.group.service.JobResource;

/**
 *
 * @author 海
 */
public class Hits implements JobResource {
    
    private HashSet<Hit> hits = new HashSet<Hit>((int)(BEST_HITS_NUM*1.25));
    private final static int BEST_HITS_NUM =50;
//    public static void main(String[] args){
//        Hits h = new Hits();
//        h.setHit(new Hit("abc",new Integer[]{1,2,3}));
//        h.setHit(new Hit("def",new Integer[]{5,6,7}));
//        h.setHit(new Hit("gih",new Integer[]{8,9,10}));
//        h.setHit(new Hit("fdd",new Integer[]{10,11}));
//        System.out.println(h);
//        h.compact();
//        System.out.println(h);
//    }
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeInt(hits.size());
            for (Hit h : hits) {
                out.write(h.toBytes());
            }
            baos.close();
            out.close();
            return baos.toByteArray();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Hits(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            DataInputStream in = new DataInputStream(bais);
            int len = in.readInt();
            for (int j = 0; j < len; j++) {
                hits.add(new Hit(in));
            }
            in.close();
            bais.close();
        } catch (IOException ex) {
            Logger.getLogger(Hit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Hits(Hit[] hits) {
        setHits(hits);
    }
    
    public Hits() {
        
    }
    
    public Hit[] toArray() {
        return hits.toArray(new Hit[0]);
    }
    
    public Hit[] toArray(String[] keywords) {
        Vector<Hit> vecHits = new Vector<Hit>();
        for (String keyword : keywords) {
            vecHits.add(getHitByKeyword(keyword));
        }
        return vecHits.toArray(new Hit[0]);
    }
    
    public void setHits(Hit[] arrayHits) {
        for (Hit h : arrayHits) {
            setHit(h);
        }
    }
    
    public void combine(Hits hits) {
        setHits(hits.toArray());
    }
    
    public void setHit(Hit hit) {
        if (!hits.add(hit)) {
            //已经存在的Hit添加之
            Iterator<Hit> iter = hits.iterator();
            while (iter.hasNext()) {
                Hit h = iter.next();
                if (h.equals(hit)) {
                    h.addPos(hit.getPos());
                }
            }
        }
    }
    
    public Hit getHitByKeyword(String keyword) {
        //已经存在的Hit添加之
        Iterator<Hit> iter = hits.iterator();
        while (iter.hasNext()) {
            Hit h = iter.next();
            if (h.getKeyword().equals(keyword)) {
                return h;
            }
        }
        return null;
    }
    
    public void compact(){
        int i = 0;
        if(hits.size()<=BEST_HITS_NUM)
            return;
        Iterator<Hit> iter = hits.iterator();
        int []freqs = new int[hits.size()];
        while (iter.hasNext()) {
            Hit t = iter.next();
            freqs[i]=t.getFrequency();
            i++;
        }
        Arrays.sort(freqs);
        int min = freqs[BEST_HITS_NUM-1];
        iter = hits.iterator();
        
        while (iter.hasNext()) {
            Hit t = iter.next();
            if(t.getFrequency()<min){
                hits.remove(t);
                iter  = hits.iterator();
                continue;
            }
        }
        
    }
    
    public String toString() {
        String s = "";
        for (Hit h : hits.toArray(new Hit[0])) {
            s += h.toString();
            s += "\n";
        }
        return s;
    }
    public int size(){
        return hits.size();
    }
}
