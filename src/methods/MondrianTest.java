/********************************************************************************************************************************************
 * main Class for 																													*
 * Programmer: Christopher O'Donnell|3165328																								*
 * Date Completed: 																															*
 * 																																			*
 * This class is responsible for 
 *																																			*
 ********************************************************************************************************************************************/
package methods;

import java.util.*;
import taxonomy.TaxonomyTree;
import methods.BasicMondrian;
import table.Tuple;
public class MondrianTest{
	public static void main(String args[]){
		TaxonomyTree tree = new TaxonomyTree("*", "number,letter");
		tree.addNode("number", "one,two,three");
		tree.addNode("letter", "a,b,c");
		TaxonomyTree tree2 = new TaxonomyTree();
		ArrayList<TaxonomyTree> trees = new ArrayList<>();
		trees.add(tree2);
		trees.add(tree);
		ArrayList<String> data1 = new ArrayList<>();
		data1.add("12");
		data1.add("a");
		ArrayList<String> data2 = new ArrayList<>();
		data2.add("345");
		data2.add("b");
		ArrayList<String> data3 = new ArrayList<>();
		data3.add("21");
		data3.add("c");
		ArrayList<String> data4 = new ArrayList<>();
		data4.add("256");
		data4.add("c");
		ArrayList<String> data5 = new ArrayList<>();
		data5.add("83");
		data5.add("c");
		ArrayList<String> data6 = new ArrayList<>();
		data6.add("58");
		data6.add("a");
		ArrayList<String> data7 = new ArrayList<>();
		data7.add("183");
		data7.add("b");
		ArrayList<String> data8 = new ArrayList<>();
		data8.add("368");
		data8.add("b");
		Tuple tup1 = new Tuple(data1, 1);
		Tuple tup2 = new Tuple(data2, 1);
		Tuple tup3 = new Tuple(data3, 1);
		Tuple tup4 = new Tuple(data4, 1);
		Tuple tup5 = new Tuple(data5, 1);
		Tuple tup6 = new Tuple(data6, 1);
		Tuple tup7 = new Tuple(data7, 1);
		Tuple tup8 = new Tuple(data8, 1);
		ArrayList<Tuple> data = new ArrayList<>();
		data.add(tup1);
		data.add(tup2);
		data.add(tup3);
		data.add(tup4);
		data.add(tup5);
		data.add(tup6);
		data.add(tup7);
		data.add(tup8);
		BasicMondrian bm = new BasicMondrian(data, 2, trees);
		bm.mondrianAlgorithm();
		System.out.println(bm.getResults());
	}
}