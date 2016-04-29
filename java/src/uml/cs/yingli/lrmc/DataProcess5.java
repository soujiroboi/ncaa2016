package uml.cs.yingli.lrmc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataProcess5 {
	
	public static double calP(double a, double x1, double x2) {
		return 1 - 1/(1+(1/Math.pow(Math.E, a*(x1 - x2))));
	}
	
	public static void main(String arg[]) throws Exception {
		//parameter result from Matlab Linear Regression
		double a = -1834.72;
		
		// create Scanner inFile1
	    Scanner inFile1 = new Scanner(new File("data/Pi.txt"));

	    // Original answer used LinkedList, but probably preferable to use ArrayList in most cases
	    // List<Float> temps = new LinkedList<Float>();
	    List<Double> temps = new ArrayList<Double>();

	    // while loop
	    while (inFile1.hasNext()) {
	      // find next line
	      Double token1 = Double.valueOf(inFile1.next());
	      temps.add(token1);
	    }
	    inFile1.close();

	    Double[] Pi = temps.toArray(new Double[0]);
	    
	    CSVWriter writer = new CSVWriter(new FileWriter("data/YingLi2012-15.csv", true), ',');
	    
	    CSVReader reader = new CSVReader(new FileReader("data/SampleSubmission.csv"));
	    String [] nextLine = reader.readNext();
	    nextLine = reader.readNext();
	    writer.writeNext(nextLine);
	    while ((nextLine = reader.readNext()) != null) {
	    	String[] strs = nextLine[0].split("_");
	    	// nextLine[0] is the id of teams
	    	int i = Integer.parseInt(strs[1]) - 1101;
	    	int j = Integer.parseInt(strs[2]) - 1101;
	    	String[] res = new String[3];
	    	res[0] = nextLine[0];
	    	res[1] = Double.toString(calP(a, Pi[i], Pi[j]));
	    	writer.writeNext(res);
	    }
	    writer.close();
	    
	}
}
