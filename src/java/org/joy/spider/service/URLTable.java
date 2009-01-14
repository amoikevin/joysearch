/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.spider.service;

import com.sleepycat.bind.tuple.BooleanBinding;
import com.sleepycat.bind.tuple.ShortBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.db.CursorConfig;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;
import org.joy.deployer.Deployer;

/**
 * 网址缓冲，所有被分析器发现的网址都在这里缓冲，如果过了一定时间还是没有被下载成功，就会自动重新被分配
 * @author Lamfeeling
 */
public class URLTable {

    private ResultSet downloading;
    private ResultSet dead;
    private ResultSet downloaded;
    private long UPDATE_DELAY = 1000 * 60 * 30;

    public URLTable() throws DBException {
        downloading = new ResultSet(new StringBinding(),
                new ShortBinding());
        dead = new ResultSet(new StringBinding(),
                /*boolean 字段不表示任何信息*/
                new BooleanBinding());
        downloaded = new ResultSet(new StringBinding(),
                /*boolean 字段不表示任何信息*/
                new BooleanBinding());
        //设定定时器，定时更新
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    Deployer.logger.info("正在删除超时的URL");
                    update();
                    Deployer.logger.info("更新结束");
                } catch (DBException ex) {
                    Logger.getLogger(URLTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, UPDATE_DELAY, UPDATE_DELAY);
    }

    /**
     * 向Cache中添加一个URL
     */
    public boolean request(String URL) throws DBException {
        if (verify(URL)) {
            downloading.put(URL, 0);
            return true;
        }
        return false;
    }

    private boolean verify(String URL) throws DBException {
        //是否是死链接,或者已经安排了下载,或者已经下载了
        if (dead.get(URL) != null) {
            return false;
        } else if (downloaded.get(URL) != null) {
            return false;
        } else if (downloading.get(URL) != null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 从新鲜的队列中删除一个URL，原因是这个URL被成功的下载，分析。
     * @param URL
     * @throws org.joy.db.DBException
     */
    public void finish(String URL) throws DBException {
        downloading.remove(URL);
        downloaded.put(URL, false);
    }

    /**
     * 导出新鲜的URL，每导出一次，这些URL的寿命就+1
     * @return 返回新鲜但是还未被下载的URL
     * @throws org.joy.db.DBException
     */
    public String[] export() throws DBException {
        Vector<String> URLs = new Vector<String>();
        CursorConfig cc = new CursorConfig();
        cc.setWriteCursor(true);
        ResultReader reader = downloading.getReader(cc);
        while (reader.next()) {
            //导出之前把这个URL的寿命+1
            String URL = (String) reader.getKey();
            short age = (Short) reader.getValue();
            reader.delete();
            age++;
            //更新这个URL的Age
            reader.put(URL, age);
            URLs.add(URL);
        }
        reader.close();
        return URLs.toArray(new String[0]);
    }

    /**
     * 定期删掉始终不能被下载的URL,（淘汰过老的URL）
     * @throws org.joy.db.DBException
     */
    private void update() throws DBException {
        CursorConfig cc = new CursorConfig();
        cc.setWriteCursor(true);
        ResultReader reader = downloading.getReader(cc);
        while (reader.next()) {
            //如果被分发了5次还是没有被下载成功，则被删掉.
            if ((Short) reader.getValue() >= 5) {
                //添加一个死连接
                Deployer.logger.info(reader.getKey() + "被投入死链接");
                dead.put(reader.getKey(), false);
                reader.delete();
            }
        }
        reader.close();
    }

    public void reset() throws DBException{
        downloading.clear();
        downloaded.clear();
        dead.clear();
    }
    public void close() throws DBException {
        downloading.close();
        downloaded.close();
        dead.close();
    }
}
