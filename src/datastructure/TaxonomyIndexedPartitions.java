package datastructure;

import table.Table;
import table.Tuple;
import taxonomy.TaxonomyTree;
import taxonomy.TaxonomyNode;

import java.util.ArrayList;
import java.util.Iterator;

//suggested by the paper to be implemented for TopDown
//each node represents a generalized record over the union of children?? (not sure what its a union over.)
//each child node represents a specialization of the parent node on exactly 1 attribute
//each leaf node is the set of compressed data records having the same generalized record - leaf partition
//For each cut in a set of children_i, P_x denotes a leaf partition whose generalized record contains
//x and Link_x denotes the link of all P_x with the head of Link_x stored with x
public class TaxonomyIndexedPartitions {

	public boolean verbose;

	int classCount = 2;
	private Node root;
	//set of Links over the current set of cuts available.
	//Maybe have each cut store the links between the appropriate nodes

	public ArrayList<Cut> activeCuts;
	private ArrayList<Node> leaf;


	//a list of the leaves related to the a given cut.
	//for the active cut X, the list leafList.get(X) is the list of all leaf nodes
	//that contain QID that contain X.
	//list i is related to the ith active cut.
	//activeCuts.leafList;

	public TaxonomyIndexedPartitions(ArrayList<Tuple> t, ArrayList<TaxonomyTree> trees, boolean[] isNumerical, boolean verbose) {
		this.verbose = verbose;
		ArrayList<String> rootTuple = new ArrayList<>();
		for(int i = 0; i < trees.size(); i++) {
			rootTuple.add("*");
		}
		root = new Node(t, new Tuple(rootTuple, -1));

		activeCuts = new ArrayList<>();
		leaf = new ArrayList<>();
		leaf.add(root);

		Iterator<TaxonomyTree> itr = trees.iterator();
		TaxonomyNode currentRoot;
		int attributeCount = 0;
		if(verbose) {
			System.out.println("Current active cuts:");
		}
		while(itr.hasNext()) {
			TaxonomyTree tree = itr.next();
			currentRoot = tree.root;


			if(isNumerical[attributeCount]) {
				int[] vals = new int[t.size()];
				for(int j = 0; j < t.size(); j++) {
					vals[j] = Integer.parseInt(t.get(j).get(attributeCount));
				}
				currentRoot.setNumericVals(vals);
				currentRoot.generateChildren();
			}

			Cut c = new Cut(currentRoot, root, attributeCount);
			c.valid = true;
			c.beneficial = true;
			activeCuts.add(c);
			c.getCountStats();

			Iterator<TaxonomyNode> childItr = currentRoot.children.iterator();
			Node[] tmpChild = new Node[currentRoot.children.size()];
			for(int j = 0; j < tmpChild.length; j++) {
				TaxonomyNode childTax = childItr.next();

				ArrayList<String> tmpTuple = new ArrayList<>();
				for(int k = 0; k < trees.size(); k++) {
					if(k == attributeCount) {
						tmpTuple.add(childTax.name);
					}
					else {
						tmpTuple.add("*");
					}
				}

				tmpChild[j] = new Node(root, new Tuple(tmpTuple, -1));
				Cut childCut = new Cut(childTax, tmpChild[j], c.attribute);


				c.children[j] = childCut;
			}
			root.splitRecordsBetweenChildren(currentRoot, attributeCount, isNumerical[attributeCount], tmpChild);
			for(int j = 0; j < tmpChild.length; j++) {
				if(!c.children[j].getCountStats()) {
					//c.children[j] = null;
				}
			}
			root.tmpChildren.add(tmpChild);
			attributeCount++;

		}

		Iterator<Cut> cutItr = activeCuts.iterator();
		int j = 0;
		while(cutItr.hasNext()) {
			Cut current = cutItr.next();
			current.initialiseScore(j);
			j++;
		}
		if(verbose) {
			print();
		}
		root.setValidCuts(activeCuts);
	}

	public void print() {
		Iterator<Cut> cutItr = activeCuts.iterator();
		int j = 0;
		while (cutItr.hasNext()) {
			Cut current = cutItr.next();
			System.out.println(j);
			System.out.println("attribute: " + current.attribute);
			System.out.println("representation: " + current.t.name);
			System.out.println("score: " + current.initialiseScore(j));
			System.out.println("List size: " + current.leafList.size());
			System.out.println("count: " + current.count);
			System.out.println("------------------------------------------");
			j++;
		}
	}

	public Cut getBestCut(boolean verbose) {
		Iterator<Cut> itr = activeCuts.iterator();
		double bestScore = Double.MIN_VALUE;
		Cut best = null;
		int i = 0, j = 0;
		while(itr.hasNext()) {
			Cut current = itr.next();
			if(current.score > bestScore) {
				bestScore = current.score;
				best = current;
				j = i;
			}
			i++;
		}
		if(verbose) {
			System.out.println("Best cut: " + j);
			System.out.println("cuts: " + activeCuts.size());
			System.out.println("attr: " + best.attribute);
			System.out.println(best.children.length);
		}
		return best;
	}

