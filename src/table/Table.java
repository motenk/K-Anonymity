package table;

import java.util.*;

public class Table {
	private ArrayList<Tuple> data;
	private int size;

	public Table(ArrayList<Tuple> list) {
		data = list;
		size = data.size();
	}

	public Table() {
		this(new ArrayList<Tuple>());
	}





	public ArrayList<Tuple> getData() {
		return data;
	}

	public void addTuple(Tuple t) {
		data.add(t);
	}

	//may be useful not sure.
	public ArrayList<Table> splitTable() {
		return null;
	}

	public Iterator<Tuple> dataIterator() {
		return data.iterator();
	}

	public ArrayList<Integer> getClassSet() {
	}
}