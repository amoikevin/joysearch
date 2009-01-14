/*
 * Document.java
 *
 * Created on 2007年12月12日, 上午10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db.entry;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.joy.lookup.service.JobResource;

/**
 *
 * @author 海
 */
public class Document implements JobResource {

    private String title;
    private String text;
    public final static String NOTHING = "Nothing to report";
    private float rank;

    public void serialize(TupleOutput out) {
        out.writeFloat(rank);
        out.writeString(text);
        out.writeString(title);
    }

    public Document(TupleInput in) {

        rank = in.readFloat();
        text = in.readString();
        title = in.readString();
    }

    public Document(String title, String text) {
        setTitle(title);
        setText(text);
        setRank(0.5f);
    }

    public Document() {
        setTitle(null);
        setText(NOTHING);
        setRank(1.0f);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.substring(0, Math.min(text.length(), 4096));
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
