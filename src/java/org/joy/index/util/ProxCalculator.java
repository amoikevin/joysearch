package org.joy.index.util;

import org.joy.index.db.entry.CompactHit;
import org.joy.index.db.entry.Hit;
import org.joy.index.db.entry.Hits;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Arrays;
import org.joy.index.db.entry.Pos;
import java.util.Vector;

/**
 *
 * @author 海
 */
public class ProxCalculator {

    public static double calProximity(CompactHit[] hits) {
        //计算邻近性，值越大越好
        int[] res = getProxArray(hits);
        int length = 1;
        for (int i = 1; i < res.length; i++) {
            length += Math.abs(res[i] - res[i - 1]);
        }
        return 100.0f / length;
    }

//    protected static double calProximity2(Integer[] hits1, Integer[] hits2) {
//        //寻找最接近的一组相距的距离
//        int minDist = -1;
//        for (int i = 0; i < hits1.length; i++) {
//            for (int j = 0; j < hits2.length; j++) {
//                int dist = Math.abs(hits2[j] - hits1[i]);
//                if (minDist == -1) {
//                    minDist = dist;
//                } else if (minDist > dist) {
//                    minDist = dist;
//                }
//            }
//        }
//        return 100 / (minDist + 1);
//    }
    protected static int[] getProxArray2(int[] hits1, int[] hits2) {
        //寻找最接近的一组相距的距离
        int[] res = new int[2];
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < hits1.length; i++) {
            int t1 = (int) hits1[i];
            int t2 = getProxArray2(t1, hits2);
            if (t2 - t1 == 1) {
                //如果已经有距离为1的直接返回
                res[0] = t1;
                res[1] = t2;
                return res;
            }
            if (minDist > Math.abs(t1 - t2)) {
                minDist = Math.abs(t1 - t2);
                res[0] = t1;
                res[1] = t2;
            }
        }
        return res;
    }

    protected static int getProxArray2(int pos, int[] hits2) {
        //寻找最接近的一组相距的距离
        //转化成整型数组计算
        int t[] = new int[hits2.length];
        for (int i = 0; i < hits2.length; i++) {
            t[i] = hits2[i];
        }
        int res = -Arrays.binarySearch(t, pos);
        if (res <= 0) {
            return pos;
        }
        if (res == 1) {
            return hits2[0];
        }
        if (res == hits2.length + 1) {
            return hits2[hits2.length - 1];
        }
        if (Math.abs(pos - hits2[res - 2]) < Math.abs(pos - hits2[res - 1])) {
            return hits2[res - 2];
        } else {
            return hits2[res - 1];
        }
    }

    public static int[] getProxArray(CompactHit[] hits) {
        if (hits.length <= 1) {
            return new int[]{ hits[0].getPos()[0] };
        }
        //寻找最接近的一组相距的距离
        int[] res = new int[hits.length];
        int[] init = getProxArray2(hits[0].getPos(), hits[1].getPos());
        res[0] = init[0];
        res[1] = init[1];
        for (int i = 2; i < hits.length; i++) {
            res[i] = getProxArray2(res[i - 1], hits[i].getPos());
        }
        Arrays.sort(res);
        return res;
    }
//        public static int[] getProxArray(Integer[][] hits) {
//        if (hits.length <= 1) {
//            int[] pos = new int[hits[0].length];
//            int j = 0;
//            for (int i : hits[0]) {
//                pos[j] = i;
//                j++;
//            }
//            return pos;
//        }
//        //寻找最接近的一组相距的距离
//        int[] res = new int[hits.length];
//        int[] init = getProxArray2(hits[0],hits[1]);
//        res[0] = init[0];
//        res[1] = init[1];
//        for (int i = 2; i < hits.length; i++) {
//            res[i] = getProxArray2(res[i - 1], hits[i]);
//        }
//        Arrays.sort(res);
//        return res;
//    }
    public static void main(String[] args) {
        Hits hs = new Hits();
        Hit a = new Hit("A", new Pos[]{
            new Pos((short) 1, (byte) 1),
            new Pos((short) 3, (byte) 1), new Pos((short) 4, (byte) 1),
            new Pos((short) 5, (byte) 1),
            new Pos((short) 10, (byte) 1)
        });

        Hit b = new Hit("B", new Pos[]{
            new Pos((short) 9, (byte) 1),
            new Pos((short) 10, (byte) 1),
            new Pos((short) 6, (byte) 1),
            new Pos((short) 11, (byte) 1)
        });

        Hit d = new Hit("C", new Pos[]{
            new Pos((short) 12, (byte) 1),
            new Pos((short) 17, (byte) 1), new Pos((short) 18, (byte) 1),
            new Pos((short) 19, (byte) 1)
        });

        hs.setHit(a);
        hs.setHit(b);
        hs.setHit(d);
        Vector<CompactHit> vec = new Vector<CompactHit>();
        vec.add(a.toCompactHit("abc"));
        vec.add(b.toCompactHit("def"));
        vec.add(d.toCompactHit("ghi"));
//        int t = getProxArray2(15, new Integer[]{12, 13, 14, 20});
//        System.out.println(t);
//        System.out.println(hs);
        int[] res = getProxArray(vec.toArray(new CompactHit[0]));
        for (int i : res) {
            System.out.println(i);
        }
        System.out.println(calProximity(vec.toArray(new CompactHit[0])));
    }
}
