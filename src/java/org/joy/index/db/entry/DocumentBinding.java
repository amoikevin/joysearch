/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.index.db.entry;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 *
 * @author Lamfeeling
 */
public class DocumentBinding extends TupleBinding{

    @Override
    public Object entryToObject(TupleInput in) {
        return new Document(in);
    }

    @Override
    public void objectToEntry(Object obj, TupleOutput out) {
        Document doc = (Document) obj;
        doc.serialize(out);
    }

}
