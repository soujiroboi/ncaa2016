package uml.cs.yingli.lrmc;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import com.opencsv.CSVReader;

public class DataProcess2 {
	
	public static double calSH(double a, double b, double x) {
		return 1/(1+(1/Math.pow(Math.E, a*x + b)));
	}
	
	public static void main(String arg[]) throws Exception {
		//parameter result from Matlab Linear Regression
		double a = 0.0419;
		double b = -0.6060;
		
		//home court advantage, try average number
		double h = 5.5;
		
		//calculate matrix: HW[i][j], the times team i win j at i's home
		int[][] A = new int[364][364]; //364 is the sum number of the teams
		//calculate matrix: HT[i][j], the times team i play with j at i's home
		int[][] B = new int[364][364];
		//calculate Array: N[i], the total game times team i played
		int[] N = new int[364];
		//calculate Array: RR[i][j], team i road game with team j better probability (accumulate)
		double[][] RR1 = new double[364][364];//for T[i][j], j != i
		//calculate Array: RH[i][j], team i home game with team j better probability (accumulate)
		double[][] RH1 = new double[364][364];//for T[i][j], j != i
		//calculate Array: RR[i][j], team i road game with team j better probability (accumulate)
		double[][] RR2 = new double[364][364];//for T[i][j], j == i
		//calculate Array: RH[i][j], team i home game with team j better probability (accumulate)
		double[][] RH2 = new double[364][364];//for T[i][j], j == i
		//calculate Array: T[i][j], the probabilities team i transfer to team j, using RR and RH
		double[][] T = new double[364][364];
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
	    		int diff = Integer.parseInt(nextLine[3]) - Integer.parseInt(nextLine[5]);
	    		if(nextLine[6].equals("H")) {
	    			i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    			j = Integer.parseInt(nextLine[4]) - 1101;
	    			A[i][j] += 1;
	    			B[i][j] += 1;
	    			double rhxg = calSH(a, b, diff+h);
	    			RH1[i][j] += 1 - rhxg;
	    			RR1[j][i] += 1 - (1 - rhxg);
	    			RH2[i][j] += rhxg;
	    			RR2[j][i] += 1 - rhxg;
	    		}else if(nextLine[6].equals("A")) {
	    			j = Integer.parseInt(nextLine[2]) - 1101;
	    			i = Integer.parseInt(nextLine[4]) - 1101;
	    			B[i][j] += 1;
	    			double rhxg = calSH(a, b, h-diff);
	    			RH1[i][j] += 1 - rhxg;
	    			RR1[j][i] += 1 - (1 - rhxg);
	    			RH2[i][j] += rhxg;
	    			RR2[j][i] += 1 - rhxg;
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
	    		int diff = Integer.parseInt(nextLine[3]) - Integer.parseInt(nextLine[5]);
	    		if(nextLine[6].equals("H")) {
	    			i = Integer.parseInt(nextLine[2]) - 1101;//team id start from 1101
	    			j = Integer.parseInt(nextLine[4]) - 1101;
	    			A[i][j] += 1;
	    			B[i][j] += 1;
	    			double rhxg = calSH(a, b, diff+h);
	    			RH1[i][j] += 1 - rhxg;
	    			RR1[j][i] += 1 - (1 - rhxg);
	    			RH2[i][j] += rhxg;
	    			RR2[j][i] += 1 - rhxg;
	    		}else if(nextLine[6].equals("A")) {
	    			j = Integer.parseInt(nextLine[2]) - 1101;
	    			i = Integer.parseInt(nextLine[4]) - 1101;
	    			B[i][j] += 1;
	    			double rhxg = calSH(a, b, h-diff);
	    			RH1[i][j] += 1 - rhxg;
	    			RR1[j][i] += 1 - (1 - rhxg);
	    			RH2[i][j] += rhxg;
	    			RR2[j][i] += 1 - rhxg;
	    		}
	    	}
	    }
	    
	    //calculate N
	    for(int m=0; m<364; m++) {
	    	for(int n=0; n<364; n++) {
	    		N[m] = N[m] + B[m][n];
	    		N[n] = N[n] + B[m][n];
	    	}
	    }
	    
	    //calculate T
	    for(int m=0; m<364; m++) {
	    	for(int n=0; n<364; n++) {
	    		if(m==n) {
	    			for(int i=0; i<364; i++) {
	    				T[m][m] += (RR2[m][i] + RH2[m][i]);
	    			}
	    		} else {
	    			T[m][n] = RR1[m][n] + RH1[m][n];
	    		}
	    		if(N[m] > 0) {
	    			T[m][n] = T[m][n]/N[m];
	    		} else {
	    			T[m][n] = 0;
	    		}
	    	}
	    }
	    
	    //write result T to files
	    BufferedWriter outputWriter = null;
	    outputWriter = new BufferedWriter(new FileWriter("data/T.txt"));
	    
	    for(int m=0; m<364; m++) {
	    	for(int n=0; n<364; n++) {
	    		outputWriter.write(T[m][n] + " ");
	    	}
    		outputWriter.newLine();
	    }
	    outputWriter.flush();  
	    outputWriter.close();
	    
	}
}
