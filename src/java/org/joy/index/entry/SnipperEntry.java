/*
 * Snipper.java
 *
 * Created on 2008年2月9日, 下午12:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.index.entry;

import java.io.Serializable;

/**
 *
 * @author 海
 */
public class SnipperEntry implements Serializable{
    private String snipper;
    private String title;
    
    public SnipperEntry(String snipper,String title){
        setTitle(title);
        setSnipper(snipper);
    }
    public String getSnipper() {
        return snipper;
    }

    public void setSnipper(String snipper) {
        this.snipper = snipper;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
}
