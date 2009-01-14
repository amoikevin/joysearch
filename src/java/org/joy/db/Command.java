/*
 * DBCommand.java
 *
 * Created on 2007年8月3日, 下午12:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.db.CompactConfig;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DeadlockException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.Transaction;
import java.util.Stack;

/**
 * 对数据库的各种操作的抽象类。
 * @note 注意！所有的键如果是String类型，则全部被转换成hash码类型
 * @author 海
 */
public class Command {
    
    protected Connection conn;
    protected Stack<Transaction> tx = new Stack<Transaction>();
    
    protected Transaction currentTx() {
        if (tx.size() != 0) {
            return tx.peek();
        }
        return null;
    }
    
    /**
     * 创建一个Command类型实例。
     * @param conn 该命令要操作的数据库连接抽象层
     */
    public Command(Connection conn) {
        this.conn = conn;
    }
    
    public Command(Connection conn, Txn tx) {
        this.conn = conn;
        this.tx.push(tx.getTxn());
    }
    
    /**
     * 更新一个实体。包含键和值，如果键不存在，则创建之
     * @param key 键
     * @param value 值
     */
    public void setEntry(Object key, Object value) throws DLException, DBException {
        //序列化key
        DatabaseEntry keyE = new DatabaseEntry();
        
        conn.getKeyBinding().objectToEntry(key, keyE);
        DatabaseEntry valueE = new DatabaseEntry();
        conn.getValueBinding().objectToEntry(value, valueE);
        try {
            conn.getDb().put(currentTx(), keyE, valueE);
        } catch (DeadlockException ex) {
            throw new DLException();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    /**
     * 更新一个数据库实体。但如果已经存在并不覆盖，
     * @param key 键
     * @param value 值
     * @return 是否更改
     */
    public boolean setEntryNoOverwrite(Object key, Object value) throws DLException, DBException {
        //序列化key
        DatabaseEntry keyE = new DatabaseEntry();
        
        conn.getKeyBinding().objectToEntry(key, keyE);
        DatabaseEntry valueE = new DatabaseEntry();
        conn.getValueBinding().objectToEntry(value, valueE);
        try {
            OperationStatus retVal = conn.getDb().putNoOverwrite(currentTx(), keyE, valueE);
            if (retVal == OperationStatus.KEYEXIST) {
                return false;
            }
        } catch (DeadlockException ex) {
            throw new DLException();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        return true;
    }
    
    /**
     * 删除指定的实体
     * @param key 指定的实体的键
     */
    public void removeEntry(Object key) throws DLException, DBException {
        //序列化key
        DatabaseEntry keyE = new DatabaseEntry();
        
        conn.getKeyBinding().objectToEntry(key, keyE);
        
        try {
            conn.getDb().delete(currentTx(), keyE);
        } catch (DeadlockException ex) {
            throw new DLException();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    /**
     * 通过建获取实体。
     * @param key 指定的键
     * @return 返回键所指定的数据库实体
     */
    public Object getEntry(Object key) throws DLException, DBException {
        return getEntry(key, LockMode.DEFAULT);
    }
    
    /**
     * 通过建获取实体。
     * @param key 指定的键
     * @return 返回键所指定的数据库实体
     */
    public Object getEntry(Object key, LockMode lockMode) throws DLException, DBException {
        //序列化key
        DatabaseEntry keyE = new DatabaseEntry();
        
        conn.getKeyBinding().objectToEntry(key, keyE);
        
        DatabaseEntry valueE = new DatabaseEntry();
        try {
            OperationStatus retVal = conn.getDb().get(currentTx(), keyE, valueE, lockMode);
            if (retVal == OperationStatus.NOTFOUND) {
                return null;
            }
        } catch (DeadlockException ex) {
            throw new DLException();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        return conn.getValueBinding().entryToObject(valueE);
    }
    
    public ResultReader search(Object key, boolean writeCursor) throws DBException {
        try {
            CursorConfig cursorConfig = new CursorConfig();
            if (writeCursor) {
                cursorConfig.setWriteCursor(true);
            }
            Cursor c = conn.getDb().openCursor(null, cursorConfig);
            DatabaseEntry keyE = new DatabaseEntry();
            conn.getKeyBinding().objectToEntry(key, keyE);
            
            ResultReader r = new SearchResultReader(c, conn.getReaderPool(), keyE, conn.getKeyBinding(), conn.getValueBinding());
            return r;
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public ResultReader search(Object key) throws DBException {
        return search(key, false);
    }
    
    public ResultReader search(Object key, Object value, boolean writeCursor) throws DBException {
        try {
            CursorConfig cursorConfig = new CursorConfig();
            if (writeCursor) {
                cursorConfig.setWriteCursor(true);
            }
            Cursor c = conn.getDb().openCursor(null, cursorConfig);
            DatabaseEntry keyE = new DatabaseEntry(), valE = new DatabaseEntry();
            conn.getKeyBinding().objectToEntry(key, keyE);
            conn.getValueBinding().objectToEntry(value, valE);
            
            ResultReader r = new SearchResultReader(c, conn.getReaderPool(), keyE, valE, conn.getKeyBinding(), conn.getValueBinding());
            return r;
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public void clear() throws DBException {
        try {
            conn.getDb().truncate(null, false);
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    
    public void compact() throws DBException{
        try {
            conn.getDb().compact(null,null,null,null,new CompactConfig());
            for(SecondaryDatabase secDb:conn.getSecTable().values()){
                conn.getDb().compact(null,null,null,null,new CompactConfig());
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
    /**
     * 开始一个事物
     */
    protected void beginTxn() throws DBException {
        try {
            tx.push(conn.getEnv().beginTransaction(currentTx(), null));
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        
    }
    
    /**
     * 结束一个事务
     */
    protected void endTxn() throws DBException {
        if (currentTx() != null) {
            try {
                currentTx().commitNoSync();
            } catch (DatabaseException ex) {
                ex.printStackTrace();
                try {
                    currentTx().abort();
                } catch (DatabaseException exIn) {
                    ex.printStackTrace();
                }
                
                throw new DBException();
            }
            
            tx.pop();
        }
    }
}
