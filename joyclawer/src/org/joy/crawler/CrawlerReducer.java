package org.joy.crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

public class CrawlerReducer extends Reducer<Text, LongWritable, Text, Text> {

	public void run(
			final org.apache.hadoop.mapreduce.Reducer<Text, LongWritable, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// put all the download operation into a threadpool
		BlockingQueue<Runnable> queue = new SynchronousQueue<Runnable>();
		int numMaxWorker = context.getConfiguration().getInt(
				"org.joy.crawler.woker.max", 1024 * 1024);
		int numWorker = context.getConfiguration().getInt(
				"org.joy.crawler.worker", 5);

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(numWorker,
				numMaxWorker, 1024 * 1024, TimeUnit.SECONDS, queue);

		while (context.nextKey()) {
			final String url = context.getCurrentKey().toString();
			threadPool.submit(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Downloader t = new Downloader();
					Logger.getRootLogger().info("download"+url);
					try {
						String content = t.download(url)
								.replaceAll("\\n+", " ").replaceAll(
										"\\s+|\\t+", " ");
						synchronized (context) {
							context.write(new Text(url), new Text(content));
						}
						System.out.print(".");
					} catch (Exception e) {
						synchronized (context) {
							System.err.print("*");
							try {
								context.write(new Text(url), new Text(
										"Visted, but not available!"));
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}

			});
		}
		threadPool.shutdown();
		threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	};
}
