package methods;



import datastructure.Cut;
import datastructure.TaxonomyIndexedPartitions;
import table.Table;
import table.Tuple;
import taxonomy.TaxonomyNode;
import taxonomy.TaxonomyTree;
import util.Performance;

import java.util.ArrayList;
import java.util.Iterator;

public class TopDown {

	TaxonomyIndexedPartitions tips;
	int kValue;
	long startTime;
	//originalTable
	private ArrayList<Tuple> data;
	private int numberOfColumns;
	private int[][] widths;
	private ArrayList<ArrayList<Integer>> attributeRanges;
	private ArrayList<TaxonomyTree> attributeTrees;

	static boolean[] isNumerical;

	public TopDown(ArrayList<Tuple> originalTable, int kValue, ArrayList<TaxonomyTree> trees) {
		attributeTrees = trees;
		data = originalTable;
		numberOfColumns = data.get(0).size();
		widths = new int[numberOfColumns][2];
		attributeRanges = new ArrayList<ArrayList<Integer>>();

		isNumerical = new boolean[numberOfColumns];
		for(int i = 0; i < numberOfColumns - 1; i++) {
			try {
				Integer.parseInt(originalTable.get(0).get(i));
				isNumerical[i] = true;
			}
			catch (NumberFormatException e) {
				isNumerical[i] = false;
			}
		}
		//The middlevalue of the sensitive data.
		double middleVal = calcMiddleVal(originalTable);
		System.out.println(middleVal);
		ArrayList<Tuple> modTable = new ArrayList<>();
		Iterator<Tuple> itr = originalTable.iterator();
		while(itr.hasNext()) {
			modTable.add(itr.next().convertToTimTuple(middleVal));
		}
		startTime = System.currentTimeMillis();
		this.kValue = kValue;
		//initializes cuts.
		ArrayList<TaxonomyTree> tmpList = new ArrayList<>();
		for(int i = 0; i < trees.size() - 1; i++) {
			tmpList.add(trees.get(i));
		}
		tips = new TaxonomyIndexedPartitions(modTable, tmpList, isNumerical, false);
		setRangesAndWidths();
	}



	public Performance topDownAlgorithm() {
		Performance perf = new Performance();
		boolean verbose = false;
		int j =0;
		sad:
		{
			while (tips.checkValidityAndBenefical(kValue, verbose)) {
				if (verbose) {
					System.out.println("------------------" + j++ + "------------------");
					tips.print();
				}
				//prints the active cuts.

				Cut bestCut = tips.getBestCut(verbose);

				if (bestCut == null) break sad;
				tips.performCut(bestCut, isNumerical);
				//System.out.println("cut performed");

			}
		}
		//System.out.println("Algorithm done");
		System.out.println("average k: " + tips.getAverageK());
		perf.setRuntime(System.currentTimeMillis() - startTime);
		perf.setMeasuredK(tips.getActualK());

		double ncp = 0.0;
		double r_ncp = 0.0;
		for (int i = 0; i < numberOfColumns; i++) {
			r_ncp += getNormalisedWidth(i);
		}
		r_ncp *= data.size();
		ncp += r_ncp;
//		ncp /= numberOfColumns;
//		ncp /= data.size();
//		ncp *= 100;
		perf.setNcp(ncp);

		return perf;
	}

	//not completed.
	public ArrayList<Tuple> getResults() {
		ArrayList<Tuple> table = tips.getPrivateTable();
		return table;
	}

	public double generateAverageClassValue() {
		return tips.getAverageK();
	}

	public double calcMiddleVal(ArrayList<Tuple> t) {
		int classIndex = t.get(0).size() - 1;
		Iterator<Tuple> itr = t.iterator();
		double total = 0;
		double count = 0;
		while(itr.hasNext()) {
			total += Double.parseDouble(itr.next().get(classIndex));
			count++;
		}
		return (total / count);
	}

