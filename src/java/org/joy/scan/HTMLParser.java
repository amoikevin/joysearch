/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.scan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;
import java.util.Vector;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Lamfeeling
 */
public class HTMLParser {

    private DOMParser parser = new DOMParser();
    private static final String[] LARGE_NODES = {"DIV", "TABLE"};
    private static final String[] IMPORTANT_NODES = {"TR", "TD"};
    private static final String[] INFO_NODE = {"P", "SPAN", "BR"};
    private static final String LINK_NODE = "A";
    private static final String[] TITLE = {"H1", "H2", "H3"};
    private static final String[] INVALID_TAGS = {"STYLE", "COMMENT", "SCRIPT", "OPTION"};
    public static final int TITLE_WEIGHT = 200;
    public static final int PLAIN_TEXT_WEIGHT = 30;
    public static final int ANCHOR_TEXT_WEIGHT = 10;
    public static final int REF_WEIGHT = 70;
    private int textSize,  numLinks = 1,  numInfoNodes;
    private String title;
    private String context;
    private String body;
    private TreeMap<Float, Node> nodeList = new TreeMap<Float, Node>();
    private Vector<HyperLink> links = new Vector<HyperLink>();
    private Vector<Paragraph> paragraph = new Vector<Paragraph>();
    //private HashSet<Node> stopNodes = new HashSet<Node>();

    private void reset() {
        //stopNodes.clear();
        nodeList.clear();
        getLinks().clear();
        textSize = 0;
        numLinks = 1;
        numInfoNodes = 0;
    }

