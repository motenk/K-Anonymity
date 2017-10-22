package methods;

import table.Table;
import table.Tuple;
import taxonomy.TaxonomyTree;
import taxonomy.TaxonomyNode;
import java.util.*;
import java.util.stream.IntStream;


public class BasicMondrian{
	private int numberOfColumns;
	private boolean[] isCategorical;
	private int k;
	private ArrayList<Tuple> data;
	private int[][] widths;
	private ArrayList<Partition> result;
	private ArrayList<ArrayList<Integer>> attributeRanges;
	private ArrayList<TaxonomyTree> attributeTrees;
	private ArrayList<Tuples> outputResults;
	private	double ncp = 0.0;

	//constructor
	//data - the actual data - linkedlist of tuples
	//k value - minimum equivalence class number
	//numberOfColumns - number of attributes well be anonymising
	//isCatagorical - boolean array, one boolean for each column, whether or not it is a categorical column
	//widths - has a high and low index for the width of each attribute - needs an array of values all values in order...
	public BasicMondrian(Table input, int k, ArrayList<TaxonomyTree> attributeTrees){
		data = input.getData();
		this.k = k;
		numberOfColumns = data.get(0).getSize();
		isCategorical = new boolean[numberOfColumns];
		widths = new int[numberOfColumns][2];
		this.attributeTrees = attributeTrees;
		outputResults = new ArrayList<Tuple>();
		setRangesAndWidths();
		setCategoricalArray();
	}

