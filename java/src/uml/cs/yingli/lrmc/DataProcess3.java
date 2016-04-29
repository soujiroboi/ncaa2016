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

public class DataProcess3 {
	
	public static void main(String arg[]) throws Exception {
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

	    /*
	    for (Double s : Pi) {
	      System.out.println(s);
	    }
	    */
	    
	    
	    
	    //calculate hashtable: WT, win times with Pi difference
	  	Hashtable<Double, Integer> WT = new Hashtable<Double, Integer>();
	  	//calculate hashtable: PT, play times with Pi difference
	  	Hashtable<Double, Integer> PT = new Hashtable<Double, Integer>();
	  	//calculate hashtable: WPT, win probability with Pi difference
	  	Hashtable<Double, Double> WPT = new Hashtable<Double, Double>();
	    
	    CSVReader reader = new CSVReader(new FileReader("data/RegularSeasonCompactResults.csv"));
	    String [] nextLine = reader.readNext();
	    while ((nextLine = reader.readNext()) != null) {
	    	// nextLine[] is an array of values from the line
	    	int i = 0; //Home team
	    	int j = 0;
	    	//use 2013-2015 three years data to predict 2016
	    	if(Integer.parseInt(nextLine[0])>2012 && Integer.parseInt(nextLine[0])<2016) {
	    		i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    		j = Integer.parseInt(nextLine[4]) - 1101;
	    		double key1 = Pi[i] - Pi[j];
	    		double key2 = Pi[j] - Pi[i];
	    		if(WT.containsKey(key1)) {
	    			int tmp = WT.get(key1);
	    			WT.remove(key1);
	    			WT.put(key1, tmp+1);
	    		} else {
	    			WT.put(key1, 1);
	    		}
	    		if(PT.containsKey(key2)) {
	    			int tmp = PT.get(key2);
	    			PT.remove(key2);
	    			PT.put(key2, tmp+1);
	    		} else {
	    			PT.put(key2, 1);
	    		}
	    	}
	    }
	    
	    reader = new CSVReader(new FileReader("data/TourneyCompactResults.csv"));
	    nextLine = reader.readNext();
	    while ((nextLine = reader.readNext()) != null) {
	    	// nextLine[] is an array of values from the line
	    	int i = 0; //Home team
	    	int j = 0;
	    	//use 2013-2015 three years data to predict 2016
	    	if(Integer.parseInt(nextLine[0])>2012 && Integer.parseInt(nextLine[0])<2016) {
	    		int diff = Integer.parseInt(nextLine[3]) - Integer.parseInt(nextLine[5]);
	    		i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    		j = Integer.parseInt(nextLine[4]) - 1101;
	    		double key1 = Pi[i] - Pi[j];
	    		double key2 = Pi[j] - Pi[i];
	    		if(WT.containsKey(key1)) {
	    			int tmp = WT.get(key1);
	    			WT.remove(key1);
	    			WT.put(key1, tmp+1);
	    		} else {
	    			WT.put(key1, 1);
	    		}
	    		if(PT.containsKey(key2)) {
	    			int tmp = PT.get(key2);
	    			PT.remove(key2);
	    			PT.put(key2, tmp+1);
	    		} else {
	    			PT.put(key2, 1);
	    		}
	    	}
	    }
	    
	    for(double key : WT.keySet()) {
	    	if(PT.containsKey(key)) {
	    		double tmp = WT.get(key)/(WT.get(key) + PT.get(key));
	    		WPT.put(key, tmp);
	    	} else {
	    		WPT.put(key, (double) 1);
	    	}
	    }
	    for(double key : PT.keySet()) {
	    	if(!WT.containsKey(key)) {
	    		WPT.put(key, (double) 0);
	    	}
	    }
	    
	    
	    BufferedWriter outputWriter = null;
	    outputWriter = new BufferedWriter(new FileWriter("data/PiWinPro.txt"));
	    
	    List<Double> keys = new ArrayList<Double>(WPT.keySet());
	    Collections.sort(keys);
	    
	    for(double key : keys) {
	    	outputWriter.write(key + " " + WPT.get(key));
    		outputWriter.newLine();
	    }
	    outputWriter.flush();  
	    outputWriter.close();
	    
	}
}
