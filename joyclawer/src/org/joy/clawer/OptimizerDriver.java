package org.joy.clawer;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class OptimizerDriver extends Configured implements Tool {
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new OptimizerDriver(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		Configuration conf = getConf();
		Job job = new Job(conf, "优化");
		job.setJarByClass(Clawer.class);
		job.setMapperClass(OptimizerMapper.class);
		job.setReducerClass(OptimizerReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BooleanWritable.class);
		
		String workdir = conf.get("org.joy.clawer.dir");
		FileSystem fs = FileSystem.get(conf);
		Path outPath = new Path(workdir + "in");
		if (fs.exists(outPath)) {
			fs.delete(outPath, true);
		}

		Path inPath = new Path(workdir + "out");

		for (FileStatus s : fs.listStatus(inPath)) {
			Path sub = s.getPath();
			FileInputFormat.addInputPath(job, sub);
		}

		FileOutputFormat.setOutputPath(job, new Path(workdir + "in"));
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
