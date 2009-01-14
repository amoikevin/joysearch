/*
 * HyperLink.java
 *
 * Created on 2007年10月26日, 下午5:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.scan;


/**
 *
 * @author 海
 */
public class HyperLink {
    private String URL;
    private String text;
    /** Creates a new instance of HyperLink */
    public HyperLink(String URL,String text) {
        this.URL = URL.toLowerCase();
        this.text = text;
    }

    public String getURL() {
        return URL;
    }

    public String getText() {
        return text;
    }
    
}
