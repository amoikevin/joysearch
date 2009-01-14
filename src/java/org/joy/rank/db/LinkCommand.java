/*
 * LinkCommand.java
 *
 * Created on 2007年11月26日, 上午8:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.rank.db;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.joy.db.DBException;
import org.joy.rank.service.ServerImp;
import org.joy.pagerank.BigBoolArray;
import org.joy.pagerank.PageRank;
import org.joy.deployer.Deployer;
import org.joy.db.Command;
import org.joy.dblookup.service.UID;
/**
 *
 * @author 海
 */
public class LinkCommand extends Command {
    
    static Hashtable<Long, Integer> relIndex = new Hashtable<Long, Integer>();
    static PageRank cal = new PageRank();
    static {
        cal.setBeta(0.5f);
        cal.setPrecision(0.0000001f);
    }
    public LinkCommand(LinkConnection conn) {
        super(conn);
    }
    
    private BigBoolArray genRelMatrix() throws DBException {
        try {
            
            Cursor cursor = conn.getDb().openCursor(null, null);
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            OperationStatus retVal = cursor.getFirst(key, value, LockMode.DEFAULT);
            int i = 0;
            while (retVal == OperationStatus.SUCCESS) {
                //依次读取URL
                //按照数据库中，链接的排列顺序，构造一个从链接名称到矩阵索引的映射
                relIndex.put(LongBinding.entryToLong(key), i);
                retVal = cursor.getNext(key, value, LockMode.DEFAULT);
                i++;
            }
            
            //生成矩阵
            Deployer.logger.info("一共有" + relIndex.size() + "个页面");
            Deployer.logger.info("申请前内存大小:");
            ServerImp.checkMem();
            BigBoolArray relMatrix = new BigBoolArray(relIndex.size() * (long) relIndex.size());
            //写入关系
            retVal = cursor.getFirst(key, value, LockMode.DEFAULT);
            i = 0;
            while (retVal == OperationStatus.SUCCESS) {
                LinkEntry entry = (LinkEntry) conn.getValueBinding().entryToObject(value);
                for (Long link : entry.getLinks()) {
                    Integer j = relIndex.get(link);
                    if (j != null) {
                        relMatrix.set(i * relIndex.size() + j, true);
                    }
                }
                i++;
                retVal = cursor.getNext(key, value, LockMode.DEFAULT);
            }
            cursor.close();
            return relMatrix;
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(null, ex);
            throw new DBException();
        }
    }
    
    public Hashtable<Long, Float> calPageRank() throws DBException {
        relIndex.clear();
        BigBoolArray relation = genRelMatrix();
        cal.setLinkArray(relation, relIndex.size());
        cal.run();
        Hashtable<Long, Float> rankTable = new Hashtable<Long, Float>();
        Enumeration<Long> keys = relIndex.keys();
        while (keys.hasMoreElements()) {
            Long hash = keys.nextElement();
            int index = relIndex.get(hash);
            rankTable.put(hash, cal.getProb()[index]);
        }
        return rankTable;
    }
    
    public float getPageRank(String URL) {
        int index = relIndex.get(UID.from(URL));
        return cal.getProb()[index];
    }
    
    public Long[] getRefs(String URL){
        int index = relIndex.get( UID.from(URL) );
        
        Hashtable<Integer,Long> reverseTable = new Hashtable<Integer,Long>();
        Enumeration<Long> keys = relIndex.keys();
        while (keys.hasMoreElements()) {
            Long hash = keys.nextElement();
            int i = relIndex.get(hash);
            reverseTable.put(i,hash);
        }
        
        Vector<Long> refs = new Vector<Long>();
        int j =0;
        for (int i = 0; i < relIndex.size(); i++) {
            if(cal.getLinkArray().get( (long)index+i*relIndex.size() )) {
                //分发到各各分数据库
                refs.add(reverseTable.get(j));
            }
            j++;
        }
        return refs.toArray(new Long[0]);
    }
}
