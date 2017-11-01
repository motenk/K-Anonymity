/*
BasicMondrian Class for COMP4240
Programmer: Chris O'Donnell, Moten Kingsmill
Date Completed: 25/10/17

This class implements Mondrian Multi-Dimensional K-Anonymity as proposed by LeFevre et. al in 2006
It takes an ArrayList of Tuples, a k value, and an ArrayList of TaxonomyTrees as the constructor.
It performs a series of calculations on the data to determine and apply the most appropriate specialisation.
Algorithm:
Generalise to maximum (categories = *, numeric = [full range of column])
Anonymise(partition)
	if no allowable partition cut
		return partition - specialised
	else
		choose "widest" dimension
		get frequency set of that dimension
		find the median value of that frequency set
		split the partition into left and right sides using the median and specialise as it goes
		return Anonymise(left) U Anonymise(right)
*/

package methods;

import table.Table;
import table.Tuple;
import taxonomy.TaxonomyTree;
import taxonomy.TaxonomyNode;
import java.util.*;
import java.util.stream.IntStream;

import javax.management.monitor.StringMonitor;

import sun.util.resources.cldr.ss.CalendarData_ss_SZ;

public class BasicMondrian{
	//widths int array defines the indices of the smalled and largest numbers in the range of a partition
	//attributeRanges has an arraylist for each attribute storing the complete range of the attribute (numeric)
	//or the number of nodes of the attribute tree(categorical)
	//attribute trees stores trees for categorical attributes and nulls for numeric
	//the reason for this is making indexing trees easy using column number indices
	private int numberOfColumns;
	private boolean[] isCategorical;
	private int k;
	private ArrayList<Tuple> data;
	private int[][] widths;
	private ArrayList<Partition> result;
	private ArrayList<ArrayList<Integer>> attributeRanges;
	private ArrayList<TaxonomyTree> attributeTrees;
	private ArrayList<Tuple> outputResults;
	private	double ncp = 0.0;


	//constructor
	//data - the actual data - linkedlist of tuples
	//k value - minimum equivalence class number
	//numberOfColumns - number of attributes we'll be anonymising
	//isCatagorical - boolean array, one boolean for each column, whether or not it is a categorical column
	//Preconditon: 	Valid array list of tuples input, valid k value, value arraylist of attribute trees (trees for cat. attr., nulls for numerics)
	//Postcondtion:	BasicMondrian object initialised
	//Status:		Coded and efficient
	//Written by:	Chris
	public BasicMondrian(ArrayList<Tuple> input, int k, ArrayList<TaxonomyTree> attributeTrees){
		data = input;
		this.k = k;
		numberOfColumns = data.get(0).size()-1;
		isCategorical = new boolean[numberOfColumns];
		widths = new int[numberOfColumns][2];
		this.attributeTrees = attributeTrees;
		outputResults = new ArrayList<Tuple>();
		attributeRanges = new ArrayList<ArrayList<Integer>>();
		result = new ArrayList<Partition>();
		setCategoricalArray();
		setRangesAndWidths();
	}

