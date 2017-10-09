package taxonomy;

import java.util.*;

public class TaxonomyNode
{
	private int id;
	private TaxonomyNode parent;
	private ArrayList<TaxonomyNode> children;
	private String value;

	public Iterator<TaxonomyNode> childrenIterator() {
		return children.iterator();
	}

	public String getValue() {
		return value;
	}
}