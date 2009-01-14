/*
 * ReverseIndexCommand.java
 *
 * Created on 2007年12月11日, 下午8:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.OperationStatus;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.Command;
import org.joy.db.Connection;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;
import org.joy.db.SearchResultReader;
import org.joy.db.ResultSetConfig;
import org.joy.index.db.entry.CHitVectorBinding;
import org.joy.index.db.entry.CompactHit;

class SearchResultSet extends ResultSet {

    private static ResultSetConfig config = new ResultSetConfig();

    static {
        config.setBufferSize(1024 * 512);
        config.setKeyBinding(new IntegerBinding());
        config.setValBinding(new CHitVectorBinding());
        config.setType(DatabaseType.BTREE);
    }

    public SearchResultSet() throws DBException {
        super(config);
    }
}

/**
 *
 * @author 海
 */
public class RICommand extends Command {

    /** Creates a new instance of ReverseIndexCommand */
    public RICommand(Connection conn) {
        super(conn);
    }
    private final static int MAX_RESULT = 1000;

    private SearchResultSet[] extract(ResultReader[] readers) throws DBException {
        TreeMap<Integer, ResultSet> setsMap = new TreeMap<Integer, ResultSet>();

        for (ResultReader reader : readers) {
            int count = 0;
            SearchResultSet temp = new SearchResultSet();
            while (reader.next()) {
                //目前测试，不加入限定
//                if (count >= MAX_RESULT) {
//                    break;
//                }
                //把记录转换成URLhash:HITs的形式
                CompactHit e = (CompactHit) reader.getValue();

                Vector<CompactHit> hs = new Vector<CompactHit>();
                hs.add(e);
                temp.put(e.getHash(), hs);
                //System.out.println(e.getURL());
                count++;
            }
            //如果之前已经存在一个同样Count的set，那么将set的 count+1，避免重复
            while (setsMap.get(count) != null) {
                count++;
            }
            setsMap.put(count, temp);
        }
        return setsMap.values().toArray(new SearchResultSet[0]);
    }

    private void join(SearchResultSet[] sets) throws DBException {

        //找到数量最小的count
        CursorConfig cfg = new CursorConfig();
        cfg.setWriteCursor(true);

        //求交
        for (int i = 1; i < sets.length; i++) {
            ResultReader baseReader = sets[0].getReader(cfg);
            while (baseReader.next()) {
                if (!sets[i].contains(baseReader.getKey())) {
                    baseReader.delete();
                }
            }
            baseReader.close();
        }

        //把Hit综合起来
        ResultReader baseReader = sets[0].getReader(cfg);
        while (baseReader.next()) {
            Vector<CompactHit> hs = (Vector<CompactHit>) baseReader.getValue();
            for (int i = 1; i < sets.length; i++) {
                Vector<CompactHit> t = (Vector<CompactHit>) sets[i].get(baseReader.getKey());
                hs.addAll(t);
            }
            baseReader.put(baseReader.getKey(), hs);
        }
        baseReader.close();
    }

    public ResultSet search(String[] keywords) throws DBException {
        //寻找每个Keyword的Reader
        ResultReader[] readers = new SearchResultReader[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            readers[i] = search(keywords[i]);
        }
        SearchResultSet[] s = extract(readers);
        join(s);
        for (ResultReader reader : readers) {
            reader.close();
        }
        for (int i = 1; i < s.length; i++) {
            s[i].close();
        }
        return s[0];
    }

    public Hashtable<String,Integer> getStat() throws DBException {
        try {
            Cursor c = conn.getDb().openCursor(null, null);
            Hashtable<String, Integer> ITFTable = new Hashtable<String, Integer>();
            DatabaseEntry keyE = new DatabaseEntry();
            while (c.getNextNoDup(keyE, new DatabaseEntry(), null) == OperationStatus.SUCCESS) {
                String keyword = StringBinding.entryToString(keyE);
                int i = 0;
                do {
                    i++;
                //System.out.println(hits);
                } while (c.getNextDup(new DatabaseEntry(), new DatabaseEntry(), null) == OperationStatus.SUCCESS);
                ITFTable.put(keyword, i);
            }
            c.close();
            return ITFTable;
        } catch (DatabaseException ex) {
            Logger.getLogger(RICommand.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
}
