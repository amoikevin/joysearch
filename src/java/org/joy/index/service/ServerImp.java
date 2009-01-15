/*
 * ServerImp.java
 *
 * Created on 2007年12月13日, 下午4:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.service;

import org.joy.query.SearchServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joy.group.service.Job;
import org.joy.query.Cursor;
import org.joy.index.db.Cache;
import org.joy.index.db.DocumentCommand;
import org.joy.index.db.DocumentConnection;
import org.joy.index.db.Platform;
import org.joy.index.db.RIStatistics;
import org.joy.index.db.RIndexConnection;
import org.joy.index.db.Searcher;
import org.joy.index.db.entry.Document;
import org.joy.query.ResultCursor;
import com.sleepycat.db.LockMode;
import org.joy.query.SnipperEntry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.joy.db.Command;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.index.service.Database;
import org.joy.index.service.IndexJob;
import org.joy.index.service.RankUpdateJob;
import org.joy.deployer.Deployer;
import org.joy.index.util.RIS;
 
/**
 *
 * @author 海
 */
public class ServerImp extends UnicastRemoteObject implements SearchServer, Database {

    private Platform platform;
    private DocumentConnection docConn;
    private RIndexConnection rIndexConn;
    private Cache cache;
    private Searcher searcher;

    public ServerImp() throws DBException, RemoteException {
        super();
        //启动数据库平台
        platform = new Platform();
        docConn = (DocumentConnection) platform.open("Doc");
        rIndexConn = (RIndexConnection) platform.open("RIndex");
        //读取文件中的RIStatics
        searcher = new Searcher(docConn, rIndexConn, RIS.load());
        cache = new Cache(rIndexConn);
    }

    public void index() throws DBException, RemoteException {
        searcher.reset();
        RIStatistics ris = cache.loadHits();
        RIS.save(ris);
        searcher.updateSorter(ris);
    }

    public Cursor search(String[] keywords) throws DBException, RemoteException {
        ResultReader reader = searcher.search(keywords);
        if (reader != null) {
            return new ResultCursor(reader, new DocumentCommand(docConn));
        }
        return null;
    }

    public void clear() throws DBException, RemoteException {
        cache.clear();
        DocumentCommand docCmd = new DocumentCommand(docConn);
        docCmd.clear();
        Command riCmd = new Command(rIndexConn);
        riCmd.clear();
        searcher.reset();
    }

    public SnipperEntry getSnipper(String[] keywords, String URL) throws DBException {
        if (keywords != null) {
            DocumentCommand docCmd = new DocumentCommand(docConn);
            Document doc = (Document) docCmd.getEntry(URL);
            return new SnipperEntry(searcher.getSnipper(keywords, URL), doc.getTitle());
        }
        return null;
    }

    public String getTitle(String URL) throws DBException {
        DocumentCommand docCmd = new DocumentCommand(docConn);
        Document doc = (Document) docCmd.getEntry(URL);
        return doc.getTitle();
    }

    public String queryURL(int hash) throws DBException, RemoteException {
        DocumentCommand docCmd = new DocumentCommand(docConn);
        return docCmd.getURLByHash(hash, LockMode.DEFAULT);
    }

    public void putJob(Job j) throws RemoteException {
        try {
            if (j instanceof IndexJob) {
                IndexJob job = (IndexJob) j;
                Command docCmd = new Command(docConn);

                ResultReader reader = docCmd.search(job.getURL(),true);
                if (!reader.next() ||
                        ((Document) reader.getValue()).getText().equals(Document.NOTHING)) {
                    Document doc;
                    if (job.getDocument() == null) {
                        doc = new Document();
                    } else {
                        doc = (Document) job.getDocument();
                    }
                    reader.put(job.getURL(), doc);
                   // docCmd.setEntry(job.getURL(), doc);
                }

                Deployer.logger.info(job.getURL());
                reader.close();
                cache.put(job.getURL(), job.getHits());
            } else if (j instanceof RankUpdateJob) {
                RankUpdateJob job = (RankUpdateJob)j;
                DocumentCommand docCmd = new DocumentCommand(docConn);
                ResultReader reader = docCmd.search(job.getURL(), true);
                if (!reader.next()) {
                    reader.close();
                    //System.out.println("不存在"+job.getURL());
                    Deployer.logger.warn("警告：试图给不存的URL"+job.getURL()+"更新Rank");
                    //throw new IllegalArgumentException();
                }
                Document docInfo = (Document) reader.getValue();
                docInfo.setRank(job.getRank());
                reader.put(job.getURL(), docInfo);
                reader.close();
            }
            return;
        } catch (DBException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getName() {
        return System.getProperties().toString();
    }

    public void shutdown() throws DBException {
        docConn.setFree();
        rIndexConn.setFree();
        platform.syncAndClose();
    }

}
