package kanonymity;

import java.util.*;

public class Tuple
{
	private String[] values;
	private int size;

	public Tuple(ArrayList<String> input)
	{
		size = input.size();
		values = new String[size];
		for (int i = 0; i < size; i++)
			values[i] = input.get(i);
	}

	public String get(int i)
	{
		return values[i];
	}

	public String printTuple()
	{
		String output = "";
		for (int i = 0; i < size; i++)
		{
			output += values[i]+", ";
		}
		return output;
	}
}
