package datastructure;

import table.Tuple;
import taxonomy.TaxonomyNode;

import java.util.ArrayList;
import java.util.Iterator;

public class Node {
    Node parent;
    Node[] children;
    ArrayList<Node[]> tmpChildren;
    //ArrayList<Tuple> data;
    ArrayList<Tuple> subTable;
    ArrayList<Cut> validCuts;

    //array contains the frequency per class (ie the sensitive data)
    int[] frequency;
    int classCount = 2;

    //the tuple that represents the entire set.
    public Tuple tuple;




    public Node(ArrayList<Tuple> t, Tuple tuple) {
        parent = null;
        children = null;
        subTable = t;
        frequency = new int[classCount];
        Iterator<Tuple> itr = t.iterator();
        while(itr.hasNext()) {
            frequency[itr.next().getClassID()]++;
        }
        tmpChildren = new ArrayList<>();
        this.tuple = tuple;
    }

    public Node(Node parent, Tuple tuple) {
        this.parent = parent;
        children = null;
        subTable = new ArrayList<>();
        frequency = new int[classCount];
        this.tuple = tuple;
    }

    public Node[] setChildren(int index) {
        children = tmpChildren.get(index);
        return children;
    }



    public void dataAdd(Tuple t) {
        subTable.add(t);
        frequency[t.getClassID()]++;
    }


    //Precondition:		Children initialized and the same size as the children of cut.taxNode
    //Postcondition:	Splits the data between the parent and the appropriate children based on the specialization
    //Status:			Requires implementation of record knowing what specialization it belongs to
    //Written by:		Tim
    public void splitRecordsBetweenChildren(TaxonomyNode taxNode, int attribute, boolean isNumeric, Node[] children) {
        Iterator<Tuple> itr = subTable.iterator();
        while(itr.hasNext()) {
            Tuple current = itr.next();
            int generalized = taxNode.specialize(current.get(attribute), isNumeric);
            children[generalized].dataAdd(current);
        }

    }






    public int count() {
        return subTable.size();
    }

    public int[] frequency() {
        return frequency;
    }

    public void generateTmpChildren(boolean[] isNumeric) {
        tmpChildren = new ArrayList<>();
        Iterator<Cut> itr = validCuts.iterator();
        while(itr.hasNext()) {
            Cut current = itr.next();

            Node[] tmp;
            if(current.t.children == null || current.t.children.size() == 0) {
                tmp = null;
            }
            else {
                tmp = new Node[current.t.children.size()];
                for (int i = 0; i < current.t.children.size(); i++) {
                    ArrayList<String> list = new ArrayList<>();
                    for(int j = 0; j < tuple.size(); j++) {
                        if(j == current.attribute) {
                            list.add(current.t.children.get(i).name);
                        }
                        else {
                            list.add(tuple.get(j));
                        }
                    }
                    tmp[i] = new Node(this, new Tuple(list, -1));

                    current.children[i].leafList.add(tmp[i]);
                }
                splitRecordsBetweenChildren(current.t, current.attribute, isNumeric[current.attribute], tmp);
            }

            tmpChildren.add(tmp);
        }
    }

    public void setValidCuts(ArrayList<Cut> validCuts) {
        this.validCuts = new ArrayList<>();
        for(int i = 0; i < validCuts.size(); i++) {
            this.validCuts.add(validCuts.get(i));
        }
    }
}
