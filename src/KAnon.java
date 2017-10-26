import java.util.*;
import java.io.*; 

public class KAnon
{
	/*
		Method: main
		Preconditions: Existing CSV file
		Postconditions: K-Anonymous table generated
	*/
	public static void main(String[] args) 
	{
		Scanner scanner = new Scanner(System.in); 
		boolean flag = false;
		int input = 0;

        System.out.println(args[0]);
		System.out.println("Processing File... Please wait.");
		KAnonMethods table = new KAnonMethods(importFile(new File(args[0])));
		if (table != null)
			System.out.println("File successfully imported! Current K-Anonymous Value: "+table.evaluateKAnon());
		else
			return;

		while (!flag)
		{
			try
			{
				System.out.println("Please enter a value for K:");
				input = scanner.nextInt();
				if (input < 20 && input > 0)
					break;
			}
			catch (InputMismatchException e)
			{
				System.out.println("Invalid input! Please try again.");
				scanner.next();
				continue;
			}

		}

		ArrayList<Tuple> output = table.makeKAnon(input);

		if (output != null) 
		{
			System.out.println("Table Processed! Data Loss Value: "+table.getDataLoss());
			String[] parts = args[0].split("\\.");
			if(outputFile(output, (parts[0]+"_output.csv")))
				System.out.println("File successfully ouput!");
			else
				System.out.println("File failed to ouput!");
		}
		else
			System.out.println("Error converting file.");
		System.out.println("Program Complete. Exiting.");
	}

	private static ArrayList<Tuple> importFile(File file)
	{
		Scanner console;
		ArrayList<Tuple> output = new ArrayList<Tuple>();
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
		//Process column names here
		while(console.hasNextLine()) //Import file into data structure
		{
			ArrayList<String> tupleInput = new ArrayList<String>();
			Scanner lineInput = new Scanner(console.nextLine());
			lineInput.useDelimiter(",");
			while(lineInput.hasNext()) //Import file into data structure
			{
				tupleInput.add(lineInput.next());
			}
			output.add(new Tuple(tupleInput));
		}
		return output;
	}

	private static boolean outputFile(ArrayList<Tuple> input, String title)
	{
		try
		{
		    PrintWriter writer = new PrintWriter(title, "UTF-8");
		    for (int i = 0; i < input.size(); i++)
		    {
		    	writer.println(input.get(i).printTuple());
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