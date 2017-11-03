package datastructure;

//suggested by the paper to be implemented for TopDown
//each node represents a generalized record over the union of children?? (not sure what its a union over.)
//each child node represents a specialization of the parent node on exactly 1 attribute
//each leaf node is the set of compressed data records having the same generalized record - leaf partition
//For each cut in a set of children_i, P_x denotes a leaf partition whose generalized record contains
//x and Link_x denotes the link of all P_x with the head of Link_x stored with x
public class TaxonomyIndexedPartitions {


//	private Node root;
//	//set of Links over the current set of cuts available. Maybe have each cut store the links between the appropriate nodes
//
//	private ArrayList<Cut> activeCuts;
//
//	public TaxonomyIndexedPartitions(Table t, ArrayList<TaxonomyTree> trees) {
//		root = new Node(t);
//		activeCuts = new ArrayList<>();
//		Iterator<TaxonomyTree> itr = trees.iterator();
//		TaxonomyNode currentRoot;
//		while(itr.hasNext()) {
//			currentRoot = itr.next().getRoot();
//			activeCuts.add(new Cut(currentRoot));
//		}
//	}
//
//	//A cut describes the current generalized value which can be specialized.
//	//Each cut contains the list of nodes in the tree that contain records generalized to the value of the cut.
//	private class Cut {
//		String value;
//
//
//		//the generalization in the taxonomy tree
//		TaxonomyNode taxNode;
//		//list of nodes in the TIPs that contain the generalization
//		ArrayList<Node> list;
//
//		//a cut is beneficial as long as the tuples that it represents contains more than 1 class
//		boolean beneficial;
//
//		public Cut(TaxonomyNode taxNode) {
//			this.taxNode = taxNode;
//			list = new ArrayList<Node>();
//			value = taxNode.getName();
//			beneficial = false;
//		}
//
//		public Iterator<Node> listIterator() {
//			return list.iterator();
//		}
//
//		public Iterator<TaxonomyNode> taxNodeChildIterator() {
//			return taxNode.childrenIterator();
//		}
//
//		public boolean listContains(Node n) {
//			return list.contains(n);
//		}
//
//		public void listRemove(Node current) {
//			list.remove(current);
//		}
//
//		public void listAdd(ArrayList<Node> list) {
//			this.list.addAll(list);
//		}
//
//		public void isBeneficial() {
//			Iterator<Node> itr = list.iterator();
//			ArrayList<Integer> classSet = new ArrayList<>();
//			int count = 0;
//			while(itr.hasNext()) {
//				Iterator<Integer> itr2 = itr.next().classSet().iterator();
//				while(itr2.hasNext()) {
//					Integer current = itr2.next();
//					if(classSet.contains(current)) {
//						classSet.add(current);
//					}
//					if(classSet.size() > 1) {
//						break;
//					}
//				}
//				if(classSet.size() > 1) {
//					beneficial = true;
//					break;
//				}
//			}
//		}
//	}
//
//	private class Node {
//		Node parent;
//		ArrayList<Node> children;
//		//ArrayList<Tuple> data;
//		Table subTable;
//
//		int countRecords;
//		//array contains the frequency per class (ie the sensitive data)
//		int[] frequency;
//
//		public Node(Table t) {
//			parent = null;
//			children = new ArrayList<Node>();
//			subTable = new Table(t.getData());
//		}
//
//		public Node(Node parent) {
//			this.parent = parent;
//			children = new ArrayList<Node>();
//			subTable = new Table();
//		}
//
//		public void setChildren(ArrayList<Node> children) {
//			this.children = children;
//		}
//
//
//
//		public void dataAdd(Tuple t) {
//			subTable.addTuple(t);
//		}
//
//
//		//Precondition:		Children initialized and the same size as the children of cut.taxNode
//		//Postcondition:	Splits the data between the parent and the appropriate children based on the specialization
//		//Status:			Requires implementation of record knowing what specialization it belongs to
//		//Written by:		Tim
//		public void splitRecordsBetweenChildren(TaxonomyNode taxNode) {
//			Iterator<Tuple> itr = subTable.dataIterator();
//			while(itr.hasNext()) {
//				Tuple current = itr.next();
//				int generalized = current.getGeneralizedIndex(taxNode);
//				children.get(generalized).dataAdd(current);
//			}
//		}
//
//		//Precondition:		Children initialized.
//		//Postcondition:	Removes the children that relate to 0 records.
//		//Status:
//		//Written by:	 	Tim
//		public void removeEmptyChildren() {
//			for(int i = 0; i < children.size(); i++) {
//				if(children.get(i).countRecords == 0) {
//					children.remove(i);
//					i--;
//				}
//			}
//		}
//
//		public void addChild(Node n) {
//			children.add(n);
//		}
//
//		public ArrayList<Node> getChildren() {
//			return children;
//		}
//
//		//Precondition:		The table has been defined.
//		//Precondition:		Return the set of classes defined by the sub-table.
//		//Status:			Not done.
//		//Written by:
//		//Note:				Should possibly be a function of the table object.
//		public ArrayList<Integer> classSet() {
//			return subTable.getClassSet();
//		}
//	}
//
//	//From the paper:
//	//We refine each leaf partiion P_best found on Link_Best as follows:
//		//For each value c in child(Best), a child partition P_C is created under P_Best
//		//and it's data records are split among the child partitions:
//			//P_C contains a data record in P_best if c generalizes the corresponding domain value in the record.
//		//Empty P_C are removed.
//		//Link P_C to every Link_x that P_Best was previously linked (except Link_Best)
//	//Mark C as a beneficial cut if R_C has more than one class (ie the set of data records generalized to c)
//	//Add new set of cuts.
//	//Remove old cut.
//	public void performSpecialization(Cut cut) {
//		Iterator<Node> itr = cut.listIterator();
//
//		//adding the cuts to the set of active cuts and marking them as beneficial (or not)
//		Iterator<TaxonomyNode> taxChildItr = cut.taxNodeChildIterator();
//		ArrayList<Cut> newCuts = new ArrayList<>();
//		while(taxChildItr.hasNext()) {
//			TaxonomyNode current = taxChildItr.next();
//			newCuts.add(new Cut(current));
//		}
//
//
//		while(itr.hasNext()) {
//			Node current = itr.next();
//			Iterator<Cut> cutItr = newCuts.iterator();
//			while(cutItr.hasNext()) {
//				Cut currCut = cutItr.next();
//				Node n = new Node(current);
//				currCut.list.add(n);
//				current.addChild(n);
//			}
//			current.splitRecordsBetweenChildren(cut.taxNode);
//			current.removeEmptyChildren();
//
//			cutItr = activeCuts.iterator();
//			while(cutItr.hasNext()) {
//				Cut currentCut = cutItr.next();
//				if(currentCut == cut) {
//					continue;
//				}
//				if(currentCut.listContains(current)) {
//					currentCut.listRemove(current);
//					currentCut.listAdd(current.getChildren());
//				}
//			}
//
//		}
//		Iterator<Cut> cutItr = newCuts.iterator();
//		while(cutItr.hasNext()) {
//			Cut currentCut = cutItr.next();
//			currentCut.isBeneficial();
//			currentCut.updateScore();
//		}
//
//
//		activeCuts.addAll(newCuts);
//		activeCuts.remove(cut);
//
//
//	}


}
