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
	int[] classes;

	public TopDown(ArrayList originalTable, int kValue, ArrayList<TaxonomyTree> trees) {
		this.kValue = kValue;
		//initializes cuts.
		tips = new TaxonomyIndexedPartitions(originalTable, trees, classes, true);
	}



	public void topDownAlgorithm() {
		int j =0;
		while(tips.checkValidityAndBeneifical(kValue, true)) {

			System.out.println("------------------" + j++ + "------------------");
			//prints the active cuts.
			tips.print();
			Cut bestCut = tips.getBestCut(true);


			tips.performCut(bestCut);
			System.out.println("cut performed");

		}
		System.out.println("Algorithm done");



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