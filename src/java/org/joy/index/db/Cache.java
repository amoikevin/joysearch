/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.db;

import org.joy.index.db.entry.CompactHit;
import org.joy.index.db.entry.Hit;
import org.joy.index.db.entry.Hits;
import org.joy.index.db.entry.HitsBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.BtreeStats;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.StatsConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;
import org.joy.db.ResultSetConfig;

/**
 *
 * @author 海
 */
public class Cache extends ResultSet {

    private RIndexConnection rIndexConn;
    private RICommand rIndexCmd;
    private static ResultSetConfig config = new ResultSetConfig();

    static {
        config.setKeyBinding(new StringBinding());
        config.setValBinding(new HitsBinding());
        config.setBufferSize(1024 * 1024 * 20);
        config.setSupportDup(true);
    }

    public Cache(RIndexConnection rIndexConn) throws DBException {
        super(config);
        this.rIndexConn = rIndexConn;
        this.rIndexCmd = new RICommand(rIndexConn);
    }

    private void updateRIndex(String URL, Hits hits) throws DBException {
        //优化
        //hits.compact();
        //添加进RIndex
        for (Hit h : hits.toArray()) {
            CompactHit cp =h.toCompactHit(URL);
            ResultReader r = rIndexCmd.search(URL, cp, false);
            if(r.next()){
                System.out.println(URL + "有"+hits);
                System.out.println();
            }
            r.close();
            rIndexCmd.setEntry(h.getKeyword(), cp);
        }
    }

    public RIStatistics loadHits() throws DBException {
        Cursor c = null;
        try {
            CursorConfig cursorConfig = new CursorConfig();
            cursorConfig.setWriteCursor(true);
            //读取一个URL的所有Hit组装成一个Hits
            c = db.openCursor(null, cursorConfig);
            DatabaseEntry keyE = new DatabaseEntry();
            DatabaseEntry valE = new DatabaseEntry();
            
            int numDocs = 0;
            while (c.getNextNoDup(keyE, valE, null) == OperationStatus.SUCCESS) {
                Hits hits = new Hits();
                String URL = (String) getKeyBinding().entryToObject(keyE);
                do {
                    Hits h = (Hits) getValueBinding().entryToObject(valE);
                    hits.combine(h);
                    //索引过之后删除指针
                    c.delete();
                //System.out.println(hits);
                } while ((c.getNextDup(keyE, valE, null) == OperationStatus.SUCCESS));
                //更新RIndex
                //System.out.println("更新"+URL+"的关键字");
                updateRIndex(URL, hits);
                
                numDocs++;
            }
            System.out.println("结束 压缩索引...");
            //压缩数据库
            StatsConfig conf = new StatsConfig();
            conf.setFast(false);
            BtreeStats stats = (BtreeStats) rIndexConn.getDb().getStats(null, conf);
            System.out.println(stats);
            return new RIStatistics(numDocs, rIndexCmd.getStat());
        // rIndexCmd.compact();
        } catch (DatabaseException ex) {
            Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        } finally {
            try {
                c.close();
            } catch (DatabaseException ex) {
                ex.printStackTrace();
            }
        }
    }
}
