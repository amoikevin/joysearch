/*
 * Transaction.java
 *
 * Created on 2007年11月30日, 下午5:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.db.DatabaseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 海
 */
public class Txn {

    private com.sleepycat.db.Transaction txn;

    public Txn(com.sleepycat.db.Transaction txn) {
        this.txn = txn;
    }

    com.sleepycat.db.Transaction getTxn() {
        return txn;
    }

    public void abort() throws DBException {
        try {
            txn.abort();
        } catch (DatabaseException ex) {
            Logger.getLogger(Txn.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }

    public void endTxn() throws DBException {
        try {
            txn.commit();
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            try {
                txn.abort();
            } catch (DatabaseException ex2) {
                ex.printStackTrace();
            }
            throw new DBException();
        }
    }
}
