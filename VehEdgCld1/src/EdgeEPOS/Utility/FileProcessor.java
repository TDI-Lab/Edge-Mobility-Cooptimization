package EdgeEPOS.Utility;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

public class FileProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		convercsvtodat();
	}
	private static void convercsvtodat() {
		// TODO Auto-generated method stub
		String CSV_SEPARATOR = ",";
		String line;
		String str, str1, str2, str3;
		String[] data;
		
		//for requests:
		
		String Loadpath = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\request\\LoadReq.csv";
		String Srvpath = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\request\\SrvReq.csv";
		
		
		String Loadpath1 = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\request\\LoadReq.dat";
		String Srvpath1 = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\request\\SrvReq.dat";
		
		try {
			LineNumberReader csvReader = new LineNumberReader(new FileReader(Loadpath));
			LineNumberReader csvReader1 = new LineNumberReader(new FileReader(Srvpath));
			
			PrintWriter out = new PrintWriter(new FileWriter(Loadpath1));
			PrintWriter out1 = new PrintWriter(new FileWriter(Srvpath1));
			
			csvReader1.readLine();
			csvReader.readLine();
			
			for (int i = 0; i<12; i++) { 
				 if ((line = csvReader1.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					for (int j = 0; j<473; j++) {
					 	str1 = i+" "+j +" "+ Integer.parseInt(data[j]);
						out1.println(str1);
						
					}
				}
				out1.println();	
				
			}
			for (int i = 0; i<12; i++) { 
				 if ((line = csvReader.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					for (int j = 0; j<473; j++) {
					 	str = i+" "+j +" "+ Double.parseDouble(data[j]);
						out.println(str);
					}
				}
				
				out.println();	
			}	
			 		
			out.close();
			out1.close();
			csvReader.close();
		 }
		 catch (IOException e) {
		 	      System.out.print("Error: " + e);
		 	      System.exit(1);
		 }
		//for responses:
		
		Loadpath = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\response\\LoadRes.csv";
		Srvpath = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\response\\SrvRes.csv";
		
		
		Loadpath1 = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\response\\LoadRes.dat";
		Srvpath1 = "D:\\project2\\MyPlots\\AfterRun\\LoadPerAP\\response\\SrvRes.dat";
		
		try {
			LineNumberReader csvReader = new LineNumberReader(new FileReader(Loadpath));
			LineNumberReader csvReader1 = new LineNumberReader(new FileReader(Srvpath));
			
			PrintWriter out = new PrintWriter(new FileWriter(Loadpath1));
			PrintWriter out1 = new PrintWriter(new FileWriter(Srvpath1));
			
			csvReader1.readLine();
			csvReader.readLine();
			
			for (int i = 0; i<12; i++) { 
				 if ((line = csvReader1.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					for (int j = 0; j<473; j++) {
					 	str1 = i+" "+j +" "+ Integer.parseInt(data[j]);
						out1.println(str1);
						
					}
				}
				out1.println();	
				
			}
			for (int i = 0; i<12; i++) { 
				 if ((line = csvReader.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					for (int j = 0; j<473; j++) {
					 	str = i+" "+j +" "+ Double.parseDouble(data[j]);
						out.println(str);
					}
				}
				
				out.println();	
			}	
			 		
			out.close();
			out1.close();
			csvReader.close();
		 }
		 catch (IOException e) {
		 	      System.out.print("Error: " + e);
		 	      System.exit(1);
		 }
		
	}


}
