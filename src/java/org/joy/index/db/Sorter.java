/*
 * Searcher.java
 *
 * Created on 2007年12月12日, 上午10:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db;

import org.joy.index.db.entry.CompactHit;
import org.joy.index.util.ProxCalculator;
import com.sleepycat.bind.tuple.DoubleBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.db.LockMode;
import java.util.Arrays;
import java.util.Vector;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;
import org.joy.db.ResultSetConfig;

/**
 *
 * @author 海
 */
public class Sorter {

    private DocumentConnection docConn;
    private RIndexConnection rIndexConn;
    private RIStatistics riStat;

    public Sorter(DocumentConnection docConn, RIndexConnection rIndexConn, RIStatistics riStat) {
        this.docConn = docConn;
        this.rIndexConn = rIndexConn;
        this.riStat = riStat;
    }

    class SortResultSetConfig extends ResultSetConfig {

        public SortResultSetConfig() {
            super();
            setBufferSize(1024 * 512);
            setKeyBinding(new DoubleBinding());
            setValBinding(new IntegerBinding());
            setSupportDup(true);
        }
    }

    private double[] genITFTable(String[] keywords) {
        double[] ITFTable = new double[keywords.length];
        //生成当前ITF
        int i = 0;
        for (String keyword : keywords) {
            if (riStat.getITFTable().get(keyword) == null) {
                return null;
            }
            int freq = riStat.getITFTable().get(keyword);
            double weight = Math.abs(Math.log( (double) freq/riStat.getTotal() ));
            System.out.println(keyword + "共有" + freq + "个,weight" + weight);
            ITFTable[i] = weight;
            i++;
        }
        //排序
        Arrays.sort(ITFTable);
        return ITFTable;
    }

    public ResultSet sort(ResultReader reader, String[] keywords) throws DBException {
        ResultSet res = new ResultSet(new SortResultSetConfig());
        double[] ITFTable = genITFTable(keywords);
        if (ITFTable == null)//如果连ITFTable都找不全，那么就不要继续查找了
        {
            return res;
        }
        DocumentCommand docCmd = new DocumentCommand(docConn);
        int count = 0;

        while (reader.next()) {
            Integer hash = (Integer) reader.getKey();
            Vector<CompactHit> hits = (Vector<CompactHit>) reader.getValue();
            if (hash == "http://jwzj.suda.edu.cn/web/".hashCode()) {
                System.out.println("1");
                //double proximity2 = ProxCalculator.calProximity(hits.toArray(new CompactHit[0]));
                //System.out.println(hash + "\nrank " + rank + "\nprox " + proximity + "\nfreq " + freq + "\n");
            }
            double proximity = ProxCalculator.calProximity(hits.toArray(new CompactHit[0]));

            //获取总频率
            int freq = 0;
            int i = ITFTable.length - 1;
            for (CompactHit hit : hits) {
                freq += hit.getWeight() * ITFTable[i];//*getITFWeight(hit.getHash());
                i--;
            }
            float rank = docCmd.getRankByHash(hash, LockMode.DEFAULT);
//            //System.out.println("------------------------------");

            //取倒数
            double totalRank = 1.0f / (freq/10e5 * proximity * rank);

//            if (hash == "http://ysxy.suda.edu.cn/user_reg.asp".hashCode()) {
//                System.out.println("1");
//                //double proximity2 = ProxCalculator.calProximity(hits.toArray(new CompactHit[0]));
//                System.out.println(hash + "\nrank " + rank + "\nprox " + proximity + "\nfreq " + freq + "\n");
//                System.out.println(res1 + " " + res2 + " " + res3);
//            }
            if (hash == "http://www.suda.edu.cn/".hashCode()) {
                System.out.println("1");
                //double proximity2 = ProxCalculator.calProximity(hits.toArray(new CompactHit[0]));
                System.out.println(hash + "\nrank " + rank + "\nprox " + proximity + "\nfreq " + freq + "\n");
            }
            if (hash == "http://dag.suda.edu.cn/showfile.asp?id=38".hashCode()) {
                System.out.println("2");
                //double proximity2 = ProxCalculator.calProximity(hits.toArray(new CompactHit[0]));
                System.out.println(hash + "\nrank " + rank + "\nprox " + proximity + "\nfreq " + freq + "\n");
            }
            count++;
            res.put(totalRank, hash);

        }
        System.out.println("共" + count + "结果");
        return res;
    }
    }
