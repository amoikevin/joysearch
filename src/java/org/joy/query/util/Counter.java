/*
 * Counter.java
 *
 * Created on 2007年6月14日, 下午2:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.query.util;

/**
 *
 * @author AC
 */
public class Counter {
    public Counter(int i){
        this.i=i;
    }
    int i;
    public synchronized void increase(){
        i++;
    }
    public synchronized void decrease(){
        i--;
    }
    public synchronized int getVal()
    {
        return i;
    }
}
