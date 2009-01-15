/*
 * ResultEntry.java
 *
 * Created on 2007年8月4日, 下午9:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.query.core;

import java.io.Serializable;

/**
 * 结果条目，用来保存指定关键字集对应的URL，和其排名指数
 * @author 海
 */
public class ResultEntry implements Serializable{
    private String URL;
    private double rate;
    /** Creates a new instance of ResultEntry */
    public ResultEntry(String URL,double rate) {
        this.URL = URL;
        this.rate =  rate;
    }
    
    /**
     * 获取这个结果条目当中的URL
     * @return 
     */
    public String getURL() {
        return URL;
    }
    
    /**
     * 获取该URL在此次搜索当中的排名信息Rate
     * @return 获取该URL在此次搜索当中的排名信息Rate
     */
    public double getRate() {
        return rate;
    }
    public String toString(){
        return URL+":"+rate;
    }
}
