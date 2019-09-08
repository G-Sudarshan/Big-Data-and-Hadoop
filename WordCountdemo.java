import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class WordCountdemo {
	
	public static class MyMap extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>
	{
		private Text mykey = new Text();
		public void map(LongWritable key, Text value , OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException
		{
			String Line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(Line);
			while(tokenizer.hasMoreTokens())
			{
				mykey.set(tokenizer.nextToken());
				output.collect(mykey, new IntWritable(1));
			}		
		}		
	}
	
	public static class MyReduce extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>
	{
		public void reduce(Text key , Iterator<IntWritable> values , OutputCollector<Text , IntWritable> output, Reporter reporter) throws IOException
		{
			int sum=0;
			while(values.hasNext())
			{
				sum += values.next().get();
			}
			output.collect(key, new IntWritable(sum));
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		JobConf conf = new JobConf(WordCountdemo.class);
		conf.setJobName("MyFirstProgram");
		
		conf.setMapperClass(MyMap.class);
		//conf.setCombiner(Combiner.class);
		conf.setReducerClass(MyReduce.class);
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf,new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		
		JobClient.runJob(conf);
	}

}
