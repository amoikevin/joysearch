/*
 * Searcher.java
 *
 * Created on 2007年12月12日, 下午5:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joy.index.db;

import org.joy.index.util.ProxCalculator;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;
import org.joy.db.Command;
import org.joy.db.DBException;
import org.joy.db.ResultReader;
import org.joy.db.ResultSet;
import org.joy.index.db.entry.CompactHit;
import org.joy.index.db.entry.Document;

/**
 *
 * @author 海
 */
public class Searcher {

    private DocumentConnection docConn;
    private RIndexConnection riConn;
    private Hashtable<String, ResultSet> results = new Hashtable<String, ResultSet>();
    private Hashtable<String, ResultSet> hitsResults = new Hashtable<String, ResultSet>();
    private Sorter sorter;

    public Searcher(DocumentConnection docConn,
            RIndexConnection rIndexConn, RIStatistics stat) throws DBException {
        this.docConn = docConn;
        this.riConn = rIndexConn;
        updateSorter(stat);
    }

    public void updateSorter(RIStatistics stat) throws DBException {
        //生成RIStatistics
        this.sorter = new Sorter(docConn, riConn, stat);
    }

    private String getSearchString(String[] keywords) {
        String searchString = "";
        for (String keyword : keywords) {
            searchString += keyword + "|";
        }
        return searchString;
    }

    public ResultReader search(String[] keywords) throws DBException {
        String searchString = getSearchString(keywords);

        if (results.get(searchString) != null) {
            return results.get(searchString).getReader();
        }

        //需要新建一个ResultSet
        RICommand ric = new RICommand(riConn);
        ResultSet set = ric.search(keywords);
        ResultSet finalSet = sorter.sort(set.getReader(), keywords);
        //finalSet.printAll();
        //System.out.println("---------******************************************");
        results.put(searchString, finalSet);
        hitsResults.put(searchString, set);
//        System.out.println("--------------------------------------------------------------------");
        return finalSet.getReader();
    }
    //public final static int MAX_SNIPPER_LENGTH = 100;

    
    private final static int beginStride = 20;
    private final static int endStride = 100;
    
    public String getSnipper(String[] keywords, String URL) throws DBException {
        String searchString = getSearchString(keywords);
        ResultSet rs = hitsResults.get(searchString);
        if (rs == null) {
            return null;
        }
        //获取hits
        Vector<CompactHit> hits = (Vector<CompactHit>) rs.get(URL.hashCode());

        //过滤掉所有的负数
        for (CompactHit hit : hits) {
            if (hit.removeNegpos() == 0) {
                return "";
            }
        }
        //获取docinfo
        Command cmd = new Command(docConn);
        Document info = (Document) cmd.getEntry(URL);
        String text = info.getText();

        if (text.equals(Document.NOTHING)) {
            return Document.NOTHING;
        }
        ////去掉标题
        //text = text.substring(info.getTitle().length(),text.length());
        int[] res = ProxCalculator.getProxArray(hits.toArray(new CompactHit[0]));

        //判断开始和结束标记
        TreeSet<Integer> pos = new TreeSet<Integer>();
        for (int i = 0; i < res.length; i++) {
            int j = res[i] - beginStride > 0 ? res[i] - beginStride : 0;
            int k = res[i] + endStride < text.length() ? res[i] + endStride : text.length();
            if (pos.size() > 0) {
                if (j > pos.last()) {
                    pos.add(j);
                    pos.add(k);
                } else {
                    pos.remove(pos.last());
                    pos.add(k);
                }
            } else {
                pos.add(j);
                pos.add(k);
            }

        }
        StringBuilder builder = new StringBuilder();
        Integer[] snipperRes = pos.toArray(new Integer[0]);
        for (int i = 0; i < snipperRes.length; i += 2) {
            if (i > 0) {
                builder.append("......");
            }
            builder.append(text.substring(snipperRes[i], snipperRes[i + 1]));
        }
        
        int lastIndex = res[0] - snipperRes[0];
        for (String s : keywords) {
            int index = builder.toString().toLowerCase().indexOf(s, lastIndex);
            if (index == -1) {
                index = builder.toString().toLowerCase().indexOf(s);
                if (index == -1) {
                    break;
                }
            }
            lastIndex = index;
            builder.insert(index, "<b><font color=red>");
            builder.insert(index + s.length() + "<b><font color=red>".length(), "</font></b>");
        }
        return builder.toString();
    }

    public void reset() throws DBException {
        for (ResultSet rs : results.values()) {
            rs.close();
        }
        for (ResultSet rs : hitsResults.values()) {
            rs.close();
        }
        results.clear();
        hitsResults.clear();
    }
}
