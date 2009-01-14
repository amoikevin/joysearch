/*
 * Spliter.java
 *
 * Created on 2006年11月25日, 下午2:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package spliter;

import java.util.Vector;

/**
 *
 * @author Andy
 */
class SpliterOperator {

    static {
        System.loadLibrary("DLL");
    }

    public native static void init();

    public native static void setMode(int mode);

    public native static void split(String Paragraph);

    public native static String getResult();

    public native static String getFinger();

    public native static String getKeywords();

    public native static void endSplit();

    public native static void cleanUp();
}

public class Spliter {

    public static final int SPLIT = 0;
    public static final int KEYWORDS = 1;
    public static final int FINGER = 2;
    public static final int COMBINED = 3;
    private String currentText;

    public class Keyword implements Comparable<Keyword> {

        public Keyword(String word, float weight) {
            this.weight = weight;
            this.word = word;
        }
        private float weight;
        private String word;

        @Override
        public boolean equals(Object o) {
            return getWord().equals(o);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 19 * hash + (this.word != null ? this.word.hashCode() : 0);
            return hash;
        }

        public float getWeight() {
            return weight;
        }

        public String getWord() {
            return word;
        }

        public int compareTo(Keyword k) {
            return Float.compare(this.weight, k.getWeight());
        }
    }

    public Spliter() {
        SpliterOperator.init();
    }

    public void setMode(int mode) {
        if (currentText != null) {
            SpliterOperator.endSplit();
        }
        SpliterOperator.setMode(mode);
        currentText = null;
    }

    private void checkAndSplit(String text) {
        text = filter(text);
        if (!text.equals(currentText)) {
            if (currentText != null) {
                SpliterOperator.endSplit();
            }
            SpliterOperator.split(text);
            currentText = text;
        }
    }

    public Keyword[] getKeywords(String text) {
        checkAndSplit(text);
        String res = SpliterOperator.getKeywords();
        String[] words = res.split("\n");
        Vector<Keyword> keywords = new Vector<Spliter.Keyword>();
        for (String line : words) {
            if (line.trim().split(" ").length < 2) {
                continue;
            }
            String word = line.trim().split(" ")[0];
            float weight = Float.parseFloat(line.trim().split(" ")[1]);
            keywords.add(new Keyword(word, weight));
        }
        return keywords.toArray(new Keyword[0]);
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

    public String[] split2(String text) {
        String res = filter(text);
        if (res.trim().equals("")) {
            return new String[0];
        }
        SpliterOperator.split(res);
        res = SpliterOperator.getResult().trim().replaceAll("\\s+", " ");
        SpliterOperator.endSplit();
        //全部转化为小写
        Vector<String> vec = new Vector<String>();
        for (String word : res.split(" ")) {
            vec.add(word.toLowerCase());
        }
        return vec.toArray(new String[0]);
    }

    public String[] split(String text) {
        checkAndSplit(text);
        String res = SpliterOperator.getResult().trim().replaceAll("\\s+", " ");
        return res.split(" ");
    }

    public String getFinger(String text) {
        checkAndSplit(text);
        return SpliterOperator.getFinger();
    }

    public void cleanup() {
        if (currentText != null) {
            SpliterOperator.endSplit();
        }
        SpliterOperator.cleanUp();
    }
//
//    public void setTagStyle(int style) {
//        SpliterOperator.setTagStyle(style);
//    }
//
//   public void setOutputFormat(int format) {
//        SpliterOperator.setOutputFormat(format);
//    }
//
//    public int getTagStyle(int style) {
//        return SpliterOperator.getTagStyle();
//    }
//
//    public int getOutputFormat() {
//        return SpliterOperator.getOutputFormat();
//    }
//
//    public String paragraphProcessing(String paragraph) {
//        return SpliterOperator.paragraphProcessing(paragraph);
//    }
//
//    public void fileProcessing(String src, String dest) throws FileNotFoundExc {
//        File file = new File(src);
//        if (file.exists()) {
//            SpliterOperator.fileProcessing(src, dest);
//        } else {
//            throw new FileNotFoundExc();
//        }
//    }
//
//    public String[] getKeywords(String text) {
//
//        SpliterOperator.setTagStyle(1);
//        SpliterOperator.setOutputFormat(0);
//        text = text.replaceAll("[^\u4e00-\u9fa5|a-z|A-Z|0-9\\s]", " ");
//        text = text.replaceAll("\\s+", " ");
//        if (text.equals(" ") || text.equals("")) {
//            return null;
//        }
//        text = SpliterOperator.paragraphProcessing(text);
//        text = " " + text.replaceAll("\\s+", "  ");
//        text = text.toLowerCase();
//        Pattern p = Pattern.compile("\\s.+?\\s",
//                Pattern.CASE_INSENSITIVE);
//        // 获取链接
//        Matcher m = p.matcher(text);
//        // 链接列表
//        ArrayList<String> keywords = new ArrayList<String>();
//
//        while (m.find()) {
//            // 验证链接
//            String keyword = m.group().trim();
//            if (keyword.equals("")) {
//                continue;
//            }
//
//            boolean isForbiddenTag = false;
//            for (String tag : forbiddenTags) {
//                if (keyword.endsWith(tag)) {
//                    isForbiddenTag = true;
//                    break;
//                }
//            }
//            if (isForbiddenTag) {
//                continue;
//            }
//            if (keyword.length() < 2) {
//                continue;
//            }
//            keyword = keyword.substring(0, keyword.length() - 2);
//
//            boolean isFreqWord = false;
//            for (String word : freqWords) {
//                if (keyword.equals(word)) {
//                    isFreqWord = true;
//                    break;
//                }
//            }
//            if (isFreqWord) {
//                continue;
//            }
//            keywords.add(keyword);
//        }
//        return keywords.toArray(new String[0]);
//    }
//
//    public void cleanUp() {
//        SpliterOperator.cleanUp();
//    }
//
    public static void main(String[] args) {
//        SpliterOperator.init();
//        SpliterOperator.split("大家好");
//        System.out.println(SpliterOperator.getResult());
//        System.out.println(SpliterOperator.getFinger());
//        System.out.println(SpliterOperator.getKeywords());
//        SpliterOperator.endSplit();
//        SpliterOperator.cleanUp();
        Spliter s = new Spliter();
        String[] ss = s.split2("尽管如此，要实现全世界对北京的期盼，北京还有许许多多工作要做。但我们有信心在2008年到来之前把北京建设成为一个全新的城市。");
        s.cleanup();
        System.out.println(System.getProperty("user.dir"));

    //Registry
    }
}





