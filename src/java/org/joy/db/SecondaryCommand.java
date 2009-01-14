/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.SecondaryCursor;

/**
 *
 * @author 海
 */
public class SecondaryCommand extends Command {

    private SecondaryConnection secConn = (SecondaryConnection) super.conn;

    public SecondaryCommand(SecondaryConnection conn) {
        super(conn);
    }

    public ResultReader search(Object key, boolean writeCursor) throws DBException {
        try {
            CursorConfig cursorConfig = new CursorConfig();
            if (writeCursor) {
                cursorConfig.setWriteCursor(true);
            }
            SecondaryCursor c = (SecondaryCursor) secConn.getDb().openCursor(null, cursorConfig);
            DatabaseEntry keyE = new DatabaseEntry();
            secConn.getKeyBinding().objectToEntry(key, keyE);

            ResultReader r = new SSRReader(c, secConn.getReaderPool(), keyE,
                    secConn.getKeyBinding(),
                    secConn.getPrimaryKeyBinding(),
                    secConn.getValueBinding());
            return r;
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }

    public ResultReader search(Object key) throws DBException {
        return search(key, false);
    }

    public ResultReader search(Object key, Object pKey, boolean writeCursor) throws DBException {
        try {
            CursorConfig cursorConfig = new CursorConfig();
            if (writeCursor) {
                cursorConfig.setWriteCursor(true);
            }
            SecondaryCursor c = (SecondaryCursor) secConn.getDb().openCursor(null, cursorConfig);
            DatabaseEntry keyE = new DatabaseEntry(), pKeyE = new DatabaseEntry();
            secConn.getKeyBinding().objectToEntry(key, keyE);
            secConn.getPrimaryKeyBinding().objectToEntry(pKey, pKeyE);

            ResultReader r = new SSRReader(c, secConn.getReaderPool(), keyE, pKeyE,
                    secConn.getKeyBinding(), secConn.getPrimaryKeyBinding(), secConn.getValueBinding());
            return r;
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            throw new DBException();
        }
    }
}
