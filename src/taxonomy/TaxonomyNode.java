package taxonomy;

import java.util.*;

public class TaxonomyNode
{
	public TaxonomyNode parent;
	public ArrayList<TaxonomyNode> children;
	public String name;
	public int[] numericVals;

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
		if(children == null)
			return null;
		else
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

    public TaxonomyNode getNode(String input)
    {
        if (input.equals(name.toLowerCase()))
            return this;
        if (children == null)
            return null;
        for (int i = 0; i < children.size(); i++)
        {
			TaxonomyNode output = children.get(i).getNode(input);
			if (output != null)
				return output;
        }
        return null;
    }

	/**
	 * Returns the nth child which the record specializes to.
	 * This should be made better.
	 * @t the tuple that is to be specialized.
	 * @attr the attribute that is being specialized on.
	 */
	public int specialize(String attribute, boolean isNumeric) {
		Iterator<TaxonomyNode> itr = null;
		if(children != null)
			itr = children.iterator();
		if(isNumeric) {
			//following only works if the tree is binary.
			String tmp = children.get(0).name;
			if(Integer.parseInt(attribute) <= Double.parseDouble(tmp.substring(tmp.indexOf("-") + 1))) {
				return 0;
			}
			else {
				return 1;
			}
		}
		int i = 0;
		if(attribute.toLowerCase().equals(name)) {
			return 0;
		}
		while(itr != null && itr.hasNext()) {
			if(itr.next().specialize(attribute, false) != -1) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public void setNumericVals(int[] numericVals) {
		//sort array.
		this.numericVals = numericVals;
	}

	public void generateChildren() {
		ArrayList<Integer> leftList = new ArrayList<>();
		ArrayList<Integer> rightList = new ArrayList<>();
		children = new ArrayList<>();

		int[] orderedArray = new int[numericVals.length];
		for(int i = 0; i < numericVals.length; i++) {
			orderedArray[i] = numericVals[i];
		}
		for(int i = 0; i < orderedArray.length - 1; i++) {
			int smallest = orderedArray[i];
			int index = i;
			for(int j = i + 1; j < orderedArray.length; j++) {
				if(orderedArray[j] < smallest) {
					smallest = orderedArray[j];
					index = j;
				}
			}
			orderedArray[index] = orderedArray[i];
			orderedArray[i] = smallest;
			if(i == orderedArray.length / 2) {
				break;
			}
		}
		double split;
		if(orderedArray.length % 2 == 0) {
			split = orderedArray[(orderedArray.length/2) - 1] + orderedArray[orderedArray.length/2] / 2;
		}
		else {
			split = orderedArray[(orderedArray.length - 1)/2];
		}
		System.out.println("SPLIT: " + split);
		//int split = numericVals[(int) Math.random() * numericVals.length];
		int min, max;
		if(name.equals("*")) {
			min = Integer.MAX_VALUE;
			max = Integer.MIN_VALUE;
			for(int i = 0; i < numericVals.length; i++) {
				if(numericVals[i] < min ) {
					min = numericVals[i];
				}
				if(numericVals[i] > max) {
					max = numericVals[i];
				}
				if(numericVals[i] < split) {
					leftList.add(numericVals[i]);
				}
				else {
					rightList.add(numericVals[i]);
				}
			}
		}
		else {
			String left = name.substring(0, name.indexOf("-"));
			String right = name.substring(name.indexOf("-") + 1);
			min = Integer.parseInt(left);
			max = Integer.parseInt(right);
		}
		children.add(new TaxonomyNode(min + "-" + split, this));
		int[] leftArray = new int[leftList.size()];
		for(int i = 0; i < leftList.size(); i++) {
			leftArray[i] = leftList.get(i);
		}
		children.get(0).numericVals = leftArray;
		children.add(new TaxonomyNode((split + 1) + "-" + max, this));
		int[] rightArray = new int[leftList.size()];
		for(int i = 0; i < leftList.size(); i++) {
			rightArray[i] = leftList.get(i);
		}
		children.get(1).numericVals = rightArray;
	}
}