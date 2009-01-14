/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joy.query;

import org.joy.query.Filter;

/**
 *
 * @author Lamfeeling
 */
public class URLFilter extends  Filter{
    private String key;
    public URLFilter(String key){
        this.key = key;
    }
    public boolean filter(String URL) {
        return URL.indexOf(key)==-1;
    }

}
