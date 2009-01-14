/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.index.db;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author Lamfeeling
 */
public class RIStatistics implements Serializable{
    private int total;
    private Hashtable<String,Integer> ITFTable;

    public Hashtable<String, Integer> getITFTable() {
        return ITFTable;
    }

    public int getTotal() {
        return total;
    }

    public RIStatistics(int total, Hashtable<String, Integer> ITFTable) {
        this.total = total;
        this.ITFTable = ITFTable;
    }
    
}
