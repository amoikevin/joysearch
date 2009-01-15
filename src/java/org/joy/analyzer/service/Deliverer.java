/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.analyzer.service;

import org.joy.index.db.entry.Hits;
import java.util.HashSet;
import org.joy.deployer.Deployer;
import java.util.Arrays;
import org.joy.index.service.DBGroup;
import org.joy.index.service.DBGroupProxy;
import org.joy.index.service.IndexJob;
import org.joy.index.db.entry.Document;
import org.joy.rank.service.LinkSystemAnalyzer;
import org.joy.scan.HyperLink;
import org.joy.scan.Paragraph;
import org.joy.scan.HTMLParser;
import org.joy.scan.Utility;
import org.joy.spider.service.SpiderGroup;
import org.joy.spider.service.SpiderGroupProxy;
import org.joy.spider.service.DownloadJob;
import spliter.Spliter;

/**
 *
 * @author 海
 */
public class Deliverer {

    private DBGroupProxy dbG;
    private LinkSystemAnalyzer rank;
    private SpiderGroupProxy sG;
    private HashSet<String> allowed = new HashSet<String>();
    private HashSet<String> denied = new HashSet<String>();
    //负索引表
    private Spliter spliter;

    public Deliverer(DBGroup dbG, LinkSystemAnalyzer rank, SpiderGroup sG) {
        this.rank = rank;
        this.dbG = new DBGroupProxy(dbG);
        this.sG = new SpiderGroupProxy(sG);
        this.spliter = new Spliter();
        spliter.setMode(Spliter.SPLIT);
    }

    private boolean verifyURL(HyperLink link) {
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

    private void addLinks(String URL, int level,HTMLParser parser) {
        //添加链接引用
        for (HyperLink link : parser.getLinks()) {
            if (!verifyURL(link)) {
                continue;
            }
            //注册链接
            try {
                //生成Hits
                HitsGenerator gen = new HitsGenerator(link.getText(), link.getURL());

                //生成Hits
                Hits hs = gen.genHits(getWordSet(link.getText()), HTMLParser.REF_WEIGHT);
                
                if (hs != null && hs.size() != 0) {
                    //把链接信息集成到全文索引
                    dbG.putJob(new IndexJob(link.getURL(), hs));
                    System.out.println("包含的连接"+link.getURL());
                
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

    private void addHits(String URL, HTMLParser parser) {
        try {
            //抽取网页文本
            String text = parser.getBody();
            //分词
            HitsGenerator gen = new HitsGenerator(text);
           
            Hits hs = new Hits();
            //处理每一个段落的关键字，并赋予权值，添加入Hits
            int begin = 0, end = 0;
            for (Paragraph p : parser.getParagraph()) {
                if (begin > 4096) {
                    break;
                }
                end += p.getText().length();
                String[] wordSet = getWordSet(p.getText());
                Hits t = gen.genHits(wordSet, p.getWeight(), begin, end);
                if (t != null) {
                    hs.combine(t);
                }
                begin += p.getText().length();
            }
            //综合两段Hits
            dbG.putJob(new IndexJob(URL, hs, new Document(parser.getTitle(), parser.getBody())));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deliver(DocumentJob job, HTMLParser parser) {
        if (!verifyURL(new HyperLink(job.getURL(), null))) {
            System.out.println(job.getURL() + "已被排除");
            return;
        }
        Deployer.logger.info("分析" + job.getURL());
        addHits(job.getURL(), parser);
        addLinks(job.getURL(),job.getLevel(),parser);
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