	//Preconditon: 	BasicMondrian object valid initialisation
	//Postcondtion:	Ranges of numeric attributes and number of nodes of categorical attributes calculated and stored
	//				Widths (range indices of smallest and largest values - numeric/number of nodes - categorical) calculated and stored
	//Status:		Coded and efficient
	//Written by:	Chris
	private void setRangesAndWidths(){
		int maximumValue = 0;
		int minimumValue = Integer.MAX_VALUE;
		for (int i = 0; i < numberOfColumns; i++) {
			attributeRanges.add(new ArrayList<Integer>());
			if(isNumerical[i]){
				for (int j = 0; j < data.size(); j++) {
					int tempPotentialMax = Integer.parseInt(data.get(j).get(i));
					if(tempPotentialMax > maximumValue)
						maximumValue = tempPotentialMax;
					if(tempPotentialMax < minimumValue)
						minimumValue = tempPotentialMax;
				}
				for (int j = minimumValue; j <= maximumValue; j++) {
					attributeRanges.get(i).add(j);
				}
				widths[i][0] = 0;
				widths[i][1] = attributeRanges.get(i).size()-1;
			}
			else{
				maximumValue = countLeafNodes(i);
				attributeRanges.get(i).add(maximumValue);
				widths[i][0] = maximumValue;
			}
		}
	}
	//Preconditon: 	Attribute trees initialised
	//Postcondtion:	Number of leaf nodes returned
	//Status:		Coded and efficient
	//Written by:	Chris
	private int countLeafNodes(int index){
		TaxonomyNode root = attributeTrees.get(index).getRoot();
		return countLeafNodesRecursive(root);
	}

	//Preconditon: 	Attribute trees initialised
	//Postcondtion:	Number of leaf nodes calculated and returned
	//Status:		Coded and efficient
	//Written by:	Chris
	private int countLeafNodesRecursive(TaxonomyNode root){
		if(root.childrenIterator() == null){
			return 1;
		}
		else{
			int num = 0;
			Iterator<TaxonomyNode> iterator = root.childrenIterator();
			while(iterator.hasNext()){
				num += countLeafNodesRecursive(iterator.next());
			}
			return num;
		}
	}

	//Preconditon: 	attributeRanges calculated, isCategorical calculated, widths calculated, valid partition parameter, valid index
	//Postcondtion:	Return normalised width of passed in partition on dimension passed in
	//Status:		Coded and efficient
	//Written by:	Chris
	private double getNormalisedWidth(int index){
		double width;
		if(isNumerical[index]){
			int lowBoundIndex = widths[index][0];
			int highBoundIndex = widths[index][1];
			width = attributeRanges.get(index).get(highBoundIndex) - attributeRanges.get(index).get(lowBoundIndex);
			return width / (attributeRanges.get(index).get(attributeRanges.get(index).size()-1) - attributeRanges.get(index).get(0));
		}
		else{
			width = widths[index][0];
			return width / (attributeRanges.get(index).get(attributeRanges.get(index).size()-1));
		}
	}



	public static void main(String[] args) {
		System.out.println("TEST");
		String[] column1 = {"employ", "employ", "unemploy", "unemploy", "employ", "unemploy"};
		String[] column2 = {"a", "a", "a", "b", "b", "b"};
		String[] column3 = {"1", "1", "2", "1", "2", "2"};
		String[] classes = {"<=50K", "<=50K", ">50K", ">50K", "<=50K", "<=50K"};
		String[][] data = new String[4][6];
		data[0] = column1;
		data[1] = column2;
		data[2] = column3;
		data[3] = classes;

		ArrayList<Tuple> list1 = new ArrayList<Tuple>();
		for(int i = 0; i < 6; i++) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(column1[i].trim());
			list.add(column2[i].trim());
			list.add(column3[i].trim());
			list.add(classes[i].trim());
			list1.add(new Tuple(list, i, true));
		}
		Table t = new Table(list1);
		ArrayList<TaxonomyTree> treeList = new ArrayList<TaxonomyTree> ();
		for(int i = 0; i < 3; i++) {
			TaxonomyTree _t = new TaxonomyTree();
			_t.root = new TaxonomyNode("*");

			TaxonomyNode t1 = new TaxonomyNode( data[i][0], _t.root);
			TaxonomyNode t2 = new TaxonomyNode( data[i][5], _t.root);
			_t.root.children = new ArrayList<TaxonomyNode>();
			_t.root.children.add(t1);
			_t.root.children.add(t2);
			treeList.add(_t);
		}
		TopDown td = new TopDown(list1, 2, treeList);
		td.topDownAlgorithm();
		ArrayList<Tuple> table = td.getResults();
	}
}
