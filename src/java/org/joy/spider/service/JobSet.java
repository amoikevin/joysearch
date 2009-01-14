/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.spider.service;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.CursorConfig;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;
import org.joy.spider.service.DownloadJob;

/**
 *
 * @author 海
 */
public class JobSet extends ResultSet {

    public JobSet() throws DBException {
        super(new IntegerBinding(), new StringBinding(), null);
    }

    public DownloadJob pop() throws DBException {
        CursorConfig cursorConfig = new CursorConfig();
        cursorConfig.setWriteCursor(true);
        ResultReader reader = getReader(cursorConfig);
        if (reader.next()) {
            String value = (String) reader.getValue();
            int level = (Integer) reader.getKey();
            reader.delete();
            reader.close();
            return new DownloadJob(value, level);
        }
        reader.close();
        return null;
    }
}
