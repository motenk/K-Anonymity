package taxonomy;

import java.util.*;

public class TaxonomyNode
{
	public TaxonomyNode parent;
	public ArrayList<TaxonomyNode> children;
	public String name;
	public int setSize;
	public int[] classFrequency;

    public TaxonomyNode() {
        parent = null;
        children = null;
        name = null;
    }

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

	public Iterator<TaxonomyNode> childrenIterator() {
		return children.iterator();
	}

	public int childrenSize() {
		return children.size();
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

	/**
	 * Returns the nth child which the record specializes to.
	 * This should be made better.
	 * @t the tuple that is to be specialized.
	 * @attr the attribute that is being specialized on.
	 */
	public int specialize(String attribute) {
		if(attribute.equals(name)) {
			return 0;
		}
		if(children == null) {
			return -1;
		}
		Iterator<TaxonomyNode> itr = children.iterator();
		int i = 0;

		while(itr.hasNext()) {
			if(itr.next().specialize(attribute) == 0) {
				return i;
			}
			i++;
		}
		return -1;
	}
}