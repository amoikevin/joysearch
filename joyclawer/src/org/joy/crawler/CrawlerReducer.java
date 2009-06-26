package org.joy.crawler;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CrawlerReducer extends Reducer<Text, LongWritable, Text, Text> {
	private Tourer t = new Tourer();

	public void reduce(Text text, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {
		int i = 0;
		String url = text.toString();
		System.out.println("downloading\t"+url);
		context.setStatus("downloading\t"+url);
		try {
			String content = t.download(url).replaceAll("\\n+", " ").replaceAll("\\s+|\\t+", " ");
			context.write(new Text(url), new Text(content));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			context.write(new Text(url), new Text("Visted, but not available!"));
		}
	}

}
