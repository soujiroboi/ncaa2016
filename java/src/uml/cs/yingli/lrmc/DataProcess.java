package uml.cs.yingli.lrmc;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import com.opencsv.CSVReader;

public class DataProcess {
	
	public static void main(String arg[]) throws Exception {
		//calculate matrix: HW[i][j], the times team i win j at i's home
		int[][] A = new int[364][364]; //364 is the sum number of the teams
		//calculate matrix: HT[i][j], the times team i play with j at i's home
		int[][] B = new int[364][364];
		//for loop, go through all the Compact Results
		//Regular Season
		CSVReader reader = new CSVReader(new FileReader("data/RegularSeasonCompactResults.csv"));
	    String [] nextLine = reader.readNext();
	    while ((nextLine = reader.readNext()) != null) {
	    	// nextLine[] is an array of values from the line
	    	int i = 0; //Home team
	    	int j = 0;
	    	//use 2013-2015 three years data to predict 2016
	    	if(Integer.parseInt(nextLine[0])>2012 && Integer.parseInt(nextLine[0])<2016) {
	    		if(nextLine[6].equals("H")) {
	    			i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    			j = Integer.parseInt(nextLine[4]) - 1101;
	    			A[i][j] += 1;
	    			B[i][j] += 1;
	    		}else if(nextLine[6].equals("A")) {
	    			j = Integer.parseInt(nextLine[2]) - 1101;
	    			i = Integer.parseInt(nextLine[4]) - 1101;
	    			B[i][j] += 1;
	    		}
	    	}
	    }
	    //tourney
	    reader = new CSVReader(new FileReader("data/TourneyCompactResults.csv"));
	    nextLine = reader.readNext();
	    while ((nextLine = reader.readNext()) != null) {
	    	// nextLine[] is an array of values from the line
	    	int i = 0; //Home team
	    	int j = 0;
	    	//use 2013-2015 three years data to predict 2016
	    	if(Integer.parseInt(nextLine[0])>2012 && Integer.parseInt(nextLine[0])<2016) {
	    		if(nextLine[6].equals("H")) {
	    			i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    			j = Integer.parseInt(nextLine[4]) - 1101;
	    			A[i][j] += 1;
	    			B[i][j] += 1;
	    		}else if(nextLine[6].equals("A")) {
	    			j = Integer.parseInt(nextLine[2]) - 1101;
	    			i = Integer.parseInt(nextLine[4]) - 1101;
	    			B[i][j] += 1;
	    		}
	    	}
	    }
	    
	    //write results A and B to files
	    /*
	    BufferedWriter outputWriter = null;
	    outputWriter = new BufferedWriter(new FileWriter("data/A2012-2015.txt"));
	    
	    for(int m=0; m<364; m++) {
	    	for(int n=0; n<364; n++) {
	    		outputWriter.write(A[m][n] + " ");
	    	}
    		outputWriter.newLine();
	    }
	    outputWriter.flush();  
	    outputWriter.close();
	    
	    outputWriter = new BufferedWriter(new FileWriter("data/B2012-2015.txt"));
	    
	    for(int m=0; m<364; m++) {
	    	for(int n=0; n<364; n++) {
	    		outputWriter.write(B[m][n] + " ");
	    	}
    		outputWriter.newLine();
	    }
	    outputWriter.flush();  
	    outputWriter.close();
	    */
	    
	    
	    //calculate Home win margin with different road win times
	    Hashtable<Integer, Integer> C = new Hashtable<Integer, Integer>();//margin---road win times
	    Hashtable<Integer, Integer> D = new Hashtable<Integer, Integer>();//margin---road play times
	    Hashtable<Integer, Double> P = new Hashtable<Integer, Double>();//margin---road win probability
	    
	    reader = new CSVReader(new FileReader("data/RegularSeasonCompactResults.csv"));
	    nextLine = reader.readNext();
	    while ((nextLine = reader.readNext()) != null) {
	    	// nextLine[] is an array of values from the line
	    	int i = 0; //Home team
	    	int j = 0;
	    	//use 2013-2015 three years data to predict 2016
	    	if(Integer.parseInt(nextLine[0])>2012 && Integer.parseInt(nextLine[0])<2016) {
	    		if(nextLine[6].equals("H")) {
	    			i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    			j = Integer.parseInt(nextLine[4]) - 1101;
	    			int diff = Integer.parseInt(nextLine[3]) - Integer.parseInt(nextLine[5]);
	    			if(!C.containsKey(diff)) {
	    				C.put(diff, B[j][i] - A[j][i]);
	    				D.put(diff, B[j][i]);
	    			} else {
	    				int tmp = C.get(diff);
	    				C.remove(diff);
	    				C.put(diff, tmp + B[j][i] - A[j][i]);
	    				tmp = D.get(diff);
	    				D.remove(diff);
	    				D.put(diff, tmp + B[j][i]);
	    			}
	    		}else if(nextLine[6].equals("A")) {
	    			j = Integer.parseInt(nextLine[2]) - 1101;
	    			i = Integer.parseInt(nextLine[4]) - 1101;
	    			int diff = Integer.parseInt(nextLine[5]) - Integer.parseInt(nextLine[3]);
	    			if(!C.containsKey(diff)) {
	    				C.put(diff, B[j][i] - A[j][i]);
	    				D.put(diff, B[j][i]);
	    			} else {
	    				int tmp = C.get(diff);
	    				C.remove(diff);
	    				C.put(diff, tmp + B[j][i] - A[j][i]);
	    				tmp = D.get(diff);
	    				D.remove(diff);
	    				D.put(diff, tmp + B[j][i]);
	    			}
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
	    		if(nextLine[6].equals("H")) {
	    			i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    			j = Integer.parseInt(nextLine[4]) - 1101;
	    			int diff = Integer.parseInt(nextLine[3]) - Integer.parseInt(nextLine[5]);
	    			if(!C.containsKey(diff)) {
	    				C.put(diff, B[j][i] - A[j][i]);
	    				D.put(diff, B[j][i]);
	    			} else {
	    				int tmp = C.get(diff);
	    				C.remove(diff);
	    				C.put(diff, tmp + B[j][i] - A[j][i]);
	    				tmp = D.get(diff);
	    				D.remove(diff);
	    				D.put(diff, tmp + B[j][i]);
	    			}
	    		}else if(nextLine[6].equals("A")) {
	    			j = Integer.parseInt(nextLine[2]) - 1101;
	    			i = Integer.parseInt(nextLine[4]) - 1101;
	    			int diff = Integer.parseInt(nextLine[5]) - Integer.parseInt(nextLine[3]);
	    			if(!C.containsKey(diff)) {
	    				C.put(diff, B[j][i] - A[j][i]);
	    				D.put(diff, B[j][i]);
	    			} else {
	    				int tmp = C.get(diff);
	    				C.remove(diff);
	    				C.put(diff, tmp + B[j][i] - A[j][i]);
	    				tmp = D.get(diff);
	    				D.remove(diff);
	    				D.put(diff, tmp + B[j][i]);
	    			}
	    		}
	    	}
	    }
	    
	    //calculate P
	    for(int diff : C.keySet()) {
	    	P.put(diff, Double.valueOf(C.get(diff))/D.get(diff));
	    }
	    
	    List<Integer> keys = new ArrayList<Integer>(P.keySet());
	    Collections.sort(keys);
	    
	    BufferedWriter outputWriter = null;
	    outputWriter = new BufferedWriter(new FileWriter("data/P2012-2015-1.txt"));
	    
	    for(int diff : keys) {
	    	outputWriter.write(diff + " " + P.get(diff));
    		outputWriter.newLine();
	    }
	    outputWriter.flush();  
	    outputWriter.close();
	    
	}
}
