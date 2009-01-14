/*
 * ResultsBuffer.java
 *
 * Created on 2007年6月9日, 下午7:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import org.joy.query.ResultEntry;

/**
 *
 * @author AC
 */
public class ResultsBuffer {
    private SortedSet<ResultEntry>[] serverBuffers;
    
    private LinkedList<ResultEntry> buffer = new LinkedList<ResultEntry>();
    //每次读取每个server最多多少个entries在缓冲里
    private final static int MAX_INBUFFER_NUM = 128;
    //每读取一个块，最多可以确定多少个entries的顺序
    private final static int MAX_VALID_SORTED_NUM =MAX_INBUFFER_NUM ;
    
    private int feedBuffer(CursorsHandler cursors){
        cursors.loadNext(serverBuffers,MAX_INBUFFER_NUM);
        
        int numFeed = 0;
        for(int i=0;i<MAX_INBUFFER_NUM;i++){
            double minRate = Double.MAX_VALUE;
            //包含有最大值的 serverName
            int minIndex = 0;
            //遍历每个服务器的缓冲
            int j = 0;
            for(SortedSet<ResultEntry> buffer:serverBuffers){
                if(buffer.size() > 0 ){
                    if(buffer.last().getRate()<minRate) {
                        minIndex = j;
                        minRate=buffer.last().getRate();
                    }
                }
                j++;
            }
            if(minRate == Double.MAX_VALUE)
                //如果都没有多余的entry,跳出
                break;
            
            ResultEntry max = serverBuffers[minIndex].last();
            serverBuffers[minIndex].remove(max);
            buffer.add(max);
            //增加看看添加了多少个 缓冲条目
            numFeed++;
        }
        //如果一个条目都没增加，返回false
        return numFeed;
    }
    private void createBuffer(int numCursors){
        serverBuffers = new SortedSet[numCursors];
        //预先为每个server创建好相应的buffer
        for(int i=0;i<numCursors;i++){
            serverBuffers[i] = new TreeSet<ResultEntry>(new ResultEntryComparator());
        }
    }
    ResultEntry[] get(CursorsHandler cursors,int startRank,int endRank){
        if(serverBuffers == null)
            createBuffer(cursors.numCursors());
        ArrayList<ResultEntry> entries = new ArrayList<ResultEntry>();
        boolean noMoreEntries = false;
        for(int i=startRank;i<endRank;i++){
            
            while(i > buffer.size()-1){
                //如果没有更多的缓冲可以读取，返回现有的数据。
                if(noMoreEntries)
                    return entries.toArray(new ResultEntry[0]);
                //如果缓冲区不够，读取之
                int numFeed =feedBuffer(cursors);
                if( numFeed<MAX_INBUFFER_NUM){
                    noMoreEntries=true;
                    //如果没有了返回
                    break;
                }
                //如果仍然不够
            }
            if(buffer.size() >i)
                entries.add( buffer.get(i));
        }
        
        if(entries.size() == 0)
            return null;
        return entries.toArray(new ResultEntry[0]);
    }
}
