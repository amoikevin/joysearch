/*
 * ResultCursor.java
 *
 * Created on 2007骞?2鏈?3鏃? 涓嬪崍4:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.query;

import org.joy.query.*;
import org.joy.index.db.DocumentCommand;
import org.joy.query.Filter;
import com.sleepycat.db.LockMode;
import org.joy.query.ResultEntry;
import org.joy.query.Cursor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import org.joy.db.DBException;
import org.joy.db.ResultReader;


/**
 *
 * @author 娴?
 */
public class ResultCursor extends UnicastRemoteObject implements Cursor {

    private ResultReader reader;
    private DocumentCommand cmd;

    /** Creates a new instance of ResultCursor */
    public ResultCursor(ResultReader reader, DocumentCommand cmd) throws RemoteException {
        super();
        this.reader = reader;
        this.cmd = cmd;
    }

    public void close() {
        try {
            reader.close();
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }

    public ResultEntry getCurrent() throws DBException {
        throw new UnsupportedOperationException();
    }

    public ResultEntry[] getNext(int count) throws DBException,RemoteException {
        return getNext(count, null);
    }

    public ResultEntry[] getNext(int count, Filter filter) throws DBException,RemoteException {
        Vector<ResultEntry> entries = new Vector<ResultEntry>();
        for (int i = 0; i < count; i++) {
            if (reader.next()) {
                //璇诲彇URL
                String URL = cmd.getURLByHash((Integer) reader.getValue(), LockMode.DEFAULT);
                if (filter != null) {
                    if (filter.filter(URL)) {
                        i--;
                        continue;
                    }
                }
                entries.add(new ResultEntry(URL,
                        (Double) reader.getKey()));
            } else {
                return entries.toArray(new ResultEntry[0]);
            }
        }
        return entries.toArray(new ResultEntry[0]);
    }

    public ResultEntry[] getPrev(int count,int count2) throws DBException {
        throw new UnsupportedOperationException();
    }

    public String getServerName() throws RemoteException {
        throw new UnsupportedOperationException();
    }

    public void P() throws RemoteException {
        System.out.println("abc");
    }
}
