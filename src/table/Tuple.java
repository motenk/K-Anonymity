package table;

import taxonomy.TaxonomyNode;

import java.util.ArrayList;
import java.util.Iterator;

public class Tuple
{
	private static final int TABLESIZE = 10007; 

	private String[] values;
	private int size;
	private long hash;
	private final int id;

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

	//determine which member of the children is the appropriate match for the data value
	//Precondition:
	//Postcondition:	Return the generalized value in the attribute field, in terms of the options provided in treeChildren.
	//Status:			Note done.
	//Written by:		Tim
	public int specialize(int attribute, Iterator<TaxonomyNode> treeChildren) {
		return -1;
	}

}
