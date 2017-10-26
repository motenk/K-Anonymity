package methods;



import datastructure.Cut;
import datastructure.TaxonomyIndexedPartitions;
import table.Table;
import taxonomy.TaxonomyTree;
import taxonomy.TaxonomyNode;

import java.util.*;
import table.Tuple;

public class TopDown {

	TaxonomyIndexedPartitions tips;
	int kValue;
	long startTime;

	static boolean[] isNumerical;

	public TopDown(ArrayList<Tuple> originalTable, int kValue, ArrayList<TaxonomyTree> trees) {
		isNumerical = new boolean[originalTable.get(0).size()];
		for(int i = 0; i < originalTable.get(0).size() - 1; i++) {
			try {
				Integer.parseInt(originalTable.get(0).get(i));
				isNumerical[i] = true;
			}
			catch (NumberFormatException e) {
				isNumerical[i] = false;
			}
		}
		ArrayList<Tuple> modTable = new ArrayList<>();
		Iterator<Tuple> itr = originalTable.iterator();
		while(itr.hasNext()) {
			modTable.add(itr.next().convertToTimTuple());
		}
		startTime = System.currentTimeMillis();
		this.kValue = kValue;
		//initializes cuts.
		ArrayList<TaxonomyTree> tmpList = new ArrayList<>();
		for(int i = 0; i < trees.size() - 1; i++) {
			tmpList.add(trees.get(i));
		}
		tips = new TaxonomyIndexedPartitions(modTable, tmpList, isNumerical, false);
	}



	public long topDownAlgorithm() {
		boolean verbose = true;
		int j =0;
		while(tips.checkValidityAndBenefical(kValue, verbose)) {
			if(verbose) {
				System.out.println("------------------" + j++ + "------------------");
				tips.print();
			}
			//prints the active cuts.

			Cut bestCut = tips.getBestCut(verbose);
			System.out.println(bestCut.leafList.get(0).tuple.toString());


			tips.performCut(bestCut, isNumerical);
			//System.out.println("cut performed");

		}
		//System.out.println("Algorithm done");
		return System.currentTimeMillis() - startTime;


	}

	//not completed.
	public ArrayList<Tuple> getResults() {
		ArrayList<Tuple> table = tips.getPrivateTable();
		return table;
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
			list.add(column1[i]);
			list.add(column2[i]);
			list.add(column3[i]);
			list.add(classes[i]);
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