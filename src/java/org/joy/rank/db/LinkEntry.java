/*
 * LinkEntry.java
 *
 * Created on 2007年11月26日, 上午9:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.rank.db;

import java.io.Serializable;
import java.util.HashSet;
import org.joy.dblookup.service.UID;

/**
 *
 * @author 海
 */
public class LinkEntry implements Serializable{
    public HashSet<Long> links=new HashSet<Long>();
    public void addLink(String link){
        links.add(UID.from(link));
    }
    public void removeLink(String link){
        links.remove(UID.from(link));
    }
    public HashSet<Long> getLinks(){
        return links;
    }

}