    public void parse(String text, String base) throws ParseException, MalformedURLException {
        reset();
        context = base;
        //input file
        BufferedReader in = new BufferedReader(new StringReader(text));
        try {
            parser.parse(new InputSource(in));
        } catch (SAXException ex) {
            throw new ParseException();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Document doc = parser.getDocument();
        //提取标题
        //提取标题
        Node titleNode = doc.getElementsByTagName("TITLE").item(0);
        if (titleNode != null) {
            paragraph.add(new Paragraph(titleNode.getTextContent(), TITLE_WEIGHT));
            this.title = titleNode.getTextContent();
        }
        //获得body节点，以此为根，计算其文本内容
        Node bodyNode = doc.getElementsByTagName("BODY").item(0);
        calCharacter(bodyNode);
        calWeight(bodyNode, 0);
        //获取文本主体
        if (nodeList.lastEntry() != null) {
            this.body = ExtractText(nodeList.lastEntry().getValue());
        } else {
            System.out.println(base);
            this.body = "";
        }
    }

    private String filter(String text) {
        text = text.replaceAll("[^\u4e00-\u9fa5|a-z|A-Z|0-9|０-９,.，。:；|\\s|\\@]", " ");
        text = text.replaceAll("[【】]", " ");
        text = text.replaceAll("\n", " ");
        text = text.replaceAll("\\|", "");
        text = text.replaceAll("\\s+", " ");
        text = text.trim();
        return text;
    }

    private boolean isImportantNode(Element e) {
        for (String s : IMPORTANT_NODES) {
            if (e.getTagName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLargeNode(Element e) {
        for (String s : LARGE_NODES) {
            if (e.getTagName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInfoNode(Element e) {
        for (String s : INFO_NODE) {
            if (e.getTagName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTitle(Element e) {
        for (String s : TITLE) {
            if (e.getTagName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private int numInfoNode(Element e) {
        int num = isInfoNode(e) ? 1 : 0;
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                num += numInfoNode((Element) children.item(i));
            }
        }
        return num;
    }

    private int numLinkNode(Element e) {
        int num = isLinkNode(e) ? 1 : 0;
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                num += numLinkNode((Element) children.item(i));
            }
        }
        return num;
    }

    public int textLength(Node n) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            return filter(n.getTextContent()).trim().length();
        }
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            int length = 0;
            Element elmt = (Element) n;
            if (isInvalidNode(elmt)) {
                return 0;
            }
            NodeList children = n.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                length += textLength(children.item(i));
            }
            return length;
        }
        return 0;
    }

    private String ExtractText(Node n) {
        if (n.getNodeType() == Node.TEXT_NODE) {
            String temp = filter(n.getTextContent()).trim();
            if (!temp.equals("")) {
                getParagraph().add(new Paragraph(temp,
                        isInAnchor ? PLAIN_TEXT_WEIGHT : ANCHOR_TEXT_WEIGHT));
            }
            return temp;
        }
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            if (isInvalidNode((Element)n)) {
                return "";
            }
            Element e = (Element) n;
            if (isLinkNode(e)) {
                isInAnchor = true;
            }
            NodeList children = n.getChildNodes();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < children.getLength(); i++) {
                sb.append(ExtractText(children.item(i)));
            }
            isInAnchor = false;
            return sb.toString();
        }
        return "";
    }
    private boolean isInAnchor;

//    public int anchorTextLength(Node n) {
//        if (n.getNodeType() == Node.TEXT_NODE) {
//            if (isInAnchor) {
//                return filter(n.getTextContent()).trim().length();
//            } else {
//                return 0;
//            }
//        }
//        if (n.getNodeType() == Node.ELEMENT_NODE) {
//            int length = 0;
//            Element elmt = (Element) n;
//            if (isInvalidNode(elmt)) {
//                return 0;
//            }
//            NodeList children = n.getChildNodes();
//            for (int i = 0; i < children.getLength(); i++) {
//                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
//                    Element e = (Element) children.item(i);
//                    if (e.getTagName().equals("A")) {
//                        isInAnchor = true;
//                    }
//                }
//                length += anchorTextLength(children.item(i));
//                isInAnchor = false;
//
//            }
//            return length;
//        }
//        return 0;
//    }
//
//    private boolean isStopNode(Node n) {
//        //如果是超链接Node，则不是StopNode
//        if (n.getNodeType() == Node.ELEMENT_NODE) {
//            if (isLinkNode((Element) n)) {
//                return false;
//            }
//        }
//        return anchorTextLength(n) / (float) textLength(n) > 0.8f;
//    }
    private boolean isLinkNode(Element e) {
        if (e.getTagName().equals(LINK_NODE)) {
            return true;
        }
        return false;
    }

    private boolean isInvalidNode(Element e) {
        for (String s : INVALID_TAGS) {
            if (e.getTagName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private HyperLink generateLink(Element e) {
        try {
            URL u = new URL(new URL(context), e.getAttribute("href"));
            String url = u.toString().toLowerCase();
            url = url.replaceAll("\\.\\/", "");
            //避免主页重定向
            if (url.endsWith("index.htm") || url.endsWith("index.html") || url.endsWith("index.asp") || url.endsWith("default.aspx") || url.endsWith("index.php") || url.endsWith("index.jsp")) {
                url = url.substring(0, url.lastIndexOf("/") + 1);
            } else if (u.getPath().indexOf(".") == -1 && !url.endsWith("/")) //一律写成**/的形式
            {
                url = url + "/";
            }
            return new HyperLink(url, e.getTextContent());
        } catch (MalformedURLException ex) {
        //System.out.println("");
        }
        return null;
    }
    //计算网页文本特征值
    public void calCharacter(Node node) {
        if (node == null) {
            return;
        }
        if (node.getNodeType() == Node.TEXT_NODE) {
            textSize += filter(node.getTextContent()).trim().length();
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;
            //检查非法Node
            if (isInvalidNode(e)) {
                return;
            }
//            if(isStopNode(node)){
//                stopNodes.add(node);
//            }
            numInfoNodes += isInfoNode(e) ? 1 : 0;
            numLinks += isLinkNode(e) ? 1 : 0;
            //如果是超链接，则添加
            if (isLinkNode(e)) {
                HyperLink link = generateLink(e);
                if (link != null) {
                    getLinks().add(link);
                }
            }
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                calCharacter(children.item(i));
            }
        }
    }

    private float fn(float x) {
        if (x > 0.8f) {
            return 0.8f;
        }
        return x;
    }
    //计算网页文本权重
    public void calWeight(Node node, int level) {
        if (node == null) {
            return;
        }
        if (node.getNodeType() == Node.TEXT_NODE) {
            return;
        }

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            float weight = 0;
            Element e = (Element) node;
            if (isInvalidNode(e)) {
                return;
            }
            //计算权重
            weight += isImportantNode(e) ? 1 : 0;
            weight += isLargeNode(e) ? 0.5 : 0;
            weight += isTitle(e) ? 1 : 0;
            weight += fn(numInfoNode(e) / (float) (numInfoNodes+1));
            weight += fn(textLength(e) / (float) textSize);
            weight -= numLinkNode(e) / (float) (numLinks+1);
            nodeList.put(weight, node);

            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                calWeight(children.item(i), level + 1);
            }
        }
    }

    public String getBody() {
        return title + " " + body;
    }

    public static void main(String args[]) throws IOException, ParseException {
        HTMLParser parser = new HTMLParser();
        FileReader reader = new FileReader("a.htm");
        BufferedReader r = new BufferedReader(reader);
        String str = "", line;
        while ((line = r.readLine()) != null) {
            str += line;
        }
        parser.parse(str, "http://localhost");
        System.out.println(parser.getBody());
        System.out.println("http://abc/./pac?a123".replaceAll("\\.\\/", ""));    //  System.out.println((new URL("http://bac.com/a").getFile()));

//        URL u = new URL(new URL("http://lcda/dfas/"), "index.htm");
//        String url = u.toString().toLowerCase();
//        url = url.replaceAll("\\.\\/", "");
//        //避免主页重定向
//        if (url.endsWith("index.htm") || url.endsWith("index.html") || url.endsWith("index.asp") || url.endsWith("default.aspx") || url.endsWith("index.php") || url.endsWith("index.jsp")) {
//            url = url.substring(0, url.lastIndexOf("/") + 1);
//        }
//        //一律写成**/的形式
//        if (u.getPath().indexOf(".") == -1 && !url.endsWith("/")) {
//            url = url + "/";
//        }
//        System.out.println(url);
    }

    public String getTitle() {
        return title;
    }

    public Vector<HyperLink> getLinks() {
        return links;
    }

    public Vector<Paragraph> getParagraph() {
        return paragraph;
    }
}
