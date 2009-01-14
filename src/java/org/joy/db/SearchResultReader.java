package org.joy.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 海
 */
public class SearchResultReader extends DefaultResultReader {

    boolean firstRead = true;
    boolean searchBoth = false;

    /** Creates a new instance of ResultSet */
    public SearchResultReader(Cursor cursor,
            Vector<ResultReader> readerPool,
            DatabaseEntry keyE,
            EntryBinding keyBinding,
            EntryBinding valueBinding) {
        super(cursor, readerPool, keyBinding, valueBinding);
        this.currentKey = keyE;
    }

    /** Creates a new instance of ResultSet */
    public SearchResultReader(Cursor cursor,
            Vector<ResultReader> readerPool,
            DatabaseEntry keyE,
            DatabaseEntry valueE,
            EntryBinding keyBinding,
            EntryBinding valueBinding) {
        super(cursor, readerPool, keyBinding, valueBinding);
        this.currentKey = keyE;
        this.currentVal = valueE;
        this.searchBoth = true;
    }

    @Override
    public boolean next(LockMode lockMode) throws DBException {
        try {
            //如果给定了值，则进行双重匹配查询
            if (firstRead) {
                if (searchBoth) {
                    if (cursor.getSearchBoth(currentKey, currentVal, lockMode) != OperationStatus.SUCCESS) {
                        return false;
                    }
                } else {
                    //如果是第一次读取，首先要进行搜索
                    if (cursor.getSearchKey(currentKey, currentVal, lockMode) != OperationStatus.SUCCESS) {
                        return false;
                    }
                }
            } else {
                if (cursor.getNextDup(currentKey, currentVal, lockMode) != OperationStatus.SUCCESS) {
                    return false;
                }
            }
            firstRead = false;
            return true;

        } catch (DatabaseException ex) {
            throw new DBException();
        }
    }
}
