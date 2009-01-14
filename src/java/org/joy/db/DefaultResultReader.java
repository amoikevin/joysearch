/*
 * ResultSet.java
 *
 * Created on 2007年12月11日, 下午8:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 海
 */
public class DefaultResultReader implements ResultReader {

    protected Cursor cursor;
    protected DatabaseEntry currentKey = new DatabaseEntry();
    protected DatabaseEntry currentVal = new DatabaseEntry();
    private EntryBinding keyBinding;
    private EntryBinding valueBinding;
    private Vector<ResultReader> readerPool;

    /** Creates a new instance of ResultSet */
    public DefaultResultReader(Cursor cursor,
            Vector<ResultReader> readerPool,
            EntryBinding keyBinding,
            EntryBinding valueBinding) {
        this.cursor = cursor;
        this.keyBinding = keyBinding;
        this.valueBinding = valueBinding;
        this.readerPool = readerPool;
    }

    public Object getKey() {
        return keyBinding.entryToObject(currentKey);
    }

    public Object getValue() {
        return valueBinding.entryToObject(currentVal);
    }

    public boolean next() throws DBException {
        return next(null);
    }

    public void close() throws DBException {
        try {
            cursor.close();
            readerPool.remove(this);
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    protected void finalize() throws Throwable {
        cursor.close();
        super.finalize();
    }

    public EntryBinding getKeyBinding() {
        return keyBinding;
    }

    public EntryBinding getValueBinding() {
        return valueBinding;
    }

    public boolean next(LockMode lockMode) throws DBException {
        try {
            if (cursor.getNext(currentKey, currentVal, lockMode) == OperationStatus.SUCCESS) {
                return true;
            }
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
        return false;
    }

    public void delete() throws DBException {
        try {
            cursor.delete();
        } catch (DatabaseException ex) {
            throw new DBException();
        }
    }

    public void put(Object key, Object value) throws DBException {
        try {
            DatabaseEntry keyE = new DatabaseEntry();
            DatabaseEntry valueE = new DatabaseEntry();
            keyBinding.objectToEntry(key, keyE);
            valueBinding.objectToEntry(value, valueE);
            cursor.put(keyE, valueE);
        } catch (DatabaseException ex) {
            throw new DBException();
        }
    }

    public int count() throws DBException {
        try {
            return cursor.count();
        } catch (DatabaseException ex) {
            Logger.getLogger(DefaultResultReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }

    //有问题，Result生成的临时Set过大
    public ResultSet orderBy(Comparator c, int count) throws DBException {
        ResultSet tempSet = new ResultSet(getKeyBinding(), getValueBinding(), c);
        for (int i = 0; i < count; i++) {
            if (next()) {
                tempSet.put(getKey(), getValue());
            } else {
                break;
            }
        }
        return tempSet;
    }

    public int compareTo(ResultReader reader) {
        try {
            return count() - reader.count();
        } catch (DBException ex) {
            Logger.getLogger(DefaultResultReader.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
}
