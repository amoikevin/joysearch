/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.db.entry;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.index.db.entry.Hits;

/**
 *
 * @author 海
 */
public class HitsBinding extends TupleBinding {

    @Override
    public Object entryToObject(TupleInput in) {
        return new Hits(in.getBufferBytes());
    }

    @Override
    public void objectToEntry(Object obj, TupleOutput out) {
        try {
            Hits hits = (Hits) obj;
            out.write(hits.toBytes());
        } catch (IOException ex) {
            Logger.getLogger(HitsBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
