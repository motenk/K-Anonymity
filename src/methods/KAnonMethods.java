package methods;

import table.Tuple;
import taxonomy.TaxonomyTree;
import util.Performance;

import java.util.*;
import java.io.*;

public class KAnonMethods
{
	private static final int TABLESIZE = 10007; 

	private double dataLoss = 0;
	private int size = 0;
	private int k = 0;
	private int currentK = 0;
	private ArrayList<Tuple> table, outputTable, baseTable;
	private Tuple headers;
	private String taxonomyName;
	private int maxRows = -1;

	//Preconditon: 	Valid ArrayList of tuples given as input
	//Postcondtion:	Object initialised
	//Status:		Coded and efficient
	//Written by:	Moten
	public KAnonMethods(ArrayList<Tuple> input, int k, String taxonomyName, int _maxRows)
	{
		maxRows = _maxRows;
		headers = input.get(0); //Store the header
		table = input;
		table.remove(0); //Remove the header from the working table
		// remove extra rows if greater than -1 (no limit) and 0 (invalid)
		if (maxRows > 0) table = new ArrayList<>(table.subList(0, maxRows));
		baseTable = table;
		outputTable = table;
		size = table.size();
		this.k = k;
		this.taxonomyName = taxonomyName;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	K-Anonymised table calculated
	//Status:		
	//Written by:	Moten
	public void makeKAnon() //Main algorithm
	{
		currentK = evaluateKAnon(table);

		if (currentK == k) //Check to see if table is already K-Anonymous
			return;

		assignTaxonomy(table); //Assign the taxonomy trees for all tuples

		table = maximize(table); //Maximize the value of all tuples
		/*
		currentK = evaluateKAnon(outputTable);
		while (currentK >= k) //Keep improving data as long as the next reduction is still K-Anonymous
		{
			table = outputTable; //Table is now the reduced (and known K-Anonymous table)
			outputTable = reduceEC(table); //Further improve all tuples
			currentK = evaluateKAnon(outputTable);
		}
		outputTable = table; //Set the output table to be equal to the last known good value
		*/
	}

	public Performance makeKAnonMond()
	{
		BasicMondrian bm = new BasicMondrian(table, k, importTrees());
		Performance perf = bm.mondrianAlgorithm();
		outputTable = bm.getResults();
		dataLoss = bm.getNcp();
		return perf;
	}

	public Performance makeKAnonTopDown()
	{
		TopDown td = new TopDown(table, k, importTrees());
		Performance perf = td.topDownAlgorithm();
		perf.setAverageClassSize(td.generateAverageClassValue());
		outputTable = td.getResults();
		//i dont have an inherent data loss function.
		return perf;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Output table returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public ArrayList<Tuple> getOutput() //If we want to re add the headers then do it here
	{
		return outputTable;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Current K value of table returned
	//Status:		Coded and mostly efficient. 
	//Written by:	Moten
	private int evaluateKAnon(ArrayList<Tuple> input) 
	{
		int min = 10000000;
		hashItem hashes[] = new hashItem[TABLESIZE];
		for (int i = 0; i < input.size(); i++)
		{
			int hash = input.get(i).getHash();
			hashItem temp = new hashItem(input.get(i).toString(), hash); 
			if (hashes[hash] == null)
				hashes[hash] = temp;
			else
				hashes[hash].addHash(temp);
		}

		for (int i = 0; i < TABLESIZE; i++)
		{
			if (hashes[i] != null)
			{
				int size = hashes[i].minSize();
				if (min > size)
					min = size;
			}
		}

		return min;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Current K value of output table returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public int getCurrentK()
	{
		if (currentK == 0)
			currentK = evaluateKAnon(outputTable);
		return currentK;
	}

	//Preconditon: 	evaluateDataLoss() run at least once
	//Postcondtion:	Data loss value returned as a percentage
	//Status:		Coded and efficient
	//Written by:	Moten
	public double getDataLoss()
	{
		return dataLoss;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Specific tuple value returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public Tuple getTuple(int i)
	{
		return table.get(i);
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	All tuples in table improved in accuracy by 1 step
	//Status:		Incomplete
	//Written by:	
	private ArrayList<Tuple> reduceEC(ArrayList<Tuple> input)
	{

		return table;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	All tuples in table set to maximum anonymity
	//Status:		Incomplete
	//Written by:	
	private ArrayList<Tuple> maximize(ArrayList<Tuple> input)
	{
		return null;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Taxonomy trees are assigned to all tuples
	//Status:		Incomplete
	//Written by:	
	private ArrayList<Tuple> assignTaxonomy(ArrayList<Tuple> input)
	{
		ArrayList<TaxonomyTree> trees = importTrees();

		return null;
	} 

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Taxonomy trees are imported from file and returned
	//Status:		Coded
	//Written by:	Moten
	private ArrayList<TaxonomyTree> importTrees()
	{
		Scanner console;
		ArrayList<TaxonomyTree> output = new ArrayList<TaxonomyTree>(); //Output arraylist of taxonomy trees. Each one relates to a field
		String id = "";
		String input = "";
		File file = new File(taxonomyName);
		
		try //Attempt to import file
		{
			console = new Scanner(file); 

		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error importing file. Please try again.");
			return null;
		}

		while(console.hasNextLine()) //Import file into data structure
		{
			Scanner line = new Scanner(console.nextLine());
			line.useDelimiter(":");

			if (line.hasNext())
			{
				id = line.next();
				if (id.charAt(0) == '$' || !line.hasNext())
					break;

				input = line.next();

				id = id.replaceAll("\\s+","");
				input = input.replaceAll("[{=}]","");
				if (input.charAt(0) == '*')
				{
					output.add(new TaxonomyTree("*", input.substring(1)));
				}
				else
				{
					output.get(output.size()-1).addNode(id, input);
				}
			}
		}/*
		for (TaxonomyTree t : output) 
		{
			System.out.println(t.print());
		}*/
		return output;
	}

	//Preconditon: 	methods.KAnonMethods initialised
	//Postcondtion:	Average run time of method printed to console
	//Status:		Coded
	//Written by:	Moten
	private void testTime()
	{
		long millis;
		long totalTime = 0;
		for (int i = 0; i < 100; i++)
		{
			millis = System.currentTimeMillis(); // Start run timer

			evaluateKAnon(table); //METHOD TO BE TESTED

			totalTime += System.currentTimeMillis()-millis;
		}
		totalTime = totalTime/100;
		System.out.println("Average run time of method in milliseconds: "+totalTime);
	}


	private class hashItem
	{
		private String value;
		private int hash;
		private hashItem next;
		private int size;

		private hashItem(String s, int in) {
			value = s;
			hash = in;
			next = null;
			size = 1;
		}

		private boolean matchName(String input) 
		{
			if (input.equals(value))
				return true;
			return false;
		}

		private boolean matchHash(int input)
		{
			if (input == hash)
				return true;
			return false;
		}

		public void addHash(hashItem input)
		{
			if (input.matchName(value))
			{
				size++;
				return;
			}
			if (next == null)
				next = input;
			else
				next.addHash(input);
		}

		public int minSize()
		{
			if (next == null)
				return size;
			int smaller = next.minSize();
			if (smaller < size)
				return smaller;
			return size;
		}

		private hashItem getNext()
		{
			return next;
		}
	}
}
