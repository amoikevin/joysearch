/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.db.entry;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lamfeeling
 */
public class CHitVectorBinding extends TupleBinding {

    @Override
    public Object entryToObject(TupleInput in) {
        int size = in.readInt();
        Vector<CompactHit> hits = new Vector(size);
        for (int i = 0; i < size; i++) {
            try {
                int length = in.readInt();
                byte[] b = new byte[length];
                in.read(b);
                hits.add(new CompactHit(b));
            } catch (IOException ex) {
                Logger.getLogger(CHitVectorBinding.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return hits;
    }

    @Override
    public void objectToEntry(Object obj, TupleOutput out) {
        Vector<CompactHit> hits = (Vector<CompactHit>) obj;
        out.writeInt(hits.size());
        for (CompactHit h : hits) {
            try {
                out.writeInt(h.getData().length);
                out.write(h.getData());
            } catch (IOException ex) {
                Logger.getLogger(CHitVectorBinding.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
