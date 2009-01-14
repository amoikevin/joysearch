/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.query;

import java.io.Serializable;

/**
 *
 * @author 海
 */
public class Result extends ResultEntry implements Serializable {

    private SnipperEntry snipper;

    public Result(SnipperEntry snipper, ResultEntry e) {
        super(e.getURL(), e.getRate());
        this.snipper = snipper;
    }

    public String getSnipper() {
        return snipper.getSnipper();
    }

    public void setSnipper(SnipperEntry snipper) {
        this.snipper = snipper;
    }

    public String getTitle() {
        return snipper.getTitle();
    }

    public void setTitle(String title) {
        this.snipper.setTitle(title);
    }
}
