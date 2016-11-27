package DataStreams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


class Param_hash {
	int a;
	int b;
	public Param_hash(int a,int b) {
		this.a = a;
		this.b = b;
	}
	@Override
	public String toString() {
		StringBuilder print = new StringBuilder();
		print.append(a);
		print.append(" - ");
		print.append(b);
		return print.toString();
	}
}

public class MyDataStreams {
	
	private final static int P = 123457;
	private final static int N_BUCKETS = 3*((int)Math.pow(10, 4));
	private List<Param_hash> param_hash = new ArrayList<>();
	private int Count[][];
	
	public List<Param_hash> getParam_hash() {
		return param_hash;
	}


	public void setParam_hash(List<Param_hash> param_hash) {
		this.param_hash = param_hash;
	}


	public int hash(int a,int b,int n_buckets,int x) {
		int y = x % P;
		int hash_val = (a*y + b) % P;
		return ( hash_val % n_buckets); 
	}
	
	public MyDataStreams() {
		loadHashParams("HW4-q4/hash_params.txt");
		Count = new int[param_hash.size()][N_BUCKETS];
	}
	
	
	public void loadHashParams(String filename) {
		
		List<Param_hash> param = new ArrayList<Param_hash>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			// read line with bufferedReader
			String line = reader.readLine();
			while(line != null) {
				String[] splitLine = line.split("\t");
				Param_hash param1 = new Param_hash(Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1]));
				param.add(param1);
				line = reader.readLine();
			}
			this.param_hash = param; 
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int countLineOfFile(String filename) {
		int count = 0;
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line = reader.readLine();
			while(line != null) {
				count++;
				line = reader.readLine();
			}
			reader.close();
			return count;
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public List<Integer> getExactCount(String filename) {
		List<Integer> count = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line = reader.readLine();
			while(line != null) {
				String line1[] = line.split("\t");
				count.add(Integer.parseInt(line1[1]));
				line = reader.readLine();
			}
			reader.close();
			
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return count;
	}

	public int appoximateCount(int word_id,int[][] c,int n_hashes,List<Param_hash> list) {
		
		int min_count = 0;
		for(int j = 0;j < list.size();j++) {
			
			int a = list.get(j).a;
			int b = list.get(j).b;
			
			int hash_bucket =  hash(a, b,N_BUCKETS,word_id);
			int count = c[j][hash_bucket];
			
			if(min_count !=0){
				
				min_count = Math.min(count, min_count);
			}else {
				min_count = count;
			}
			
		}	
		return min_count;
	}
	
	public void agorithmDataStreams(String dataset,int n_hashes
			,List<Param_hash> param_hashs,int n_lines,int reportFrequent) {
		System.out.println("DataStreams is Starting");
		// frequent reporting
		List<Integer> reportIterator = new ArrayList<Integer>(); 
		for(int i = 1;i <reportFrequent;i++){
			int e = (n_lines*i)/reportFrequent;
			reportIterator.add(e);
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset)));
			int line_number = 0;
			String line = reader.readLine();
			while(line != null) {
				int word = Integer.parseInt(line);
				for(int j = 0;j < n_hashes;j++) {
					Param_hash param = param_hash.get(j);
					int hashBucket = hash(param.a, param.b, N_BUCKETS, word);
					Count[j][hashBucket] += 1;
					if(reportIterator.indexOf(line_number) != -1) {
						int completion = 1 + reportIterator.indexOf(line_number);
						System.out.println("Completion " + completion + "/" + reportFrequent);
					}
				}
				line = reader.readLine();
				line_number++;
			}
		}catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void compareFirstWordsFromDataSet(String dataSet, String CountFile,int k,int reportFrequent,int n_hashes) {
		
		// File input
		System.out.println("Input dataSet is: " + dataSet + " and Input CountFile is: " + CountFile);
		// load data files
		int n_lines = countLineOfFile(dataSet);
		System.out.println("n_lines is: " + n_lines);
		List<Integer> exactWordCount = getExactCount(CountFile);
		for(int j = 0 ;j < n_hashes;j++) {
			for(int x = 0;x < N_BUCKETS;x++) {
				this.Count[j][x] = 0;
			}
		}
		
		this.agorithmDataStreams(dataSet,n_hashes,param_hash, n_lines, reportFrequent);
		List<Integer> approxWordCount = new ArrayList<>();
		// Compare approximate to compare the counts of all the words
		if(k == -1)
			k = exactWordCount.size();
		for(int word = 1;word <= k;word++) {
			int appro = appoximateCount(word, Count, n_hashes,param_hash);
			approxWordCount.add(appro);
		}
		
		// Print result
		if(k < 20) {
			System.out.println("----First " + k + " word, exact count----");
			printFrequencyOfFirst(k, exactWordCount, n_lines);
			System.out.println();
		}else {
			String filename = "approx_word_count.txt";
			saveFile(filename, approxWordCount);
		}	
	}
	
	public void saveFile(String filename,List<Integer> list) {
		System.out.println("Save " + filename);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for(int i = 0;i < list.size();i++) {
				StringBuilder str = new StringBuilder();
				str.append(i + 1);
				str.append("\t");
				str.append(list.get(i));
				str.append("\n");
				writer.write(str.toString());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void printFrequencyOfFirst(int k,List<Integer> wordCount,int totalWordCount) {
		for (int i = 1;i <= k;i++) {
			float freq = (float)wordCount.get(i)/totalWordCount;
			System.out.println("Word " + i + " has count: " + wordCount.get(i) + " (frequency )" + freq);
		}
	}
	
	public void MyMain(String dataSet,String Count,int k,int reportFreq) {
		compareFirstWordsFromDataSet(dataSet, Count, k, reportFreq, param_hash.size());
	}
	
	public static void main(String args[]) {
		MyDataStreams myDataStreams = new MyDataStreams();
		myDataStreams.MyMain("HW4-q4/words_stream.txt", "HW4-q4/counts.txt", -1, 100);
		System.out.println("End");
		// Ve graph bang matlab
	}
}
