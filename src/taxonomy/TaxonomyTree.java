package taxonomy;

public class TaxonomyTree
{
	//used to describe the current position for the 
	private TaxonomyNode root;

	public TaxonomyTree()
	{
		root = null;
	}

	public TaxonomyTree(String id, String input)
	{
		root = new TaxonomyNode(id,input);
	}

	public void addNode(String id, String input)
	{
		TaxonomyNode newNode = new TaxonomyNode(id,input,root);
		if (root == null)
		{
			root = newNode;
			return;
		}
		else
			root.addNode(newNode);
	}

	public TaxonomyNode getRoot()
	{
		return root;
	}
    
    public TaxonomyNode getNode(String input)
	{
		return root.getNode(input);
	}

	public String print()
	{
		return root.print();
	}
}