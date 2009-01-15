/*
 * BufferReader.java
 *
 * Created on 2007年6月12日, 上午10:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joy.query.core;

import org.joy.query.core.ResultEntry;

/**
 *
 * @author AC
 */
public class BufferReader {
    private ResultsBuffer buffer;
    private int pageSize;
    private CursorsHandler cursors;
    /** Creates a new instance of BufferReader */
    public BufferReader(ResultsBuffer buffer,CursorsHandler cursors,int pageSize){
        this.buffer= buffer;
        this.pageSize = pageSize;
        this.cursors= cursors;
    }
    
    public ResultEntry[] getPage(int pageNo){
        int start = pageSize*pageNo;
        int end  = start+pageSize;
        return buffer.get(cursors,start,end);
    }
}
