package org.joy.clawer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ParserMapper extends Mapper<LongWritable, Text, Text, Text> {
	private static Parser parser = new Parser();

	protected void map(LongWritable key, Text value, Context context)
			throws java.io.IOException, InterruptedException {
		// analyze the doc
		try {
			String line = value.toString().replaceAll("\\t+", "\t");
			String url = line.split("\\t")[0];
			String text = line.split("\\t")[1];
			context.setStatus(url);
			System.out.println("分析\t" + url);

			context.write(new Text(url), new Text("NULL"));

			for (String link : parser.extract(url, text, context
					.getConfiguration().get("org.joy.clawer.regEx"))) {
				if (link.contains("#")) {
					continue;
				}
				context.write(new Text(url), new Text(link));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
}
