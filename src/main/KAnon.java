package main;

import methods.KAnonMethods;
import table.Tuple;

import java.util.*;
import java.io.*; 

public class KAnon
{
	//Preconditon: 	Valid CSV file
	//Postcondtion:	K-Anonymous table generated and output
	//Status:		Coded and efficient
	//Written by:	Moten
	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner(System.in); 
		boolean flag = false;
		int input = 0;

		while (!flag) //Get K-Value
		{
			try
			{
				System.out.println("Please enter a value for K:");
				input = scanner.nextInt();
				if (input < 50 && input > 0)
					break;
			}
			catch (InputMismatchException e)
			{
				System.out.println("Invalid input! Please try again.");
				scanner.next();
				continue;
			}

		}

		System.out.println("Processing File... Please wait.");
		long millis = System.currentTimeMillis(); // Start run timer

		KAnonMethods table = new KAnonMethods(importFile(new File(args[0])), input); //De

		if (table != null)
			System.out.println("File successfully imported! Current K-Anonymous Value: "+table.getCurrentK());
		else
			return;

		table.makeKAnonTopDown();
		ArrayList<Tuple> output = table.getOutput();

		if (output != null) 
		{
			System.out.println("Table Processed! Data Loss Value: "+table.getDataLoss()+"%");
			String[] parts = args[0].split("\\.");
			if(outputFile(output, (parts[0]+"_output.csv")))
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
				tupleInput.add(lineInput.next());
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
	private static boolean outputFile(ArrayList<Tuple> input, String title)
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