	public BasicMondrian(ArrayList<Tuple> input, int k, ArrayList<TaxonomyTree> attributeTrees, int qid){
		data = input;
		this.k = k;
		if(qid > (data.get(0).size()-1) || qid < 0)
			numberOfColumns = data.get(0).size()-1;
		else
			numberOfColumns = qid;
		isCategorical = new boolean[numberOfColumns];
		widths = new int[numberOfColumns][2];
		this.attributeTrees = attributeTrees;
		outputResults = new ArrayList<Tuple>();
		attributeRanges = new ArrayList<ArrayList<Integer>>();
		result = new ArrayList<Partition>();
		setCategoricalArray();
		setRangesAndWidths();
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
			if(!isCategorical[i]){
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

	//Preconditon: 	Data initialised, isCatagorical initialised
	//Postcondtion:	Boolean array of whether or not each column of data is categorical calculated and stored
	//Status:		Coded and efficient
	//Written by:	Chris
	private void setCategoricalArray(){
		for (int i = 0; i < numberOfColumns; i++) {
			String s = data.get(0).get(i);
			for (int j = 0; j < s.length(); j++) {
				//basically "if any character in this string is not a digit between 0 and 9"
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

	//Preconditon: 	TaxonomyTrees Initialised, valid index
	//Postcondtion:	Node matching value returned
	//Status:		Coded and Efficient
	//Written by:	Moten
	private TaxonomyNode findNodeForValue(String value, int dimension)
    {
        return attributeTrees.get(dimension).getNode(value);
	}

	//Preconditon: 	attributeRanges calculated, isCategorical calculated, widths calculated, valid partition parameter, valid index
	//Postcondtion:	Return normalised width of passed in partition on dimension passed in
	//Status:		Coded and efficient
	//Written by:	Chris
	private double getNormalisedWidth(Partition partition, int index){
		double width;
		if(!isCategorical[index]){
			int lowBoundIndex = partition.getWidths()[index][0];
			int highBoundIndex = partition.getWidths()[index][1];
			width = attributeRanges.get(index).get(highBoundIndex) - attributeRanges.get(index).get(lowBoundIndex);
			return width / (attributeRanges.get(index).get(attributeRanges.get(index).size()-1) - attributeRanges.get(index).get(0));
		}
		else{
			width = partition.getWidths()[index][0];
			return width / (attributeRanges.get(index).get(attributeRanges.get(index).size()-1));
		}
	}

	//Preconditon: 	numberOfColumns calc'd, valid partition parameter
	//Postcondtion:	Widest partition dimension returned
	//Status:		Coded and efficient
	//Written by:	Chris
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

	//Preconditon: 	valid partition parameter, valid dimension
	//Postcondtion:	HashMap of value frequencies returned
	//Status:		Coded and efficient
	//Written by:	Chris
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

	//Preconditon: 	valid partition parameter, valid dimension
	//Postcondtion:	Partition median value, median +1 value, lowest, and highest values all set
	//Status:		Coded and efficient
	//Written by:	Chris
	private void findMedian(Partition partition, int dimension){
		HashMap<String, Integer> frequency = getFrequencySet(partition, dimension);
		String splitValue = "";
		ArrayList<String> valueList = new ArrayList<String>(frequency.keySet());
		valueList.sort(Comparator.comparing(Integer::parseInt));
		int total = 0;
		for (Integer i : frequency.values()) {
			total += i;
		}
		int middle = total/2;
		if(middle < k || valueList.size() <= 1){
			partition.setMedian("", "", valueList.get(0), valueList.get(valueList.size()-1), dimension);
			return;
		}

		int index = 0;
		int splitIndex = 0;
		boolean foundSplit = false;
		String nextValue = "";
		for (int i = 0; i < valueList.size(); i++) {
			index += frequency.get(valueList.get(i));
			if(index > k && total - index > k){
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
		partition.setMedian(splitValue, nextValue, valueList.get(0), valueList.get(valueList.size()-1), dimension);
		return;
	}

	//Preconditon: 	value is of format "[number] - [number]", valid dimension
	//Postcondtion:	numerical value is divided into two separate ranges around the median value and stored in either left or right arraylists
	//Status:		Coded and efficient
	//Written by:	Chris
	private void splitNumericalValue(String value, String splitValue, ArrayList<String> leftMiddle, ArrayList<String> rightMiddle, int dimension){
		String[] valuesArray = value.split(" - ");
		if(valuesArray.length <= 1){
			leftMiddle.set(dimension, valuesArray[0]);
			rightMiddle.set(dimension, valuesArray[0]);
		}
		else{
			String low = valuesArray[0];
			String high = valuesArray[1];
			if(low.equals(splitValue)){
				leftMiddle.set(dimension, low);
			}
			else{
				leftMiddle.set(dimension, low + " - " + splitValue);
			}
			if(high.equals(splitValue)){
				rightMiddle.set(dimension, high);
			}
			else{
				rightMiddle.set(dimension, splitValue + " - " + high);
			}
		}
	}

	//Preconditon: 	valid partition in, valid dimension, attributeRanges calc'd, numberOfColumns calc'd
	//Postcondtion:	ArrayList of specialised subpartitions returned
	//Status:		Coded and efficient
	//Written by:	Chris
	private ArrayList<Partition> splitNumerical(Partition partition, int dimension, int[][] partitionWidth, ArrayList<String> partitionMiddle){
		ArrayList<Partition> subPartitions = new ArrayList<Partition>();
		findMedian(partition, dimension);
		String splitValue = partition.getSplitValue(dimension);
		String nextValue = partition.getNextValue(dimension);
		String lowValue = partition.getLowestValue(dimension);
		String highValue = partition.getHighestValue(dimension);
		int partitionLow = attributeRanges.get(dimension).indexOf(Integer.parseInt(lowValue));
		int partitionHigh = attributeRanges.get(dimension).indexOf(Integer.parseInt(highValue));
		if(lowValue.equals(highValue)){
			partitionMiddle.set(dimension, lowValue);
		}
		else{
			partitionMiddle.set(dimension, lowValue + " - " + highValue);
		}
		partitionWidth[dimension][0] = partitionLow;
		partitionWidth[dimension][1] = partitionHigh;
		if(splitValue.equals("") || splitValue.equals(nextValue)){
			return new ArrayList<Partition>();
		}
		int middlePosition = attributeRanges.get(dimension).indexOf(Integer.parseInt(splitValue));
		ArrayList<String> leftMiddle = new ArrayList<>(partitionMiddle);
		ArrayList<String> rightMiddle = new ArrayList<>(partitionMiddle);
		splitNumericalValue(partitionMiddle.get(dimension), splitValue, leftMiddle, rightMiddle, dimension);
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

	//Preconditon: 	valid partition in, valid dimension, attributeTrees initialised, numberOfColumns calc'd
	//Postcondtion:	ArrayList of specialised subpartitions returned
	//Status:		Coded and efficient
	//Written by:	Chris
	private ArrayList<Partition> splitCategorical(Partition partition, int dimension, int[][] partitionWidth, ArrayList<String> partitionMiddle){
		ArrayList<Partition> subPartitions = new ArrayList<Partition>();
		TaxonomyNode splitValue = findNodeForValue(partitionMiddle.get(dimension), dimension);
		ArrayList<TaxonomyNode> subNodes = new ArrayList<TaxonomyNode>();
		Iterator<TaxonomyNode> splitIterator;
		if(splitValue == null || splitValue.childrenIterator() == null)
			return new ArrayList<Partition>();
		else
			splitIterator = splitValue.childrenIterator();
		int numberOfChildren = 0;
		while(splitIterator.hasNext()){
			subNodes.add(splitIterator.next());
			numberOfChildren++;
		}
		ArrayList<ArrayList<Tuple>> subGroups = new ArrayList<ArrayList<Tuple>>();
		for (int i = 0; i < numberOfChildren; i++) {
			subGroups.add(new ArrayList<Tuple>());
		}
		if(subNodes.size() == 0){
			return new ArrayList<Partition>();
		}
		for(Tuple t : partition.getData()){
			String qidValue = t.get(dimension).trim().toLowerCase();
			int specialiseIndex = splitValue.specialize(qidValue, false);
			if(specialiseIndex == -1){
				System.out.println("Generalisation tree error.");
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

	//Preconditon: 	valid partition in, valid dimension, isCatagorical calc'd
	//Postcondtion:	ArrayList of specialised subpartitions returned
	//Status:		Coded and efficient
	//Written by:	Chris
	private ArrayList<Partition> splitPartition(Partition partition, int dimension){
		int[][] partitionWidth = partition.getWidths();
		ArrayList<String> partitionMiddle = partition.getCurrentGeneralisation();
		if(!isCategorical[dimension])
			return splitNumerical(partition, dimension, partitionWidth, partitionMiddle);
		else
			return splitCategorical(partition, dimension, partitionWidth, partitionMiddle);
	}

	//Preconditon: 	valid partition in
	//Postcondtion:	Whether or not partition is splittable determined and returned
	//Status:		Coded and efficient
	//Written by:	Chris
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

	//Preconditon: 	BasicMondrian object validly initialised
	//Postcondtion:	ArrayList of generalised tuples set
	//Status:		Coded and efficient
	//Written by:	Chris
	public void mondrianAlgorithm(){
		ArrayList<String> middleTemp = new ArrayList<>();
		for (int i = 0; i < numberOfColumns; i++) {
			if(!isCategorical[i]){
				middleTemp.add(attributeRanges.get(i).get(0) + " - " + attributeRanges.get(i).get(attributeRanges.get(i).size()-1));
			}
			else{
				middleTemp.add("*");
			}
		}
		Partition wholePartition = new Partition(data, widths, middleTemp, numberOfColumns);
		System.out.println("Starting anonymisation");
		long startTime = System.currentTimeMillis();
		anonymise(wholePartition);
		long runtime = System.currentTimeMillis() - startTime;
		System.out.println("Anonymisation finished, runtime: " + runtime + "ms");
		for(Partition p : result){
			double r_ncp = 0.0;
			for (int i = 0; i < numberOfColumns; i++) {
				r_ncp += getNormalisedWidth(p, i);
			 }
			ArrayList<String> temp = p.getCurrentGeneralisation();
			for (int i = 0; i < p.length(); i++) {
				ArrayList<String> pTemp = new ArrayList<>(temp);
				for(int j = numberOfColumns; j < data.get(0).size(); j++){
					pTemp.add(p.getData().get(i).get(j));
				}
				outputResults.add(new Tuple(pTemp, p.getData().get(i).getID()));
			}
			r_ncp *= p.length();
			ncp += r_ncp;
		}
		HashMap<String, Integer> classes = new HashMap<String, Integer>();
		for(Tuple tuple : outputResults){
			String temp = "";
			for(int i = 0; i < numberOfColumns; i++){
				temp += tuple.get(i);
			}
			if(classes.containsKey(temp))
					classes.put(temp, classes.get(temp)+1);
			else
				classes.put(temp, 1);
		}
		double index = 0;
		int minNumber = Integer.MAX_VALUE;
		for(String s : classes.keySet()){
			index += classes.get(s);
			if(classes.get(s) < minNumber)
				minNumber = classes.get(s);
		}
		
		System.out.println("Average class size: " + index/classes.size());
		System.out.println("Actual K value: " + minNumber);
		ncp /= numberOfColumns;
		ncp /= data.size();
		ncp *= 100;
	}

	//Preconditon: 	Mondrian algorithm has run, ncp calc'd
	//Postcondtion:	return ncp value
	//Status:		Coded and efficient
	//Written by:	Chris
	public double getNcp(){
		return ncp;
	}

	//Preconditon: 	mondrian algorithm has run
	//Postcondtion:	ArrayList of generalised tuples returned
	//Status:		Coded and efficient
	//Written by:	Chris
	public ArrayList<Tuple> getResults(){
		return outputResults;
	}
	
	//this private inner class is reponsible for storing the intermediate partitions required by this algorithm
	//it keeps track of the raw data assigned to it, the width of that data per column, the current generalisation of that data per column
	//an array of whether each column can be further split, this partition's split and split + 1 value and its highest and lowest values.
	private class Partition{
		private int[][] widths;
		private ArrayList<Tuple> data;
		private ArrayList<String> currentGeneralisation;
		private int[] splittable;
		private String[] splitValue = new String[numberOfColumns];
		private String[] nextValue = new String[numberOfColumns];
		private String[] lowestValue = new String[numberOfColumns];
		private String[] highestValue = new String[numberOfColumns];
		//Preconditon: 	valid data, widths, generalisation, numberofcols passed in
		//Postcondtion:	Valid partition object initialised
		//Status:		Coded and efficient
		//Written by:	Chris
		private Partition(ArrayList<Tuple> data, int[][] widths, ArrayList<String> currentGeneralisation, int numberOfColumns){
			this.widths = widths;
			this.data = data;
			this.currentGeneralisation = currentGeneralisation;
			splittable = new int[numberOfColumns];
			Arrays.fill(splittable, 1);
		}

		//Preconditon: 	partition initialised
		//Postcondtion:	number of tuples in data returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private int length(){
			return data.size();
		}

		//Preconditon: 	partition initialised
		//Postcondtion:	widths array returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private int[][] getWidths(){
			return widths;
		}

		//Preconditon: 	partition initialised
		//Postcondtion:	partition data returned (arraylist of tuples)
		//Status:		Coded and efficient
		//Written by:	Chris
		private ArrayList<Tuple> getData(){
			return data;
		}

		//Preconditon: 	partition initialised
		//Postcondtion:	partition generalisations returned (arraylist of strings)
		//Status:		Coded and efficient
		//Written by:	Chris
		private ArrayList<String> getCurrentGeneralisation(){
			return currentGeneralisation;
		}

		//Preconditon: 	partition initialised
		//Postcondtion:	int array splittable returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private int[] getSplittable(){
			return splittable;
		}

		//Preconditon: 	partition initialised
		//Postcondtion:	partition's split, next, lowest and highest values set
		//Status:		Coded and efficient
		//Written by:	Chris
		private void setMedian(String splitValue, String nextValue, String lowestValue, String highestValue, int dimension){
			this.splitValue[dimension] = splitValue;
			this.nextValue[dimension] = nextValue;
			this.lowestValue[dimension] = lowestValue;
			this.highestValue[dimension] = highestValue;
		}

		//Preconditon: 	setMedian has been called
		//Postcondtion:	splitValue string returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private String getSplitValue(int dimension){
			return splitValue[dimension];
		}

		//Preconditon: 	setMedian has been called
		//Postcondtion:	nextValue string returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private String getNextValue(int dimension){
			return nextValue[dimension];
		}

		//Preconditon: 	setMedian has been called
		//Postcondtion:	lowestValue string returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private String getLowestValue(int dimension){
			return lowestValue[dimension];
		}

		//Preconditon: 	setMedian has been called
		//Postcondtion:	highestValue string returned
		//Status:		Coded and efficient
		//Written by:	Chris
		private String getHighestValue(int dimension){
			return highestValue[dimension];
		}
	}
}