	public boolean checkValidityAndBenefical(int kValue, boolean verbose) {
		for(int i = 0; i < activeCuts.size(); i++) {
			Cut current = activeCuts.get(i);
			current.getCountStats();
			if (!current.valid || !current.beneficial) {
				if(verbose) {
					System.out.println("removing: " + i);
					System.out.println("attr: " + current.attribute);
					System.out.println("val: " + current.t.name);
					System.out.println("valid: " + current.valid);
					System.out.println("beneficial: " + current.beneficial);
				}
				activeCuts.remove(current);
				i--;

			}
			else if(!current.validCheck(kValue)){
				activeCuts.remove(current);
				i--;

			}
		}
		if(verbose && activeCuts.size() == 0) {
			System.out.println("no valid or beneficial cuts remaining");
		}
		return (activeCuts.size() != 0);
	}










	//From the paper:
	//We refine each leaf partiion P_best found on Link_Best as follows:
	//For each value c in child(Best), a child partition P_C is created under P_Best
	//and it's data records are split among the child partitions:
	//P_C contains a data record in P_best if c generalizes the corresponding domain value in the record.
	//Empty P_C are removed.
	//Link P_C to every Link_x that P_Best was previously linked (except Link_Best)
	//Mark C as a beneficial cut if R_C has more than one class (ie the set of data records generalized to c)
	//Add new set of cuts.
	//Remove old cut.
	public void performCut(Cut cut, boolean[] isNumeric) {
		//int cutIndex = activeCuts.indexOf(cut);
		//iterator over the records whose QID contain the cut
		Iterator<Node> itr = cut.leafList.iterator();


		//remove the original cut to the set of active cuts.
		activeCuts.remove(cut);



		ArrayList<ArrayList<Node>> childrenPartitionList = new ArrayList<ArrayList<Node>>();

		for(int i = 0; i < cut.t.children.size(); i++) {
			childrenPartitionList.add(new ArrayList<Node>());
		}

		//For each partition P_best: (leaves that cotain the value represented by the cut)
		while(itr.hasNext()) {
			Node current = itr.next();
			Iterator<Cut> cutItr = activeCuts.iterator();


			leaf.remove(current);
			//create a new child partition (for each child in the taxTree)
			//sets up children & splits data records up.
			if(current.validCuts == null) {
				continue;
			}
			int cutIndex = current.validCuts.indexOf(cut);
			current.setChildren(cutIndex);
			current.validCuts.remove(cut);
			for(int i = 0; i < current.children.length; i++) {
				leaf.add(current.children[i]);
				current.children[i].setValidCuts(current.validCuts);
				current.children[i].validCuts.add(cut.children[i]);
			}
			//split data records up

			//
			for(int i = 0; i < cut.t.children.size(); i++) {
				if(current.children[i].count() != 0) {
					childrenPartitionList.get(i).add(current.children[i]);
				}
			}

			cutItr = activeCuts.iterator();
			//For every list of partitions if it contains the current partition then get rid of it,
			// and replace it with the *current* partitions children
			while(cutItr.hasNext()) {
				Cut currentCut = cutItr.next();

				if(currentCut != cut && currentCut.leafList.contains(current)) {
					currentCut.leafList.remove(current);
					for(int j = 0; j < current.children.length; j++) {
						currentCut.leafList.add(current.children[j]);

					}

				}
			}


		}


		for(int i = 0; i < cut.children.length; i++) {
			activeCuts.add(cut.children[i]);
			cut.children[i].generateChildren();
		}
		for(int i = 0; i < activeCuts.size(); i++) {
			activeCuts.get(i).updateChildCuts();
		}
		Iterator<Node> leafIterator = leaf.iterator();
		while(leafIterator.hasNext()) {
			Node currentLeaf = leafIterator.next();
			//generates possible children of the leaves based on the active cuts. Then splits the data into subgroups.
			//also adds the
			currentLeaf.generateTmpChildren(isNumeric);

			//add to possible children
		}
		for(int i = 0; i < activeCuts.size(); i++) {
			activeCuts.get(i).getCountStats();
		}
		for(int i = 0; i < cut.children.length; i++) {

			if(cut.children[i].t.children != null && cut.children[i].t.children.size() != 0) {
				cut.children[i].initialiseScore(cut.children[i].attribute);
			}
		}
		for(int i = 0; i < activeCuts.size(); i++) {
			if(cut.attribute != activeCuts.get(i).attribute) {
				activeCuts.get(i).updateScore(i);
			}


		}


	}

	public ArrayList<Tuple> getPrivateTable() {
		ArrayList<Tuple> privateTable = new ArrayList<>();
		Iterator<Node> itr = leaf.iterator();
		while(itr.hasNext()) {
			Node current = itr.next();
			int count = 0;
			/*for(int i = 0; i < current.frequency.length; i++) {
				/*count += current.frequency[i];
				for(int j = 0; j < current.frequency[i]; j++) {
					Tuple t = current.tuple.convertToOrigTuple(i);
					//System.out.println(t.toString());
					privateTable.add(t);
				}
			}*/
			for(int i = 0; i < current.subTable.size(); i++) {
				Tuple t = current.subTable.get(i).convertToOrigTuple();
				privateTable.add(t);
			}

		}
		return privateTable;
	}
}