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
		trees.add(null);
		trees.add(1, tree);
		ArrayList<String> data1 = new ArrayList<>();
		data1.add("12");
		data1.add("a");
		data1.add("15");
		ArrayList<String> data2 = new ArrayList<>();
		data2.add("345");
		data2.add("b");
		data2.add("17");
		ArrayList<String> data3 = new ArrayList<>();
		data3.add("21");
		data3.add("c");
		data3.add("12");
		ArrayList<String> data4 = new ArrayList<>();
		data4.add("256");
		data4.add("one");
		data4.add("43");
		ArrayList<String> data5 = new ArrayList<>();
		data5.add("83");
		data5.add("two");
		data5.add("12");
		ArrayList<String> data6 = new ArrayList<>();
		data6.add("58");
		data6.add("three");
		data6.add("51");
		ArrayList<String> data7 = new ArrayList<>();
		data7.add("183");
		data7.add("b");
		data7.add("23");
		ArrayList<String> data8 = new ArrayList<>();
		data8.add("368");
		data8.add("two");
		data8.add("12");
		Tuple tup1 = new Tuple(data1, 1);
		Tuple tup2 = new Tuple(data2, 2);
		Tuple tup3 = new Tuple(data3, 3);
		Tuple tup4 = new Tuple(data4, 4);
		Tuple tup5 = new Tuple(data5, 5);
		Tuple tup6 = new Tuple(data6, 6);
		Tuple tup7 = new Tuple(data7, 7);
		Tuple tup8 = new Tuple(data8, 8);
		ArrayList<Tuple> data = new ArrayList<>();
		data.add(tup1);
		data.add(tup2);
		data.add(tup3);
		data.add(tup4);
		data.add(tup5);
		data.add(tup6);
		data.add(tup7);
		data.add(tup8);
		for(Tuple t : data){
			System.out.println(t + " " + t.getID());
		}
		BasicMondrian bm = new BasicMondrian(data, 2, trees);
		bm.mondrianAlgorithm();
		for(Tuple t : bm.getResults()){
			System.out.println(t + " " + t.getID());
		}
		System.out.println(bm.getNcp());
	}
}