	//
	private void setRangesAndWidths(){
		int maximumValue = 0;
		int minimumValue = Integer.MAX_VALUE;
		for (int i = 0; i < numberOfColumns; i++) {
			attributeRanges.add(new ArrayList<Integer>());
			if(!isCategorical[i]){
				for (int j = 0; j < data.size(); j++) {
					int tempPotentialMax = Integer.parseInt(data.get(j).get(i));
					if(tempPotentialMax > maximumValue)
						maximumValue = tempPotentialMax;
					if(tempPotentialMax < minimumValue)
						minimumValue = tempPotentialMax;
				}
				for (int j = minimumValue; j < maximumValue; j++) {
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

	private void setCategoricalArray(){
		for (int i = 0; i < numberOfColumns; i++) {
			String s = data.get(0).get[i];
			for (int j = 0; j < s.length(); j++) {
				if(Character.digit(s.charAt(j),10) < 0){
					isCategorical[i] = true;
					break;
				}
				else{
					isCategorical[i] = false;
				}
			}
		}
	}

	private int countLeafNodes(int index){
		TaxonomyNode root = attributeTrees.get(index).getRoot();
		return countLeafNodesRecursive(root);
	}

	private int countLeafNodesRecursive(TaxonomyNode root){
		if(root.getChildren() == null){
			return 1;
		}
		else{
			int num = 0;
			Iterator<TaxonomyNode> iterator = root.childIterator();
			while(iterator.hasNext()){
				num += countLeafNodesRecursive(iterator.next());
			}
			return num;
		}
	}

	//Preconditon: 	TaxonomyTrees Initialised
	//Postcondtion:	Node matching value returned
	//Status:		Coded and Efficient
	//Written by:	Moten
	private TaxonomyNode findNodeForValue(String value, int dimension)
    {
        return attributeTrees.get(dimension).getNode(value);
	}

	//
	private double getNormalisedWidth(Partition partition, int index){
		double width;
		if(!isCategorical[index]){
			int lowBoundIndex = partition.getWidths()[index][0];
			int highBoundIndex = partition.getWidths()[index][1];
			width = attributeRanges.get(index).get(highBoundIndex) - attributeRanges.get(index).get(lowBoundIndex);
		}
		else{
			width = partition.getWidths()[index][0];
		}
		return width / attributeRanges.get(index).get(attributeRanges.size()-1);
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

	private void findMedian(Partition partition, int dimension){
		HashMap<String, Integer> frequency = getFrequencySet(partition, dimension);
		String splitValue = "";
		ArrayList<String> valueList = new ArrayList<String>(frequency.keySet());
		Collections.sort(valueList);
		int total = 0;
		for (Integer i : frequency.values()) {
			total += i;
		}
		int middle = total/2;
		if(middle < k || valueList.size() <= 1){
			partition.setMedian("", "", valueList.get(0), valueList.get(valueList.size()-1));
			return;
		}

		int index = 0;
		int splitIndex = 0;
		boolean foundSplit = false;
		String nextValue = "";
		for (int i = 0; i < valueList.size(); i++) {
			index += frequency.get(valueList.get(i));
			if(index >= middle){
				splitValue = valueList.get(i);
				splitIndex = i;
				foundSplit = true;
				break;
			}
		}
		if(!foundSplit){
			System.out.println("Can't find split value...");
		}
		if(splitIndex != valueList.size()-1)
			nextValue = valueList.get(splitIndex+1);
		else{
			nextValue = valueList.get(splitIndex);
		}
		partition.setMedian(splitValue, nextValue, valueList.get(0), valueList.get(valueList.size()-1));
		return;
	}

	private void splitNumericalValue(String value, String splitValue, ArrayList<String> leftMiddle, ArrayList<String> rightMiddle){
		String[] valuesArray = value.split(" - ");
		if(valuesArray.length <= 1){
			leftMiddle.add(valuesArray[0]);
			rightMiddle.add(valuesArray[0]);
		}
		else{
			String low = valuesArray[0];
			String high = valuesArray[1];
			if(low.equals(splitValue)){
				leftMiddle.add(low);
			}
			else{
				leftMiddle.add(low + " - " + splitValue);
			}
			if(high.equals(splitValue)){
				rightMiddle.add(high);
			}
			else{
				rightMiddle.add(splitValue + " - " + high);
			}
		}

	}

	private ArrayList<Partition> splitNumerical(Partition partition, int dimension, int[][] partitionWidth, ArrayList<String> partitionMiddle){
		ArrayList<Partition> subPartitions = new ArrayList<Partition>();
		findMedian(partition, dimension);
		String splitValue = partition.getSplitValue();
		String nextValue = partition.getNextValue();
		String lowValue = partition.getLowestValue();
		String highValue = partition.getHighestValue();
		int partitionLow = attributeRanges.get(dimension).indexOf(Integer.parseInt(lowValue));
		int partitionHigh = attributeRanges.get(dimension).indexOf(Integer.parseInt(highValue));
		if(lowValue.equals(highValue)){
			partitionMiddle.set(dimension, lowValue);
		}
		else{
			partitionMiddle.set(dimension, lowValue + " - " + highValue);
		}
		partitionWidth[dimension][0] = partitionLow;
		partitionWidth[dimension][0] = partitionHigh;
		if(splitValue.equals("") || splitValue.equals(nextValue)){
			return new ArrayList<Partition>();
		}
		int middlePosition = attributeRanges.get(dimension).indexOf(Integer.parseInt(splitValue));
		ArrayList<String> leftMiddle = new ArrayList<>(partitionMiddle);
		ArrayList<String> rightMiddle = new ArrayList<>(partitionMiddle);
		splitNumericalValue(partitionMiddle.get(dimension), splitValue, leftMiddle, rightMiddle);
		ArrayList<Tuple> lhs = new ArrayList<Tuple>();
		ArrayList<Tuple> rhs = new ArrayList<Tuple>();
		for(Tuple t : partition.getData()){
			int pos = attributeRanges.get(dimension).indexOf(Integer.parseInt(t.get(dimension)));
			if(pos <= middlePosition){
				lhs.add(t);
			}
			else{
				rhs.add(t);
			}
		}
		int[][] leftWidths = new int[numberOfColumns][];
		int[][] rightWidths = new int[numberOfColumns][];
		for (int i = 0; i < numberOfColumns; i++) {
			leftWidths[i] = partitionWidth[i].clone();
			rightWidths[i] = partitionWidth[i].clone();
		}
		leftWidths[dimension][0] = partitionWidth[dimension][0];
		leftWidths[dimension][1] = middlePosition;
		rightWidths[dimension][0] = attributeRanges.get(dimension).indexOf(Integer.parseInt(nextValue));
		rightWidths[dimension][1] = partitionWidth[dimension][1];
		subPartitions.add(new Partition(lhs, leftWidths, leftMiddle, numberOfColumns));
		subPartitions.add(new Partition(rhs, rightWidths, rightMiddle, numberOfColumns));
		return subPartitions;
	}

	private ArrayList<Partition> splitCategorical(Partition partition, int dimension, int[][] partitionWidth, ArrayList<String> partitionMiddle){
		ArrayList<Partition> subPartitions = new ArrayList<Partition>();
		TaxonomyNode splitValue = findNodeForValue(partitionMiddle.get(dimension));
		ArrayList<TaxonomyNode> subNodes = new ArrayList<TaxonomyNode>();
		Iterator<TaxonomyNode> splitIterator = splitValue.childIterator();
		while(splitIterator.hasNext()){
			subNodes.add(splitIterator.next());
		}
		ArrayList<ArrayList<Tuple>> subGroups = new ArrayList<ArrayList<Tuple>>();
		if(subNodes.size() == 0){
			return new ArrayList<Partition>();
		}
		for(Tuple t : partition.getData()){
			String qidValue = t.get(dimension);
			int specialiseIndex = splitValue.specialize(qidValue);
			if(specialiseIndex == -1){
				System.out.println("Generalisation tree error.");
				continue;
			}
			if(specialiseIndex == 0){
				continue;
			}
			subGroups.get(specialiseIndex).add(t);
		}
		boolean flag = true;
		for (int i = 0; i < subGroups.size(); i++) {
			if(subGroups.get(i).size() == 0){
				continue;
			}
			if(subGroups.get(i).size() < k){
				flag = false;
				break;
			}
		}
		if(flag){
			for (int i = 0; i < subGroups.size(); i++) {
				if(subGroups.get(i).size() == 0){
					continue;
				}
				ArrayList<String> middleTemp = new ArrayList<>(partitionMiddle);
				int[][] widthsTemp = new int[numberOfColumns][];
				for (int j = 0; j < numberOfColumns; j++) {
					widthsTemp[j] = partitionWidth[j].clone();
				}
				middleTemp.set(dimension, subNodes.get(i).getName());
				widthsTemp[dimension][0] = countLeafNodesRecursive(subNodes.get(i));
				subPartitions.add(new Partition(subGroups.get(i), widthsTemp, middleTemp, numberOfColumns));
			}
		}
		return subPartitions;
	}

	private ArrayList<Partition> splitPartition(Partition partition, int dimension){
		int[][] partitionWidth = partition.getWidths();
		ArrayList<String> partitionMiddle = partition.getCurrentGeneralisation();
		if(!isCategorical[dimension])
			return splitNumerical(partition, dimension, partitionWidth, partitionMiddle);
		else
			return splitCategorical(partition, dimension, partitionWidth, partitionMiddle);
	}

	private boolean checkSplittable(Partition partition){
		int sumOfSplittableArray = IntStream.of(partition.getSplittable()).sum();
		return (sumOfSplittableArray == 0) ? false : true;
	}

	private void anonymise(Partition partition){
		if(!checkSplittable(partition)){
			result.add(partition);
		}
		else{
			int dimension = chooseDimesion(partition);
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

	public void mondrianAlgorithm(){
		ArrayList<String> middleTemp = new ArrayList<>();
		for (int i = 0; i < numberOfColumns; i++) {
			if(!isCategorical[i]){
				middleTemp.add(attributeRanges.get(i).get(0) + " - " + attributeRanges.get(i).get(attributeRanges.get(i).size()-1));
			}
			else{
				middleTemp.add(attributeRanges.get(i).get(0));
			}
		}
		Partition wholePartition = new Partition(data, widths, middleTemp, numberOfColumns);
		System.out.println("Starting anonymisation");
		long startTime = System.currentTimeMillis();
		anonymise(wholePartition);
		long runtime = System.currentTimeMillis() - startTime;
		System.out.println("Anonymisation finished, runtime: " + runtime);
		for(Partition p : result){
			double r_ncp = 0.0;
			for (int i = 0; i < numberOfColumns; i++) {
				r_ncp += getNormalisedWidth(p, i);
			}
			ArrayList<String> temp = p.getCurrentGeneralisation();
			for (int i = 0; i < p.size(); i++) {
				ArrayList<String> pTemp = new ArrayList<>(temp);
				//I honestly have no idea why they add the piece of data in the last place in each partitions tuple to the results...
				pTemp.add(p.getData().get(i).get(numberOfColumns-1));
				//not sure what to do for id...
				outputResults.add(new Tuple(pTemp, 0));
			}
			r_ncp *= p.length();
			ncp += r_ncp;
		}
		ncp /= numberOfColumns;
		ncp /= data.size();
		ncp *= 100;
	}

	public double getNcp(){
		return ncp;
	}

	public ArrayList<Tuple> getResults(){
		return outputResults;
	}
	
	private class Partition{
		private int[][] widths;
		private ArrayList<Tuple> data;
		private ArrayList<String> currentGeneralisation;
		private int[] splittable;
		private String splitValue;
		private String nextValue;
		private String lowestValue;
		private String highestValue;
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

		private int[] getSplittable(){
			return splittable;
		}

		private void setMedian(String splitValue, String nextValue, String lowestValue, String highestValue){
			this.splitValue = splitValue;
			this.nextValue = nextValue;
			this.lowestValue = lowestValue;
			this.highestValue = highestValue;
		}

		private String getSplitValue(){
			return splitValue;
		}

		private String getNextValue(){
			return nextValue;
		}

		private String getLowestValue(){
			return lowestValue;
		}

		private String getHighestValue(){
			return highestValue;
		}
	}
}