package org.joy.clawer;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OptReducer extends Reducer<Text, BooleanWritable, Text, Text> {
	protected void reduce(
			Text key,
			java.lang.Iterable<BooleanWritable> values,
			org.apache.hadoop.mapreduce.Reducer<Text, BooleanWritable, Text, Text>.Context context)
			throws java.io.IOException, InterruptedException {
		boolean visited = false;
		for (BooleanWritable b : values) {
			if (b.get()) {
				visited =true;
			}
		}
		if (!visited) {
			context.write(new Text(key), new Text(""));
		}else{
			System.out.println(key);
		}
	};
}
