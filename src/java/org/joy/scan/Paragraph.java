/*
 * Paragraph.java
 *
 * Created on 2008年2月8日, 下午10:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.scan;

/**
 *
 * @author 海
 */
public class Paragraph {
    
    private String text;
    private int weight;
    public Paragraph(String text,int weight) {
        this.text = text;
        this.weight = weight;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
}
