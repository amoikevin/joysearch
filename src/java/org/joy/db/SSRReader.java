/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.SecondaryCursor;
import java.util.Vector;

/**
 * Secondary Search Result Reader = SSRReader
 * @author 海
 */
public class SSRReader extends SearchResultReader {

    private DatabaseEntry currentPKey = new DatabaseEntry();
    private EntryBinding pKeyBinding;

    public SSRReader(SecondaryCursor cursor,
            Vector<ResultReader> readerPool,
            DatabaseEntry keyE,
            EntryBinding keyBinding,
            EntryBinding pKeyBinding,
            EntryBinding valueBinding) {
        super(cursor, readerPool, keyE, keyBinding, valueBinding);
        this.pKeyBinding = pKeyBinding;
    }

    public SSRReader(SecondaryCursor cursor,
            Vector<ResultReader> readerPool,
            DatabaseEntry keyE,
            DatabaseEntry pKeyE,
            EntryBinding keyBinding,
            EntryBinding pKeyBinding,
            EntryBinding valueBinding) {
        super(cursor, readerPool, keyE, keyBinding, valueBinding);
        this.currentPKey = pKeyE;
        this.pKeyBinding = pKeyBinding;
    }

    public Object getPrimaryKey() {
        return pKeyBinding.entryToObject(currentPKey);
    }

    @Override
    public boolean next(LockMode lockMode) throws DBException {
        SecondaryCursor sCursor = (SecondaryCursor) this.cursor;
        try {
            if (firstRead) {
                //如果给定了值，则进行双重匹配查询
                if (searchBoth) {
                    if (sCursor.getSearchBoth(currentKey, currentPKey, currentVal, lockMode) != OperationStatus.SUCCESS) {
                        return false;
                    }
                } else {//如果是第一次读取，首先要进行搜索
                    if (sCursor.getSearchKey(currentKey, currentPKey, currentVal, lockMode) != OperationStatus.SUCCESS) {
                        return false;
                    }
                }
            } else {
                if (sCursor.getNextDup(currentKey, currentPKey, currentVal, lockMode) != OperationStatus.SUCCESS) {
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
