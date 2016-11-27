package DataStreams;
import java.awt.Color; 
import java.awt.BasicStroke; 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.xy.XYSeriesCollection; 
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class MyGraphDataStreams extends ApplicationFrame {
	
	private List<Float> errors = new ArrayList<Float>();
	private List<Float> exacts = new ArrayList<Float>();
	
	
	
	public List<Float> getErrors() {
		return errors;
	}

	public void setErrors(List<Float> errors) {
		this.errors = errors;
	}

	public List<Float> getExacts() {
		return exacts;
	}

	public void setExacts(List<Float> exacts) {
		this.exacts = exacts;
	}

	public MyGraphDataStreams(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}
	
	public List<Integer> loadCountFile(String filename) {	
		List<Integer> countDataStreams = new ArrayList<Integer>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line = reader.readLine();
			while(line != null) {
				String[] subline = line.split("\t");
				countDataStreams.add(Integer.parseInt(subline[1]));
				line = reader.readLine();
			}
			reader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return countDataStreams;
		
	}
	
	public void PlotGraph() {
		
		
		
	}
	
	public void ComputeError() {
		List<Integer> exactWordCount = loadCountFile("HW4-q4/counts.txt");
		List<Integer> approxWordCount = loadCountFile("approx_word_count.txt");
		int totalWords = exactWordCount.size();
		
		for(int i = 0;i < totalWords;i++) {
			float fre = (float) (exactWordCount.get(i)*1./totalWords);
			fre = (float)Math.log10(fre);
			exacts.add(fre);
		}
		SaveFileExacts();
		for(int i = 0;i < totalWords;i++) {
			float error = (float)(Math.abs(exactWordCount.get(i) - approxWordCount.get(i))*1./exactWordCount.get(i));
			error = (float) Math.log10(error);
			errors.add(error);
		}
		SaveFileErrors();
	}
	public void SaveFileErrors() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("Errors.txt"));
			for(int i = 0;i < errors.size();i++) {
				StringBuilder str = new StringBuilder();
				str.append(errors.get(i));
				str.append("\n");
				writer.write(str.toString());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void SaveFileExacts() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("exact_Errors.txt"));
			for(int i = 0;i < exacts.size();i++) {
				StringBuilder str = new StringBuilder();
				str.append(exacts.get(i));
				str.append("\n");
				writer.write(str.toString());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		MyGraphDataStreams myGraphDataStreams = new MyGraphDataStreams("Test");
		myGraphDataStreams.ComputeError();
		
	}
}
