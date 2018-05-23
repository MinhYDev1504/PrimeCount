package com.example.bigtable.sample;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCountHBase {
  public static void main(String [] args) throws Exception
  {
    Configuration c=new Configuration();
    String[] files=new GenericOptionsParser(c,args).getRemainingArgs();
    Path input=new Path(files[0]);
    Path output=new Path(files[1]);
    Job j=new Job(c,"wordcount");
    j.setJarByClass(WordCountHBase.class);
    j.setMapperClass(MapForWordCount.class);
    j.setReducerClass(ReduceForWordCount.class);
    j.setOutputKeyClass(Text.class);
    j.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(j, input);
    FileOutputFormat.setOutputPath(j, output);
    System.exit(j.waitForCompletion(true)?0:1);
  }
  //Class MAP
  public static class MapForWordCount extends Mapper<LongWritable, Text, Text, IntWritable>
  {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
    {
      // Chuyển dữ liệu trong File Text Input thành 1 chuỗi String
      String line = value.toString();
      // Tách các số ngăn cách nhau bởi dấu "," trong chuỗi trên bỏ vào 1 mảng String[]
      String[] numbers = line.split(",");
      
      for(String number: numbers )
      {
        // Parse mỗi phần tử sang kiểu số Integer
        int num = Integer.parseInt(number);
        // Khởi tạo outputKey
        Text outputKey = new Text(number.toUpperCase().trim());
        // Khởi tạo biến outputValue lưu giá trị của outputKey 
        IntWritable outputValue = new IntWritable(1);
        // Kiểm tra nếu đúng là số nguyên tố thì ghi vào cập giá trị <key,value>
        if(isPrime(num)){
          context.write(outputKey, outputValue);
        }        
      }
    }
    // Hàm isPrime kiểm tra số nguyên tố
    public static boolean isPrime(int n) {
      // Số nguyên tố không thể nhỏ hơn 2
      if (n < 2) {
          return false;
      }
      // Số nguyên tố không chia hết cho số nào lớn hơn 1 và nhỏ hơn căn bậc 2 của chính nó
      int squareRoot = (int) Math.sqrt(n);
      for (int i = 2; i <= squareRoot; i++) {
          if (n % i == 0) {
              return false;
          }
      }
      return true;// n là số nguyên tố
    }

  }
  //Class REDUCE
  public static class ReduceForWordCount extends Reducer<Text, IntWritable, Text, IntWritable>
  {
    public void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
    {
      int sum = 0;
        for(IntWritable value : values)
        {
        sum += value.get();
        }
        context.write(word, new IntWritable(sum));
    }
  }
}


