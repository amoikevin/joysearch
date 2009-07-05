package org.joy.crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CrawlerReducer extends Reducer<Text, LongWritable, Text, Text> {
	private final static int BLOCK_SIZE = 1024;

	public void run(
			final org.apache.hadoop.mapreduce.Reducer<Text, LongWritable, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// put all the download operation into a threadpool
		BlockingQueue<Runnable> queue = new SynchronousQueue<Runnable>();

		int numWorker = context.getConfiguration().getInt(
				"org.joy.crawler.worker", 5);

		final Semaphore s = new Semaphore(numWorker);
		final Semaphore sBlock = new Semaphore(BLOCK_SIZE);

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(BLOCK_SIZE,
				BLOCK_SIZE * 2, Long.MAX_VALUE, TimeUnit.SECONDS, queue);
		while (context.nextKey()) {
			sBlock.acquire();
			final String url = context.getCurrentKey().toString();
			threadPool.execute(new Runnable() {

				@Override
				public void run() {
					try {
						s.acquire();
						Downloader t = new Downloader();
						System.err.println("正在下载" + url);
						String content = t.download(url)
								.replaceAll("\\n+", " ").replaceAll(
										"\\s+|\\t+", " ");
						synchronized (context) {
							context.write(new Text(url), new Text(content));
						}
						System.err.println("下载成功" + url
								+ content.getBytes().length / 1000 + "k");
					} catch (Exception e) {
						synchronized (context) {
							System.err.println("下载错误" + url);
							e.printStackTrace();
							try {
								context.write(new Text(url), new Text(
										"Visted, but not available!"));
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					s.release();
					sBlock.release();
				}

			});
		}
		threadPool.shutdown();
		threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	};
}
