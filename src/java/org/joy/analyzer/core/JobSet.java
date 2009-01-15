/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.CursorConfig;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;

/**
 *
 * @author 海
 */
public class JobSet extends ResultSet {

    public JobSet() throws DBException {
        super(new IntegerBinding(), new ByteArrayBinding(), null);
    }

    public DocumentJob pop() throws DBException {
        CursorConfig cursorConfig = new CursorConfig();
        cursorConfig.setWriteCursor(true);
        ResultReader reader = getReader(cursorConfig);
        if (reader.next()) {
            DocumentJob value =
                    new DocumentJob((byte[])reader.getValue());
            reader.delete();
            reader.close();
            return value;
        }
        reader.close();
        return null;
    }
}
