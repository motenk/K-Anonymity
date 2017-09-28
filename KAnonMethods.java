import java.util.*;
import java.io.*; 

public class KAnonMethods
{
	private double dataLoss = 0;
	private int size = 0;
	private int k = 0;
	private int currentK = 0;
	private ArrayList<Tuple> table, outputTable, baseTable;
	private Tuple headers;

	//Preconditon: 	Valid ArrayList of tuples given as input
	//Postcondtion:	Object initialised
	//Status:		Coded and efficient
	//Written by:	Moten
	KAnonMethods(ArrayList<Tuple> input, int k)
	{
		headers = input.get(0); //Store the header
		table = input;
		table.remove(0); //Remove the header from the working table
		baseTable = table;
		outputTable = table;
		size = table.size();
		this.k = k;
	}

	//Preconditon: 	KAnonMethods initialised
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

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	Output table returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public ArrayList<Tuple> getOutput() //If we want to re add the headers then do it here
	{
		return outputTable;
	}

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	Current K value of table returned
	//Status:		Coded and super inefficient
	//Written by:	Moten
	private int evaluateKAnon(ArrayList<Tuple> input) //THIS REALLY NEEDS TO BE FUCKING IMPROVED (jesus)
	{
		int min = 10000000;
		ArrayList<Integer> values = new ArrayList<Integer>();
		ArrayList<Integer> occurence = new ArrayList<Integer>();
		for (int i = 0; i < input.size(); i++)
		{
			Integer temp = input.get(i).getHash();
			int location = values.indexOf(temp);
			if (location == -1)
			{
				values.add(temp);
				occurence.add(1);
			}
			else
			{
				occurence.set(location, occurence.get(location)+1);
			}
			
		}

		for (int i = 0; i < occurence.size(); i++)
		{
			if (min > occurence.get(i))
				min = occurence.get(i);
		}

		return min;
	}

	//Preconditon: 	KAnonMethods initialised
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
		evaluateDataLoss();
		return dataLoss;
	}

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	Specific tuple value returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public Tuple getTuple(int i)
	{
		return table.get(i);
	}

	//Preconditon: 	makeKAnon() run at least once
	//Postcondtion:	Data loss calculated and stored in object
	//Status:		Incomplete
	//Written by:	
	private void evaluateDataLoss() 
	{
		dataLoss = 0;
	}

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	All tuples in table improved in accuracy by 1 step
	//Status:		Incomplete
	//Written by:	
	private ArrayList<Tuple> reduceEC(ArrayList<Tuple> input)
	{

		return table;
	}

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	All tuples in table set to maximum anonymity
	//Status:		Incomplete
	//Written by:	
	private ArrayList<Tuple> maximize(ArrayList<Tuple> input)
	{
		return null;
	}

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	Taxonomy trees are assigned to all tuples
	//Status:		Incomplete
	//Written by:	
	private ArrayList<Tuple> assignTaxonomy(ArrayList<Tuple> input)
	{
		ArrayList<TaxonomyTree> trees = importTrees();

		return null;
	} 

	//Preconditon: 	KAnonMethods initialised
	//Postcondtion:	Taxonomy trees are imported from file and returned
	//Status:		Incomplete
	//Written by:	Moten
	private static ArrayList<TaxonomyTree> importTrees()
	{
		Scanner console;
		ArrayList<TaxonomyTree> output = new ArrayList<TaxonomyTree>();

		return output;
	}
}