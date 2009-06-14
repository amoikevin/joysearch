package org.joy.clawer;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OptMapper extends
		Mapper<LongWritable, Text, Text, BooleanWritable> {

	protected void map(
			LongWritable lineNO,
			Text line,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, BooleanWritable>.Context context)
			throws java.io.IOException, InterruptedException {
		String[] pair = line.toString().split("\t");
		String key = pair[0];
		String value = pair[1];
		context.write(new Text(key), new BooleanWritable(true));
		if (!value.equals("NULL")) {
			context.write(new Text(value), new BooleanWritable(false));
		}
	};
}
