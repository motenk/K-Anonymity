package methods;

import table.Table;
import table.Tuple;
import taxonomy.TaxonomyTree;
import taxonomy.TaxonomyNode;
import java.util.*;


public class Mondrian{
	private int numberOfColumns;
	private boolean[] isCategorical;
	private int k;
	private ArrayList<Tuple> data;
	private int[][] widths;
	private ArrayList<Partition> result;
	private ArrayList<ArrayList<Integer>> attributeRanges;
	private ArrayList<TaxonomyTree> attributeTrees;

	//constructor
	//data - the actual data - linkedlist of tuples
	//k value - minimum equivalence class number
	//numberOfColumns - number of attributes well be anonymising
	//isCatagorical - boolean array, one boolean for each column, whether or not it is a categorical column
	//widths - has a high and low index for the width of each attribute - needs an array of values all values in order...
	public Mondrian(Table input, int k, ArrayList<TaxonomyTree> attributeTrees){
		data = input.getData();
		this.k = k;
		numberOfColumns = data.get(0).getSize();
		isCategorical = new boolean[numberOfColumns];
		widths = new int[numberOfColumns][2];
		this.attributeTrees = attributeTrees;
		setRanges();
		setWidths();
	}

	private void setRangesAndWidths(){
		int maximumValue = 0;
		for (int i = 0; i < numberOfColumns; i++) {
			attributeRanges.add(new ArrayList<Integer>());
			//if(notCatagorical){
				for (int j = 0; j < data.size(); j++) {
					int tempPotentialMax = Integer.parseInt(data.get(j).get(i));
					if(tempPotentialMax > maximumValue)
						maximumValue = tempPotentialMax;
				}
				for (int j = 0; j < maximumValue; j++) {
					attributeRanges.get(i).add(j);
				}
				widths[i][0] = 0;
				widths[i][1] = maximumValue;
			//}
			//else{
				// maximumValue = recursive function to get number of leaf nodes
				// attributeRanges.get(i).add(maximumValue)
				// widths[i][0] = maximumValue;
			//}
		}
	}

	private void countLeafNodes(){
		
	}

	//
	private double getNormalisedWidth(Partition partition, int index){
		double width;
		if(!isCategorical[index]){
			int lowBoundIndex = partition.getWidths()[index][0];
			int highBoundIndex = partition.getWidths()[index][1];
			// width = highest number of the partition's range - the lowest number of the patition's range
		}
		else{
			width = partition.getWidths()[index][0];
		}
		// return width / range of the whole attribute - not just the partition;
	}

	private int chooseDimesion(Partition partition){
		double maxWidth = -1;
		int maxDim = -1;
		for (int i = 0; i < numberOfColumns; i++) {
			if(partition.getSplittable()[i] != 1)
				continue;
			double normalWidth = getNormalisedWidth(partition, i);
			if(normalWidth > maxWidth){
				maxWidth = normalWidth;
				maxDim = i;
			}
		}
		return maxDim;
	}

	private HashMap<String, Integer> getFrequencySet(Partition partition, int dimension){
		HashMap<String, Integer> frequency = new HashMap<String, Integer>();
		for (Tuple t : partition.getData()) {
			if(frequency.containsKey(t.get(dimension)))
				frequency.put(t.get(dimension), frequency.get(t.get(dimension))+1);
			else
				frequency.put(t.get(dimension), 1);
		}
		return frequency;
	}

	private Dunno findMedian(Partition partition, int dimension){

	}

	private void splitNumericalValue(){

	}

	private ArrayList<Partition> splitNumerical(){

	}

	private ArrayList<Partition> splitCatagorical(){

	}

	private ArrayList<Partition> splitPartition(Partition partition, int dimension){
		int[][] partitionWidth = partition.getWidths();
		ArrayList<String> partitionMiddle = partition.getGeneralisationResults();
		if(!isCategorical[dimension])
			return splitNumerical(partition, dimension, partitionWidth, partitionMiddle);
		else
			return splitCatagorical(partition, dimension, partitionWidth, partitionMiddle);
	}

	private boolean checkSplittable(Partition partition){
		int sumOfSplittableArray = IntStream.of(partition.getSplittable).sum();
		return (sumOfSplittableArray == 0) ? false : true;
	}

	private void anonymise(Partition partition){
		if(!checkSplittable(partition)){
			result.add(partition);
		}
		else{
			int dimension = chooseDimesion();
			ArrayList<Partition> subPartitions = splitPartition(partition, dimension);
			if(subPartitions.size() == 0){
				partition.getSplittable()[dimension] = 0;
				anonymise(partition);
			}
			else{
				for (Partition p : subPartitions) {
					anonymise(p);
				}
			}
		}
	}

	public Table mondrianAlgorithm(Table table){

	}
	
	private class Partition{
		private int[][] widths;
		private ArrayList<Tuple> data;
		private ArrayList<String> currentGeneralisation;
		private int[] splittable;
		private Partition(ArrayList<Tuple> data, int[][] widths, ArrayList<String> currentGeneralisation, int numberOfColumns){
			this.widths = widths;
			this.data = data;
			this.currentGeneralisation = currentGeneralisation;
			splittable = new int[numberOfColumns];
			Arrays.fill(splittable, 1);
		}

		private int length(){
			return data.size();
		}

		private int[][] getWidths(){
			return widths;
		}

		private ArrayList<Tuple> getData(){
			return data;
		}

		private ArrayList<String> getCurrentGeneralisation(){
			return currentGeneralisation;
		}

		private int[] getSplittable()
		{
			return splittable;
		}
		
		private boolean[] getSplittable(){
			return splittable;
		}
	}
}