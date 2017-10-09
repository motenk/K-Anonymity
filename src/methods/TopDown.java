package methods;



import datastructure.TaxonomyIndexedPartitions;
import table.Table;
import taxonomy.TaxonomyTree;
import taxonomy.TaxonomyNode;

import java.util.*;

public class TopDown {
	//may just delete this and use KAnonMethods. Unsure as of yet.
	public void runAlgorithm(Table original, ArrayList<TaxonomyTree> trees) {
		TaxonomyIndexedPartitions tips = new TaxonomyIndexedPartitions(original, trees);
		//Initialize every value in T to the top most value.
		//Initialize set of cuts.
		//Array relates to the set of attributes
		//ArrayList is the set of active attribute values in the tree.
		//im thinking of storing the nodes in the table or some shit

		while(validCuts && beneficialCuts) {
			tips.performSpecialization(bestCut);
		}
	}

	public ArrayList<TaxonomyNode>[] initializeCuts(TaxonomyTree[] trees) {
		ArrayList<TaxonomyNode>[] cuts = new ArrayList[trees.length];
		for(int i = 0; i < trees.length; i++) {
			cuts[i] = new ArrayList<TaxonomyNode>();
			cuts[i].add(trees[i].getRoot());
		}
		return cuts;
	}
}