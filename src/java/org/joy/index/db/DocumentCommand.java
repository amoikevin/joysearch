/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.db;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.Command;
import org.joy.db.DBException;

/**
 *
 * @author 海
 */
public class DocumentCommand extends Command {

    public DocumentCommand(DocumentConnection docConn) {
        super(docConn);
    }

    public String getURLByHash(int hash, LockMode lockMode) throws DBException {
        DatabaseEntry keyE = new DatabaseEntry();
        DatabaseEntry valE = new DatabaseEntry();
        DatabaseEntry pKeyE = new DatabaseEntry();
        IntegerBinding.intToEntry(hash, keyE);
        try {
            if (((DocumentConnection) conn).getHashIndex().get(null, keyE, pKeyE,valE, lockMode) != OperationStatus.SUCCESS) {
                return null;
            }
            return StringBinding.entryToString(pKeyE);
        } catch (DatabaseException ex) {
            Logger.getLogger(DocumentCommand.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }

    public float getRankByHash(int hash, LockMode lockMode) throws DBException {
        DatabaseEntry keyE = new DatabaseEntry();
        DatabaseEntry valE = new DatabaseEntry();
        IntegerBinding.intToEntry(hash, keyE);
        try {
            if (((DocumentConnection) conn).getHashIndex().get(null, keyE, valE, lockMode) != OperationStatus.SUCCESS) {
                return -1.0f;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(valE.getData());
            DataInputStream dis = new DataInputStream(bais);
            return dis.readFloat();
        } catch (IOException ex) {
            Logger.getLogger(DocumentCommand.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        } catch (DatabaseException ex) {
            Logger.getLogger(DocumentCommand.class.getName()).log(Level.SEVERE, null, ex);
            throw new DBException();
        }
    }
}
