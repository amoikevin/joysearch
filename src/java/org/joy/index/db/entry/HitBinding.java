/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.index.db.entry;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.index.db.entry.Hit;

/**
 *
 * @author 海
 */
public class HitBinding extends TupleBinding {

    @Override
    public Object entryToObject(TupleInput in) {
        ByteArrayInputStream bais = new ByteArrayInputStream(in.getBufferBytes());
        DataInputStream dis = new DataInputStream(bais);
        Hit hit = new Hit(dis);
        try {
            dis.close();
            bais.close();
            return hit;
        } catch (IOException ex) {
            Logger.getLogger(HitBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void objectToEntry(Object obj, TupleOutput out) {
        try {
            Hit h = (Hit) obj;
            out.write(h.toBytes());
        } catch (IOException ex) {
            Logger.getLogger(HitBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
