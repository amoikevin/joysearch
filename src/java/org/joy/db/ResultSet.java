/*
 * NewClass.java
 *
 * Created on 2007年6月1日, 上午11:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.HashStats;
import com.sleepycat.db.LockDetectMode;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.StatsConfig;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.ResultSetConfig;

/**
 *  一个结果集·，不支持逻辑操作
 * @author AC
 */
public class ResultSet implements Comparable<ResultSet> {
    //自增的流水号
    private static int numAlive;
    private Environment env;
    public static int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    //数据库对象
    protected Database db;
    private EntryBinding keyBinding;
    private EntryBinding valueBinding;
    private Vector<ResultReader> readerPool = new Vector<ResultReader>();
    
    private void createEnvironment(long bufferSize) throws DBException {
        try {
            if (env == null) {
                EnvironmentConfig envConfig = new EnvironmentConfig();
                envConfig.setPrivate(true);
                
                envConfig.setAllowCreate(true);
                envConfig.setInitializeCDB(true);
                envConfig.setInitializeCache(true);
                envConfig.setLockDetectMode(LockDetectMode.MINWRITE);
                envConfig.setCacheSize(bufferSize);
                env = new Environment(null, envConfig);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public ResultSet(EntryBinding keyBinding, EntryBinding valueBinding) throws DBException {
        createEnvironment(DEFAULT_BUFFER_SIZE);
        //创建索引数据库
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(DatabaseType.HASH);
        dbConfig.setAllowCreate(true);
        try {
            db = env.openDatabase(null, null, null, dbConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        this.keyBinding = keyBinding;
        this.valueBinding = valueBinding;
        numAlive++;
    }
    
    public ResultSet(ResultReader reader) throws DBException {
        createEnvironment(DEFAULT_BUFFER_SIZE);
        //创建索引数据库
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(DatabaseType.HASH);
        dbConfig.setAllowCreate(true);
        try {
            db = env.openDatabase(null, null, null, dbConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        this.keyBinding = reader.getKeyBinding();
        this.valueBinding = reader.getValueBinding();
        numAlive++;
        put(reader);
    }
    
    public ResultSet(EntryBinding keyBinding, EntryBinding valueBinding, Comparator dupComparator) throws DBException {
        createEnvironment(DEFAULT_BUFFER_SIZE);
        //创建索引数据库
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(DatabaseType.HASH);
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        if (dupComparator != null) {
            dbConfig.setDuplicateComparator(dupComparator);
        }
        try {
            db = env.openDatabase(null, null, null, dbConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        this.keyBinding = keyBinding;
        this.valueBinding = valueBinding;
        numAlive++;
    }
    
    public ResultSet(ResultSetConfig config) throws DBException{
        createEnvironment(config.getBufferSize());
        //创建索引数据库
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(config.getType());
        dbConfig.setAllowCreate(true);
        
        if(config.isSupportDup()){
            dbConfig.setSortedDuplicates(true);
            if (config.getComparator() != null) {
                dbConfig.setDuplicateComparator(config.getComparator());
            }
        }
        try {
            db = env.openDatabase(null, null, null, dbConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        this.keyBinding = config.getKeyBinding();
        this.valueBinding = config.getValBinding();
        numAlive++;
    }
    
    public void close() throws DBException {
        try {
            for (int i = 0; i < readerPool.size(); i++) {
                readerPool.get(i).close();
            }
            db.close();
            numAlive--;
            if (numAlive == 0) {
                env.close();
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public boolean contains(Object key) throws DBException {
        try {
            DatabaseEntry keyE = new DatabaseEntry();
            getKeyBinding().objectToEntry(key, keyE);
            
            return db.get(null, keyE, new DatabaseEntry(), LockMode.DEFAULT) == OperationStatus.SUCCESS;
        } catch (DatabaseException ex) {
            Logger.getLogger(ResultSet.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
    
    /**
     * 获取数据集当中指定键的纪录
     * @param key 要获取的记录的键
     * @return 返回要获取的纪录值
     */
    public Object get(Object key) throws DBException {
        DatabaseEntry keyE = new DatabaseEntry();
        keyBinding.objectToEntry(key, keyE);
        DatabaseEntry valueE = new DatabaseEntry();
        OperationStatus retVal;
        try {
            retVal = db.get(null, keyE, valueE, LockMode.DEFAULT);
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        if (retVal == OperationStatus.NOTFOUND) {
            return null;
        }
        return valueBinding.entryToObject(valueE);
    }
    
    public Database getDB() {
        return db;
    }
    
    public void printAll() throws DBException {
        try {
            Cursor c = db.openCursor(null, null);
            DatabaseEntry keyE = new DatabaseEntry();
            DatabaseEntry valueE = new DatabaseEntry();
            while (c.getNext(keyE, valueE, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                Object key = keyBinding.entryToObject(keyE);
                Object value = keyBinding.entryToObject(valueE);
                System.out.println(key + ":" + value);
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    /**
     * 向数据集当中添加一个记录
     * @param key 要添加的纪录的键
     * @param value 要添加的纪录的值
     */
    public void put(Object key, Object value) throws DBException {
        try {
            DatabaseEntry keyE = new DatabaseEntry();
            keyBinding.objectToEntry(key, keyE);
            DatabaseEntry valueE = new DatabaseEntry();
            valueBinding.objectToEntry(value, valueE);
            db.put(null, keyE, valueE);
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public void put(ResultSet set) throws DBException {
        try {
            Cursor setC = set.db.openCursor(null, null);
            DatabaseEntry keyE = new DatabaseEntry();
            DatabaseEntry valueE = new DatabaseEntry();
            while (setC.getNext(keyE, valueE, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                db.put(null, keyE, valueE);
            }
            setC.close();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public void put(ResultReader reader) throws DBException {
        while (reader.next()) {
            put(reader.getKey(), reader.getValue());
        }
    }
    
    public ResultReader getReader(CursorConfig cursorConfig) throws DBException {
        try {
            Cursor c = db.openCursor(null, cursorConfig);
            synchronized (readerPool) {
                ResultReader r = new DefaultResultReader(c, readerPool, keyBinding, valueBinding);
                readerPool.add(r);
                return r;
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public ResultReader getReader() throws DBException {
        return getReader(null);
    }
    
    public ResultReader getSearchKey(Object key, boolean writeCursor) throws DBException {
        try {
            CursorConfig cursorConfig = new CursorConfig();
            if (writeCursor) {
                cursorConfig.setWriteCursor(true);
            }
            Cursor c = db.openCursor(null, cursorConfig);
            DatabaseEntry keyE = new DatabaseEntry();
            keyBinding.objectToEntry(key, keyE);
            
            ResultReader r = new SearchResultReader(c, readerPool, keyE, keyBinding, valueBinding);
            return r;
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public ResultReader getSearchKey(Object key) throws DBException {
        return getSearchKey(key, false);
    }
    
    public void remove(Object key) throws DBException {
        try {
            DatabaseEntry keyE = new DatabaseEntry();
            keyBinding.objectToEntry(key, keyE);
            OperationStatus retVal = db.delete(null, keyE);
            if (retVal == OperationStatus.NOTFOUND) {
                return;
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public int count() throws DBException {
        try {
            StatsConfig conf = new StatsConfig();
            conf.setFast(true);
            HashStats stats = (HashStats) db.getStats(null, conf);
            return stats.getNumData();
        } catch (DatabaseException ex) {
            Logger.getLogger(ResultSet.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
    
    public void clear() throws DBException {
        try {
            db.truncate(null, false);
        } catch (DatabaseException ex) {
            Logger.getLogger(ResultSet.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
    
    public EntryBinding getKeyBinding() {
        return keyBinding;
    }
    
    public EntryBinding getValueBinding() {
        return valueBinding;
    }
    
    public int compareTo(ResultSet set) {
        try {
            return count() - set.count();
        } catch (DBException ex) {
            Logger.getLogger(ResultSet.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
}
