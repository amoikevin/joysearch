package org.joy.crawler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Crawler extends Configured implements Tool {
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Crawler(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		getConf().addResource("joyclawer-site.xml");
		getConf().set("org.joy.clawer.seeds", args[0]);
		
		String workdir = getConf().get("org.joy.clawer.dir");
		FileSystem fs = FileSystem.get(getConf());
		if (fs.exists(new Path(workdir))) {
			fs.delete(new Path(workdir), true);
		}
		fs.mkdirs(new Path(workdir));
		fs.copyFromLocalFile(new Path(getConf().get("org.joy.clawer.seeds")),
				new Path(getConf().get("org.joy.clawer.dir")+"in/init.txt"));
		
		for (int i = 0; i < 15; i++) {
			int res = ToolRunner.run(getConf(), new CrawlerDriver(), args);
			if (res != 0)
				return 1;
			res = ToolRunner.run(getConf(), new ParserDriver(), args);
			if (res != 0)
				return 1;
			res = ToolRunner.run(getConf(), new OptimizerDriver(), args);
			if (res != 0)
				return 1;
		}
		return 0;
	}
}
