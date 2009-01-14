/*
 * CursorsHandler.java
 *
 * Created on 2007年6月15日, 下午4:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.query;

import java.rmi.RemoteException;
import java.util.SortedSet;
import java.util.Vector;
import org.joy.query.SearchServer;
import org.joy.query.util.Counter;

/**
 *
 * @author AC
 */
public class CursorsHandler {

    private Vector<Searcher> searchers = new Vector<Searcher>();
    private Filter filter;

    /** Creates a new instance of CursorsHandler */
    public int numCursors() {
        return searchers.size();
    }

    public CursorsHandler(String[] keywords, Filter filter, SearchServer[] servers) {
        //打开搜索游标
        Object waiter = new Object();
        this.filter = filter;
        //初始化searcher对象，传入参数
        Counter numActiveSearcher = new Counter(servers.length);
        for (SearchServer s : servers) {
            //放在一个独立的线程当中打开搜索
            Thread t = new Thread(new Searcher(s, keywords, searchers, numActiveSearcher, waiter));
            t.start();
        }
        if (numActiveSearcher.getVal() != 0) {
            synchronized (waiter) {
                try {
                    //等待通知
                    waiter.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void close() {
        for (Searcher searcher : searchers) {
            searcher.close();
        }
    }

    public void loadNext(SortedSet<ResultEntry>[] serverBuffers, int maxBufferSize) {
        //打开搜索游标
        Object waiter = new Object();
        //初始化searcher对象，传入参数

        Counter numActiveSearcher = new Counter(searchers.size());

        //看看每个server有多少个entries in buffer
        int i = 0;
        for (Searcher searcher : searchers) {
            SortedSet<ResultEntry> buffer = serverBuffers[i];
            searcher.loadNext(buffer,
                    maxBufferSize - buffer.size(), numActiveSearcher, waiter);
            i++;
        }
        if (numActiveSearcher.getVal() != 0) {
            synchronized (waiter) {
                try {
                    waiter.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class Searcher extends Thread {

        private SearchServer s;
        private String[] keywords;
        private Vector<Searcher> searchers;
        private Counter numAliveSearcher;
        private Object handlerWaiter;
        private Object waiter = new Object();
        private boolean closed;
        private int readCount;
        private SortedSet<ResultEntry> buffer;
        private boolean moreWork;
        private Thread workThread;

        public Searcher(SearchServer s, String[] keywords, Vector<Searcher> searchers,
                Counter numAliveSearcher, Object handlerWaiter) {
            super();
            this.s = s;
            this.keywords = keywords;
            this.searchers = searchers;
            this.numAliveSearcher = numAliveSearcher;
            this.handlerWaiter = handlerWaiter;
        }

        private boolean waitForTask() {
            if (moreWork) {
                return true;
            }
            synchronized (waiter) {
                try {
                    waiter.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (closed) {
                    return false;
                }
            }
            return true;
        }

        public void close() {
            synchronized (waiter) {
                closed = true;
                waiter.notify();
            }
            try {
                workThread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println("关闭结束");
        }

        public void loadNext(SortedSet<ResultEntry> buffer, int count,
                Counter numAliveSearcher, Object handlerWaiter) {
            synchronized (waiter) {
                readCount = count;
                this.buffer = buffer;
                this.numAliveSearcher = numAliveSearcher;
                this.handlerWaiter = handlerWaiter;
                moreWork = true;
                waiter.notify();
            }
        }

        public void run() {
            workThread = Thread.currentThread();
            Cursor cursor = null;
            try {
                //打开搜索游标
                cursor = s.search(keywords);
                if (cursor != null) {
                    searchers.add(this);
                }
                synchronized (numAliveSearcher) {
                    numAliveSearcher.decrease();
                    if (numAliveSearcher.getVal() == 0) {
                        synchronized (handlerWaiter) {
                            handlerWaiter.notify();
                        }
                    }
                }
                if (cursor == null) {
                    return;
                }
                //等待任务
                while (waitForTask()) {
                    moreWork = false;
                    ResultEntry[] newEntries = null;
                    if (filter != null) {
                        newEntries = cursor.getNext(readCount, filter);
                    } else {
                        newEntries = cursor.getNext(readCount);
                    }
                    if (newEntries != null) {
                        for (ResultEntry entry : newEntries) {
                            buffer.add(entry);
                        }
                    }
                    //读取完成，减去活着的线程数
                    synchronized (numAliveSearcher) {
                        numAliveSearcher.decrease();
                        //如果全部都读取完
                        if (numAliveSearcher.getVal() == 0) {
                            synchronized (handlerWaiter) {
                                handlerWaiter.notify();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                System.out.println("开始关闭游标");
                try {
                    //全部任务完成，关闭cursor
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}







