package table;

import java.util.*;

public class Table {
	private ArrayList<Tuple> data;

	public Table(ArrayList<Tuple> list) {
		data = list;
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


	public Iterator<Tuple> dataIterator() {
		return data.iterator();
	}


	public int size() { return data.size();}
}