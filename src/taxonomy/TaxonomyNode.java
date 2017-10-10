package taxonomy;

import java.util.*;

public class TaxonomyNode
{
	private int id;
	private TaxonomyNode parent;
	private ArrayList<TaxonomyNode> children;
	private String name;

	public TaxonomyNode(String id)
	{
		name = id;
		children = null;
	}

	public TaxonomyNode(String id, TaxonomyNode daddy)
	{
		this(id);
		parent = daddy;
	}

	public TaxonomyNode(String id, String input)
	{
		name = id;
		children = new ArrayList<TaxonomyNode>();

		Scanner inChil = new Scanner(input);
		inChil.useDelimiter(",");
		while (inChil.hasNext())
		{
			children.add(new TaxonomyNode(inChil.next(), this));
		}
	}

	public TaxonomyNode(String id, String input, TaxonomyNode daddy)
	{
		this(id, input);
		parent = daddy;
	}

	public void addNode(TaxonomyNode input)
	{
		if (children == null)
			return;

		for (int i = 0; i < children.size(); i++)
		{
			children.get(i).addNode(input);
			if (children.get(i).getName().equals(input.getName()))
				children.set(i, input);
		}
	}

	public Iterator<TaxonomyNode> childrenIterator() 
	{
		return children.iterator();
	}

	public String getName() {

		return name.toLowerCase();
	}

	public String print()
	{
		if (children == null)
			return name+":\n";
		String output = name+":\n";
		for (int i = 0; i < children.size(); i++)
			output += "\t"+children.get(i).print() + " ";
		return output;
	}
}