/*
 * IndexEntryComparator.java
 *
 * Created on 2007年6月9日, 下午9:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.query;

import org.joy.query.ResultEntry;
import java.util.Comparator;

/**
 *
 * @author AC
 */
public class ResultEntryComparator implements  Comparator<ResultEntry>{
    

    public int compare(ResultEntry re1, ResultEntry re2) {
       int cmp = Double.compare(re2.getRate(),re1.getRate());
       if(cmp == 0)
           return re2.getURL().compareTo(re1.getURL());
       return cmp;
    }
    

}
