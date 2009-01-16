/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.core;

import org.joy.index.db.entry.Hits;
import java.util.HashSet;
import org.joy.deployer.Deployer;
import java.util.Arrays;
import org.joy.analyzer.html.Anchor;
import org.joy.analyzer.html.HTMLDocument;
import org.joy.index.core.DBGroup;
import org.joy.index.core.DBGroupProxy;
import org.joy.index.core.IndexJob;
import org.joy.index.db.entry.Document;
import org.joy.rank.core.LinkSystemAnalyzer;
import org.joy.scan.Utility;
import org.joy.spider.core.SpiderGroup;
import org.joy.spider.core.SpiderGroupProxy;
import org.joy.spider.core.DownloadJob;
import spliter.Spliter;

/**
 *
 * @author 海
 */
public class Deliverer {

    private DBGroupProxy dbG;
    private LinkSystemAnalyzer rank;
    private SpiderGroupProxy sG;
    private final HashSet<String> allowed = new HashSet<String>();
    private final HashSet<String> denied = new HashSet<String>();
    //负索引表
    private Spliter spliter;

    public Deliverer(DBGroup dbG, LinkSystemAnalyzer rank, SpiderGroup sG) {
        this.rank = rank;
        this.dbG = new DBGroupProxy(dbG);
        this.sG = new SpiderGroupProxy(sG);
        this.spliter = new Spliter();
        spliter.setMode(Spliter.SPLIT);
    }

    private boolean verifyURL(Anchor link) {
        if (!Utility.isValidLink(link.getURL())) {
            return false;
        }
        synchronized (denied) {
            //检查Host
            for (String host : denied) {
                if (link.getURL().indexOf(host) != -1) {
                    return false;
                }
            }
        }
        synchronized (allowed) {
            //检查Host
            for (String host : allowed) {
                if (link.getURL().indexOf(host) != -1) {
                    return true;
                }
            }
            return false;
        }
    }

    private void addLinks(String URL, int level,HTMLDocument doc) {
        //添加链接引用
        for (Anchor link : doc.getAnchors()) {
            if (!verifyURL(link)) {
                continue;
            }
            //注册链接
            try {
                //生成Hits
                HitsGenerator gen = new HitsGenerator(link.getText(), link.getURL());

                //生成Hits
                Hits hs = gen.genHits(getWordSet(link.getText()), .8f);
                
                if (hs != null && hs.size() != 0) {
                    //把链接信息集成到全文索引
                    dbG.putJob(new IndexJob(link.getURL(), hs));
                    //System.out.println("包含的连接"+link.getURL());
                
                }
                //寻找下载资源下载之
                 sG.putJob(new DownloadJob(link.getURL(),level+1));
                //添加链接信息到PageRank计算器
                rank.addLink(URL, link.getURL());
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
    }

    private String[] getWordSet(String text) {
        String[] words = spliter.split2(text);
        HashSet<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(words));
        return set.toArray(new String[0]);
    }

    private void addHits(String URL, HTMLDocument  doc) {
        try {
            //抽取网页文本
            String text = doc.getBody();
            //分词
            HitsGenerator gen = new HitsGenerator(text);
           
            Hits hs = new Hits();
            //处理每一个段落的关键字，并赋予权值，添加入Hits
            for (org.joy.analyzer.Paragraph p : doc.getParagraphs()) {
                if (p.getOffset() > 4096) {
                    break;
                }
                String[] wordSet = getWordSet(p.getText());
                Hits t = gen.genHits(wordSet, (float)p.getWeight(), p.getOffset(), p.getOffset()+p.getText().length());
                if (t != null) {
                    hs.combine(t);
                }
            }
            //综合两段Hits
            dbG.putJob(new IndexJob(URL, hs, new Document(doc.getTitle(),text )));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deliver(DocumentJob job, HTMLDocument doc) {
        if (!verifyURL(new Anchor(null,job.getURL()))) {
            System.out.println(job.getURL() + "已被排除");
            return;
        }
        Deployer.logger.info("分析" + job.getURL());
        addHits(job.getURL(), doc);
        addLinks(job.getURL(),job.getLevel(),doc);
    }

    public void shutdown() {
        spliter.cleanup();
    }

    public HashSet<String> getAllowed() {
        return allowed;
    }

    public HashSet<String> getDenied() {
        return denied;
    }
}
