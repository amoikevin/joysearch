/*
 * CompactHit.java
 *
 * Created on 2008年3月17日, 下午12:28
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
import java.net.URL;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class CompactHit {

    private byte[] data;

    public CompactHit(byte[] data) {
        this.data = data;
    }

    public int getInt(int offset) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(getData(), offset, 4);
        DataInputStream dis = new DataInputStream(bais);
        return dis.readInt();
    }

    public int getWeight() {
        try {
            return getInt(0);
        } catch (IOException ex) {
            Logger.getLogger(CompactHit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.MIN_VALUE;
    }

    public int getHash() {
        try {
            return getInt(4);
        } catch (IOException ex) {
            Logger.getLogger(CompactHit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.MIN_VALUE;
    }

    public int[] getPos() {
        ByteArrayInputStream bais = new ByteArrayInputStream(getData(), 8, getData().length - 8);
        DataInputStream dis = new DataInputStream(bais);
        int[] pos = new int[(getData().length - 8) / 4];
        for (int i = 0; i < pos.length; i++) {
            try {
                pos[i] = dis.readInt();
            } catch (IOException ex) {
                Logger.getLogger(CompactHit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pos;
    }

    public int removeNegpos() {
        int[] pos = getPos();
        TreeSet<Integer> set = new TreeSet<Integer>();
        for (Integer p : pos) {
            set.add(p);
        }
        set = (TreeSet<Integer>) set.tailSet(0);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeInt(getWeight());
            out.writeInt(getHash());
            for (int p : set) {
                out.writeInt(p);
            }
            baos.close();
            out.close();
            data = baos.toByteArray();
            return set.size();
        } catch (IOException ex) {
            Logger.getLogger(Hit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public byte[] getData() {
        return data;
    }
}
