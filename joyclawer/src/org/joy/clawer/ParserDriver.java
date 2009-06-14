package org.joy.clawer;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ParserDriver extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new ParserDriver(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		Configuration conf = getConf();
		Job job = new Job(conf, "分析");
		job.setJarByClass(Clawer.class);
		job.setMapperClass(ParserMapper.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(ArrayWritable.class);

		String workdir = conf.get("org.joy.clawer.dir");
		FileSystem fs = FileSystem.get(conf);
		//auto find the latest doc folder
		long latest = 0; Path inPath = null;
		for(FileStatus stat: fs.listStatus(new Path(workdir+"doc"))){
			if(stat.getModificationTime() > latest){
				inPath = stat.getPath();
			}
		}
		FileInputFormat.addInputPath(job, inPath);
		FileOutputFormat.setOutputPath(job, new Path(workdir + "out/"
				+ System.currentTimeMillis()));
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
