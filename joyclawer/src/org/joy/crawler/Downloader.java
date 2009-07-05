/*
 * Downloader.java
 * Created on 2006年2月11日, 上午12:06
 */
package org.joy.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 页面下载器，负责下载给定的页面
 */
public class Downloader {

	private StringBuilder pageBuffer = new StringBuilder();
	private int timeOut = 1000*30;//三十秒的超时时间
	private int maxSize = 1024 * 1024;
	private boolean cancelled;
	private boolean connecting;
	private URLConnection conn = null;
	private Thread downloadThread;
	private String charset;

	public Downloader() {
		downloadThread = Thread.currentThread();
	}

	public static void main(String[] args) throws DownloadException {
		Downloader t = new Downloader();
		System.out
				.println(t
						.download("http://www.jlonline.com/fangchan/chushou/index_2.html"));
	}

	/**
	 * @param strURL
	 *            要获取连接的url
	 * @return 如果成功，返回已经设置好的connection
	 * @throws DownloadFailedException
	 *             如果连接过程失败，则抛出异常
	 */
	private void setConnectionHeader(URLConnection conn) {
		// 设置timeOut
		 conn.setConnectTimeout(getTimeOut());
		 conn.setReadTimeout(getTimeOut());
		conn.setRequestProperty("accept", "image/png,*/*;q=0.5");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent", "Baiduspider");
		conn.setRequestProperty("ua-cpu", "x86");
		conn.setRequestProperty("accept-charset", "gb2312,utf-8");
	}

	protected void openConnection(String URL) throws IOException {
		URL u = null;
		connecting = true;

		// 与资源建立连接
		u = new URL(URL);
		conn = (URLConnection) u.openConnection();
		setConnectionHeader(conn);

		// //判断是否是重新定西向过的
		// if(conn.getResponseCode() > 300 && conn.getResponseCode() < 400){
		// u = new URL(u,conn.getHeaderField("Location"));
		// conn = (HttpURLConnection) u.openConnection();
		// setConnectionHeader(conn);
		// }
		// 是否是合适的类型？
		String contentType = conn.getContentType();
		if (contentType != null) {
			contentType = contentType.toLowerCase();
			if (!contentType.startsWith("text/html")) {
				throw new ConnectException("内容不对");
			}
		}

		if (contentType != null && contentType.endsWith("utf-8")) {
			charset = "utf-8";
		} else {
			charset = "gb2312";
		}

		connecting = false;
	}

	/**
	 * 下载给定的url的页面
	 * 
	 * @param strURL
	 *            给定的url
	 * @return 所下载页面的所有字符串
	 * @throws 如果下载过程中失败
	 *             ，抛出下载失败异常
	 */
	public String download(String URL) throws DownloadException {
		cancelled = false;
		BufferedReader reader = null;
		try {
			// 打开连接
			openConnection(URL);
			// 下载流，读取到字符串
			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream(), charset));
			char[] buffer = new char[2048];
			pageBuffer.delete(0, pageBuffer.length());

			int length = 0;
			length = reader.read(buffer);
			while (length != -1) {
				if (cancelled) {
					throw new DownloadException("User Cancelled " + URL);
				}
				if (pageBuffer.length() > maxSize) {
					throw new DownloadException("内容超常 " + URL);
				}
				pageBuffer.append(buffer, 0, length);
				length = reader.read(buffer);
			}
			return pageBuffer.toString();
		} catch (Exception e) {
			// 如果失败，显示信息，抛出异常
			throw new DownloadException(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				// if(conn!=null)
				// conn.disconnect();
			} catch (IOException ex) {
				System.out.println("流关闭错误");
			}
		}
	}

	public void close() {
		cancelled = true;
		if (connecting) {
			// if(conn!=null)
			// conn.disconnect();
		}
		try {
			downloadThread.join();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public Thread getDownloadThread() {
		return downloadThread;
	}
}
