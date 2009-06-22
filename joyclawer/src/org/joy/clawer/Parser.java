package org.joy.clawer;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Parser extends SAXParser {
	private ArrayList<String> links = new ArrayList<String>();
	private String context;
	private String regEx;

	public Parser() {

	}

	@Override
	public void startElement(QName tag, XMLAttributes attr, Augmentations arg)
			throws XNIException {
		if (tag.rawname.equals("A")) {
			String href = attr.getValue("href");
			URL u;
			try {
				u = new URL(new URL(context), href);
				String url = u.toString();
				String suffix = u.getFile();
				url = url.replaceAll("\\/\\.\\/", "\\/");
				url = url.replaceAll("\\/[^\\/]+\\/\\.\\.\\/", "\\/");
				// 避免主页重定向
				if (suffix.endsWith("index.htm")
						|| suffix.endsWith("index.html")
						|| suffix.endsWith("index.asp")
						|| suffix.endsWith("default.aspx")
						|| suffix.endsWith("index.php")
						|| suffix.endsWith("index.jsp")) {
					url = url.substring(0, url.lastIndexOf("/") + 1);
				} else if (u.getPath().equals("") && !url.endsWith("/")) {
					// 一律写成**/的形式
					url = url + "/";
				}
				if (regEx != null && Pattern.matches(regEx, url)) {
					links.add(url);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		super.startElement(tag, attr, arg);
	}

	public String[] extract(String context, String text, String regEx) {
		this.context = context;
		this.regEx = regEx;
		links.clear();
		try {
			parse(new InputSource(new StringReader(text)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return links.toArray(new String[0]);
	}

	public static void main(String[] args) throws SAXException, IOException {
		String str = "<A   href='index.htm'>kdsfj</a><A   href='index.htm'>kdsfj</a>"
				+ "<A   href='index.htm'>kdsfj</a>"
				+ "<A   href='indedx.htm'>k2dsfj</a>"
				+ "<A   href='indesx.htm'>kd2sfj</a>"
				+ "<A   href='indexa.htm'>kdsf444j</a>dfsd"
				+ "ewr"
				+ ""
				+ "few<A   href='inddex.htm'>kd4sfj</a>" + "s";
		Parser p = new Parser();
		// for (String s : p.extract("http://localhost", str)) {
		// System.out.println(s);
		// }
		for (String s : p.extract("http://www.baidu.com", str, ".*baidu.com.*")) {
			;
			System.out.println(s);
		}
	}
}
