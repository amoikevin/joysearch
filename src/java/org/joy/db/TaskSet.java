/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.db;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.CursorConfig;

/**
 *
 * @author 海
 */
public class TaskSet extends ResultSet {

    public TaskSet() throws DBException {
        super(new StringBinding(), new StringBinding());
    }

    public String[] pop() throws DBException {
        CursorConfig cursorConfig = new CursorConfig();
        cursorConfig.setWriteCursor(true);
        ResultReader reader = getReader(cursorConfig);
        if (reader.next()) {
            String URL = (String) reader.getKey();
            String text = (String) reader.getValue();
            reader.delete();
            reader.close();
            return new String[]{URL, text};
        }
        reader.close();
        return null;
    }
}
