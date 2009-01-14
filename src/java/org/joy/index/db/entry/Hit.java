/*
 * Hit.java
 *
 * Created on 2007年12月11日, 下午1:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db.entry;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 海
 */
public class Hit implements Serializable {

    private TreeSet<Pos> pos;
    private String keyword;

    public CompactHit toCompactHit(String URL) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeInt(getWeight());
            out.writeInt(URL.hashCode());
            for (Pos p : pos) {
                out.writeInt(p.getPos());
            }
            baos.close();
            out.close();
            return new CompactHit(baos.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(Hit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] toBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF(keyword);
            out.writeInt(getFrequency());
            for (Pos p : pos) {
                out.write(p.toBytes());
            }
            baos.close();
            out.close();
            return baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Hit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Hit(DataInputStream in) {
        try {
            keyword = in.readUTF();
            int len = in.readInt();
            pos = new TreeSet<Pos>();
            for (int j = 0; j < len; j++) {
                pos.add(new Pos(in));
            }
        } catch (IOException ex) {
            Logger.getLogger(Hit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Hit(String keyword, Pos[] pos) {
        setPos(pos);
        setKeyword(keyword);
    }

//    public Hit(int hash, Pos[] pos) {
//        setPos(pos);
//        this.hash = hash;
//    }
    public Collection<Pos> getPos() {
        return pos;
    }

    public Integer[] getPositions() {
        Pos[] p = pos.toArray(new Pos[0]);
        Integer[] iPos = new Integer[p.length];
        for (int i = 0; i < iPos.length; i++) {
            iPos[i] = (int) p[i].getPos();
        }
        return iPos;
    }

    public void setPos(Pos[] pos) {
        this.pos = new TreeSet<Pos>(Arrays.asList(pos));
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getFrequency() {
        return pos.size();
    }

    public void addPos(Pos[] newPos) {
        for (Pos p : newPos) {
            pos.add(p);
        }
    }

    public void addPos(Collection<Pos> newPos) {
        pos.addAll(newPos);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Hit other = (Hit) obj;
        if (this.keyword != other.keyword && (this.keyword == null || !this.keyword.equals(other.keyword))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Keyword:" + keyword;
        for (Pos p : pos.toArray(new Pos[0])) {
            s += "\nPos:" + p.getPos();
        }
        return s;
    }

    public void removeNegPos() {
        pos = (TreeSet<Pos>) pos.subSet(new Pos((short) 0, (byte) 1), new Pos((short) 4096, (byte) 1));
    }

    public static void main(String[] args) {
//        TreeSet set = new TreeSet();
//        set.add(1);set.add(2);set.add(3);
//        set = set.subSet(0, 2);
//        System.out.println(set);
    }
    public static final int REF_WEIGHT = 10000;
    public static final int TITLE_WEIGHT = 200000;

    public int getWeight() {
        double weight = 1.0f;
        int scale = (int) 10e5;
        if (pos != null) {
            for (Pos p : pos) {
                weight += p.getWeight() * scale;
            }
        }
        return (int) weight;
//        for (Pos p : pos) {
//            if (p.getPos() < 0) {
//                weight += p.getLevel() *(1-(float) 50/4096);
//            } else {
//                weight += p.getLevel() * (1- (4096 - (p.getPos() + 1))/(float)4096);
//            }
//        }
//        return (int)(weight*100.0f);
    }
}
