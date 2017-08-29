import java.util.*;

public class KAnonMethods
{
	private int dataLoss = 0;
	private int size = 0;
	private final ArrayList<Tuple> table;

	KAnonMethods(ArrayList<Tuple> table)
	{
		this.table = table;
		size = table.size();
	}

	public ArrayList<Tuple> makeKAnon(int k)
	{
		return table;
	}

	public int evaluateKAnon()
	{
		int minimum = 100000;
		for (int i = 0; i < size; i++)
		{

		}
		return 0;
	}

	public int getDataLoss()
	{
		evaluateDataLoss();
		return dataLoss;
	}

	public Tuple getTuple(int i)
	{
		return table.get(i);
	}

	private void evaluateDataLoss()
	{
		dataLoss = 2;
	}
}