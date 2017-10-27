package table;

import taxonomy.TaxonomyNode;

import java.util.*;

public class Tuple
{
	private static final int TABLESIZE = 10007; 

	private String[] values;
	private int size;
	private long hash;
	private final int id;
	private int classID;
	private String origValue;

	//Preconditon: 	Valid array list of string input
	//Postcondtion:	table.Tuple initialised
	//Status:		Coded and efficient
	//Written by:	Moten
	public Tuple(ArrayList<String> input, int id)
	{
		size = input.size();
		values = new String[size];
		for (int i = 0; i < size; i++)
			values[i] = input.get(i);
		this.id = id;
	}

	public Tuple(ArrayList<String> input, int id, boolean topDown)
	{
		size = input.size();
		values = new String[size];
		for (int i = 0; i < size - 1; i++)
			values[i] = input.get(i);
		this.id = id;
		if(input.get(size - 1).equals("<=50K")) {
			classID = 0;
		}
		else if(input.get(size - 1).equals(">50K")) {
			classID = 1;
		}
		else {
			classID = -1;
		}
	}
	//use only in convertTimTuple
	public Tuple(String[] input, int id, boolean topDown)
	{
		size = input.length - 1;
		values = new String[size];
		for (int i = 0; i < size; i++)
			values[i] = input[i];
		this.id = id;
		if(Integer.parseInt(input[size]) <= 50000) {
			classID = 0;
		}
		else if(Integer.parseInt(input[size]) > 50000) {
			classID = 1;
		}
		else {
			classID = -1;
		}
		origValue = input[size];
	}





	//Preconditon: 	table.Tuple initialised
	//Postcondtion:	Value of field i returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public String get(int i)
	{
		return values[i];
	}

	//Preconditon: 	table.Tuple initialised
	//Postcondtion:	Printed tuple returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public String toString()
	{
		String output = values[0];
		for (int i = 1; i < size; i++)
			output += ", "+values[i];
		return output;
	}

	//Preconditon: 	table.Tuple initialised
	//Postcondtion:	Hash value for tuple returned
	//Status:		Coded 
	//Written by:	Moten
	public int getHash() //Gives inaccurate hashes
	{
		String total = "";
		int output = 0;
		for (int i = 0; i < values.length; i++)
			total += "$"+values[i]; 
		for (int i = 0; i < total.length(); i++)
			output += (int)total.charAt(i);

		return output%TABLESIZE;
	}

	//Preconditon: 	table.Tuple initialised
	//Postcondtion:	table.Tuple ID returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public int getID() //Gives inaccurate hashes
	{		
		return id;
	}

	//Precondition:		DataStructures.table.Tuple init
	//Postcondition:	Size of tuple returned.
	//Status:			Done
	//Written by:		Tim
	public int size() {
		return size;
	}

	public int getClassID() {
		return classID;
	}


	public Tuple convertToOrigTuple(String origValue) {
		ArrayList<String> newVals = new ArrayList<String>();
		for(int i = 0; i < values.length; i++) {
			newVals.add(values[i]);
		}
		//if(classID == 0) {
			newVals.add(origValue);
		//}
		//else {
			//newVals.add(origValue);
		//}


		Tuple t = new Tuple(newVals, -1);
		return t;
	}

	public Tuple convertToTimTuple() {
		return new Tuple(values, id, true);
	}

	public String getOrigVal() {
		return origValue;
	}
}