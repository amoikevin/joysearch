/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.rank.core;

import java.util.BitSet;

/**
 *
 * @author 海
 */
public class BigBoolArray {

//    class Block {
//
//        public int[] elems = new int[32];
//        public final static int BLOCK_SIZE = 4 * 8 * 32;
//    }
    private BitSet[] blocks;
    private final static int BLOCK_SIZE = 1024 * 1024 * 8;
    private long size;

    public BigBoolArray(long size) {
        if (size % BLOCK_SIZE != 0) {
            blocks = new BitSet[(int) (size / BLOCK_SIZE + 1)];
        } else {
            blocks = new BitSet[(int) (size / BLOCK_SIZE)];
        }
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new BitSet(BLOCK_SIZE);
        }
        this.size = size;
    }

    public void set(long index, boolean val) {
        blocks[(int) (index / BLOCK_SIZE)].set((int) (index % BLOCK_SIZE), val);
    }

    public boolean get(long index) {
        return blocks[(int) (index / BLOCK_SIZE)].get((int) (index % BLOCK_SIZE));
    }

    public static void main(String[] args) {
        BigBoolArray a = new BigBoolArray(60000L * 60000);
        a.set(333024, true);
        System.out.println(a.get(333024));
        System.out.println(a.get(203));
    }

    public long getSize() {
        return size;
    }
}
