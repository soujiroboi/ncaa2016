package uml.cs.yingli.lrmc;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class TestOpenCSV {
	
	public static void main(String arg[]) throws Exception {
	    //finish calculate A and B. write to new csv files.
	    CSVWriter writer = new CSVWriter(new FileWriter("data/A2012-2015.csv"), '\t');
	    // feed in your array (or convert your data to an array)
	    List<String[]> data = new ArrayList<String[]>();
	    data.add(new String[] {"India", "New Delhi"});
	    data.add(new String[] {"United States", "Washington D.C"});
	    data.add(new String[] {"Germany", "Berlin"});
	    
	    writer.writeAll(data);
		writer.close();
	}
}
