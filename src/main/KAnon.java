package main;

import methods.KAnonMethods;
import table.Tuple;
import util.Performance;
import util.RunTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class KAnon
{
	//Preconditon: 	Valid CSV file
	//Postcondtion:	K-Anonymous table generated and output
	//Status:		Coded and efficient
	//Written by:	Moten
	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner(System.in); 



		System.out.println("Processing File... Please wait.");
		long millis = System.currentTimeMillis(); // Start run timer

		KAnonMethods table = new KAnonMethods(importFile(new File(args[0])), Integer.parseInt(args[1]), args[3], -1); //De

		if (table != null)
			System.out.println("File successfully imported! Current K-Anonymous Value: "+table.getCurrentK());
		else
			return;

		table.makeKAnonTopDown();
		ArrayList<Tuple> output = table.getOutput();

		if (output != null) 
		{
			System.out.println("Table Processed! Data Loss Value: "+table.getDataLoss()+"%");
			if(outputFile(output, args[2]))
				System.out.println("File successfully ouput!");
			else
				System.out.println("File failed to ouput!");
		}
		else
			System.out.println("Error converting file.");

		double runtime = (System.currentTimeMillis()-millis)/1000.0;
		System.out.println("Total run time: "+runtime+" seconds");
		System.out.println("Program Complete. Exiting.");
	}

	public void runAnalysis(String method, String filename, String filename_taxonomy, int k, int repeatCount) {
		int maxRows = 5;

		ArrayList<Tuple> data;

		File file = new File("shuffled_" + filename);
		if (file.exists()) {
			data = importFile(file);
		}
		else {
			data = importFile(new File(filename));
			Tuple header = data.remove(0);
			Collections.shuffle(data);
			data.add(0, header);
			outputFile(data, "shuffled_"+filename);
			filename = "shuffled_"+filename;
			// data is now shuffled and matches file
		}


		//		System.out.println("Processing File... Please wait.");
		//			long millis = System.currentTimeMillis(); // Start run timer

		ArrayList<RunTime> runList = new ArrayList<>();
		int scaleFactor = 10;

		for (int i = scaleFactor; i <= 100; i+=scaleFactor) {
			System.out.println("\n\n** New scale factor");

			double runningtime_avg = 0;
			double ncp_avg = 0;
			double averageClassSize_avg = 0;
			int measuredK_avg = 0;

			for (int repeats = 0; repeats < repeatCount; repeats++){
				System.out.println("\n"+method);
				double blockQty = (i / 100.0);
				System.out.println("scale: "+blockQty*100+"%");
				maxRows = (int) Math.floor(data.size() * blockQty);
				if (maxRows >= data.size()) maxRows -= 1;

				KAnonMethods table = new KAnonMethods(data, k, filename_taxonomy, maxRows); //De

				Performance perf = new Performance();
				if (method.equals("KAnon")) {
					perf = table.makeKAnonMond();
				}
				else if (method.equals("TopDown")) {
					perf = table.makeKAnonTopDown();
				}

				runningtime_avg += perf.getRuntime();
				ncp_avg += perf.getNcp();
				averageClassSize_avg += perf.getAverageClassSize();
				measuredK_avg += perf.getMeasuredK();
			}
			runningtime_avg /= repeatCount;
			ncp_avg /= repeatCount;
			averageClassSize_avg /= repeatCount;
			measuredK_avg /= repeatCount;

			RunTime runTime = new RunTime(method, "shuffled_"+filename, k, maxRows, (long)runningtime_avg, ncp_avg, averageClassSize_avg, measuredK_avg);
			System.out.println(runTime);
			runList.add(runTime);
		}
		outputFile(runList, method+"-sc"+scaleFactor+"-re"+repeatCount+"-k"+k+"-analysis_"+filename);
	}

	//Preconditon: 	Valid CSV file
	//Postcondtion:	Array List of tuples generated
	//Status:		Coded and efficient
	//Written by:	Moten
	private static ArrayList<Tuple> importFile(File file)
	{
		Scanner console;
		ArrayList<Tuple> output = new ArrayList<Tuple>();
		int id = 0;
		
		try //Attempt to import file
		{
			console = new Scanner(file); 
			console.useDelimiter(",");

		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error importing file. Please try again.");
			return null;
		}

		while(console.hasNextLine()) //Import file into data structure
		{
			ArrayList<String> tupleInput = new ArrayList<String>();
			Scanner lineInput = new Scanner(console.nextLine());
			lineInput.useDelimiter(",");
			while(lineInput.hasNext()) //Import file into data structure
			{
				tupleInput.add(lineInput.next().trim());
			}
			output.add(new Tuple(tupleInput, id));
			id++;
		}

		return output;
	}

	//Preconditon: 	Valid array list of tuples and title
	//Postcondtion:	K-Anonymous saved to file
	//Status:		Coded and efficient
	//Written by:	Moten
	private static boolean outputFile(ArrayList input, String title)
	{
		try
		{
		    PrintWriter writer = new PrintWriter(title, "UTF-8");
		    for (int i = 0; i < input.size(); i++)
		    {
		    	writer.println(input.get(i).toString());
		    }
		    writer.close();
		} 
		catch (IOException e) 
		{
		   return false;
		}
		return true;
	}
}
