package org.joy.crawler;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import sun.misc.Sort;

public class CrawlerDriver extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new CrawlerDriver(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = getConf();
		Job job = new Job(conf, "下载");
		job.setJarByClass(Crawler.class);
		job.setMapperClass(InverseMapper.class);
		job.setReducerClass(CrawlerReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(15);

		String workdir = conf.get("org.joy.clawer.dir");
		FileSystem fs = FileSystem.get(conf);

		FileInputFormat.addInputPath(job, new Path(workdir + "in"));
		FileOutputFormat.setOutputPath(job, new Path(workdir + "doc/"+System.currentTimeMillis()));
		return job.waitForCompletion(true) ? 0 : 1;
	}

}
