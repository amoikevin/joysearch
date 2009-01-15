/*
 * Server.java
 *
 * Created on 2007年6月7日, 下午8:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.query.core;

import org.joy.query.core.SnipperEntry;
import org.joy.query.core.Filter;
import org.joy.query.core.ResultEntry;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.joy.index.core.DBGroupProxy;
import org.joy.query.core.SearchServer;
import org.joy.query.core.BufferReader;
import org.joy.query.core.CursorsHandler;
import org.joy.query.core.ResultsBuffer;
import org.joy.query.core.URLFilter;
import org.joy.query.core.Result;
import spliter.Spliter;

/**
 *
 * @author AC
 */
public class ServerImp extends UnicastRemoteObject implements QueryServer {

    Hashtable<String, SearchServer> servers = new Hashtable<String, SearchServer>();
    Hashtable<String, ResultsBuffer> bufferTable = new Hashtable<String, ResultsBuffer>();
    Hashtable<String, Timer> timerTable = new Hashtable<String, Timer>();
    Hashtable<String, CursorsHandler> cursorsTable = new Hashtable<String, CursorsHandler>();
    Spliter spliter = new Spliter();
    DBGroupProxy urlIndex;

    public ServerImp(DBGroupProxy urlIndexer) throws RemoteException {
        spliter.setMode(Spliter.SPLIT);
        this.urlIndex = urlIndexer;
    }

    public void addServer(String serverAddress) {
        try {
            System.out.println(serverAddress);
            SearchServer server = (SearchServer)Naming.lookup(serverAddress);
            if (servers.contains(server)) {
                return;
            }
            servers.put(server.getName(), server);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "发现错误在"+this.getClass().toString() +"\n"+ ex);
            ex.printStackTrace();
        }
    }

    public void removeServer(String serverAddress) {
        SearchServer server;
        try {
            server = (SearchServer) Naming.lookup(serverAddress);
            servers.remove(server);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String[] startSearch(String searchString) {
        Filter filter = null;
        String validPart = searchString;
        if (searchString.indexOf("site:") != -1) {
            int index = searchString.indexOf(" site:");
            String host = searchString.substring(index + " site:".length(), searchString.length());
            validPart = searchString.substring(0, index);
            filter = new URLFilter(host);
        }
        //如果已经存在这个搜索字符串的索引，直接返回
        if (bufferTable.get(searchString) != null) {
            return spliter.split2(validPart);
        }
        //建立临时结果列表的项目
        bufferTable.put(searchString, new ResultsBuffer());
        //创建定时器，到时间关闭搜索
        timerTable.put(searchString, new Timer(true));
        System.out.println(validPart);
        String[] keywords = spliter.split2(validPart);
        if (keywords.length == 0) {
            return null;
        }
        SearchServer[] servers = this.servers.values().toArray(new SearchServer[0]);
        //打开cursors
        cursorsTable.put(searchString, new CursorsHandler(keywords, filter, servers));
        return keywords;
    }

    public Result[] getPage(String searchString, int pageNo) throws RemoteException {
        long start = Calendar.getInstance().getTimeInMillis();
        //确保缓冲表有足够的entry
        System.out.println("受到请求" + searchString);
        String[] keywords = startSearch(searchString);
        System.out.println("end");
        for (String s : keywords) {
            System.out.println(s);
        }
        ResultsBuffer buffer = bufferTable.get(searchString);

        CursorsHandler cursors = null;

        //如果还是没有cursorsHandle,说明该请求不包含有效的关键词。输出之
        cursors = cursorsTable.get(searchString);
        if (cursors == null) {
            System.out.println("该请求不包含有效的关键词");
            return null;
        }

        BufferReader reader = new BufferReader(buffer, cursors, 10);
        //延长定时器
        Timer timer = timerTable.get(searchString);
        timer.cancel();
        //新建一个计时器
        timer = new Timer(true);
        timer.schedule(new EndSearchTask(this, searchString), 1000 * 60 * 5);
        timerTable.put(searchString, timer);
        System.out.println("用时" + (Calendar.getInstance().getTimeInMillis() - start));
        Result[] res = getSnipper(keywords, reader.getPage(pageNo));
        System.out.println("找到" + res.length);
        return res;
    }

    private Result[] getSnipper(String[] keywords, ResultEntry[] entries) {
        Vector<Result> res = new Vector<Result>();
        for (ResultEntry e : entries) {
            try {
                SearchServer s = (SearchServer) Naming.lookup(urlIndex.Query(e.getURL()));
                SnipperEntry snipper = s.getSnipper(keywords, e.getURL());
                res.add(new Result(snipper, e));
            } catch (Exception ex) {
                Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("FUCK" + e.getURL());
            }
        }
        return res.toArray(new Result[0]);
    }

    private void endSearch(String searchString) {
        CursorsHandler cursors = cursorsTable.get(searchString);
        cursors.close();
        cursorsTable.remove(searchString);
        bufferTable.remove(searchString);
        timerTable.remove(searchString);
    }

    public void shutdown() {
        Collection<CursorsHandler> cursorsCollection = cursorsTable.values();
        for (CursorsHandler cursors : cursorsCollection) {
            cursors.close();
        }
        spliter.cleanup();
    }

    public void clear() {
        //删除所有临时缓冲和游标
        Collection<CursorsHandler> cursorsCollection = cursorsTable.values();
        for (CursorsHandler cursors : cursorsCollection) {
            cursors.close();
        }
        cursorsTable.clear();
        bufferTable.clear();
        //取消所有定时器
        for (Timer timer : timerTable.values()) {
            timer.cancel();
        }
        timerTable.clear();

//        Collection<SearchServer> servers = this.servers.values();
//        for(SearchServer server:servers){
//            try {
//                server.index();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                throw new CenterException();
//            }
//        }
    }

    private class EndSearchTask extends TimerTask {

        private ServerImp center;
        private String searchString;

        public EndSearchTask(ServerImp center, String searchString) {
            this.center = center;
            this.searchString = searchString;
        }

        @Override
        public void run() {
            try {
                center.endSearch(searchString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
