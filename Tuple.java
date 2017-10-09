import java.util.*;

public class Tuple
{
	private String[] values;
	private int size;
	private long hash;
	private final int id;

	//Preconditon: 	Valid array list of string input
	//Postcondtion:	Tuple initialised
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

	//Preconditon: 	Tuple initialised
	//Postcondtion:	Value of field i returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public String get(int i)
	{
		return values[i];
	}

	//Preconditon: 	Tuple initialised
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

	//Preconditon: 	Tuple initialised
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
			output = output + (int)total.charAt(i);

		return output;
	}

	//Preconditon: 	Tuple initialised
	//Postcondtion:	Tuple ID returned
	//Status:		Coded and efficient
	//Written by:	Moten
	public int getID() //Gives inaccurate hashes
	{		
		return id;
	}

}