package datastructure;

import taxonomy.TaxonomyNode;

import java.util.ArrayList;
import java.util.Iterator;

public class Cut extends TaxonomyNode {
    public ArrayList<Node> leafList;
    public TaxonomyNode t;

    public int count;
    public int[] frequency;
    public Cut[] children;

    public double score, infoGain, anonyLoss;

    public boolean beneficial, valid;

    int classCount = 2;
    int attribute;


    public Cut(TaxonomyNode rootTaxonomyNode, Node rootNode, int attribute) {
        t = rootTaxonomyNode;
        leafList = new ArrayList<>();
        leafList.add(rootNode);
        if(rootTaxonomyNode.children != null) {
            children = new Cut[rootTaxonomyNode.children.size()];
        }
        frequency = new int[classCount];
        this.attribute = attribute;
        beneficial = false;
        valid = false;
    }

    public Cut(TaxonomyNode rootTaxonomyNode, int attribute) {
        t = rootTaxonomyNode;
        leafList = new ArrayList<>();
        if(rootTaxonomyNode.children != null) {
            children = new Cut[rootTaxonomyNode.children.size()];
        }
        frequency = new int[classCount];
        this.attribute = attribute;
        beneficial = false;
        valid = false;
    }

    public void setList(ArrayList<Node> list) {
        leafList = list;
    }

    //returns true if it is a beneficial cut ie the frequency
    public boolean getCountStats() {
        Iterator<Node> itr = leafList.iterator();
        count = 0;
        for(int i = 0; i < classCount; i++) {
            frequency[i] = 0;
        }
        while(itr.hasNext()) {
            Node current = itr.next();
            count += current.count();
            int[] currentFreq = current.frequency();
            for(int i = 0; i < frequency.length; i++) {
                frequency[i] += currentFreq[i];
            }
        }
        int emptyFrequencyCount = 0;
        for(int i = 0; i < classCount; i++) {
            if(frequency[i] == 0) {
                emptyFrequencyCount++;
            }
        }
        if(emptyFrequencyCount >= classCount - 1) {
            beneficial = false;
        }
        else {
            beneficial = true;
        }

        return  beneficial;
    }

    public double initialiseScore(int attribute) {
        infoGain = entropy(frequency, count);
            for(int i = 0; i < children.length; i++) {
            infoGain -= ((double) children[i].count / (double) count) * entropy(children[i].frequency, children[i].count);
        }

        anonyLoss = 0;
        int count = 0;
        Iterator<Node> itr = leafList.iterator();
        while(itr.hasNext()) {
            Node current = itr.next();
            anonyLoss += current.count();
            System.out.println("X: " + current.tuple.toString());
            if(current.tmpChildren == null) {
                System.out.println(leafList.size());
                System.out.println(itr.next().tuple.toString());
                System.out.println(current.tmpChildren.size());
            }


            Node[] currentChildren = current.tmpChildren.get(attribute);
            int smallest = Integer.MAX_VALUE;
            for(int i = 0; i < currentChildren.length; i++) {
                if(currentChildren[i].count() < smallest) {
                    smallest = currentChildren[i].count();
                }
            }
            anonyLoss -= smallest;
            count++;
        }
        anonyLoss /= count;

        if(anonyLoss == 0) {
            score = infoGain;
        }
        else {
            score = infoGain / anonyLoss;
        }
        valid = !(t.children == null || t.children.size() == 0);
        return score;
    }

    private double entropy(int[] classFrequency, double setSize) {
        double entropy = 0;
        for(int i = 0; i < classFrequency.length; i++) {
            if(classFrequency[i] != 0) {
                double val = ((double) classFrequency[i] / setSize);
                entropy -= val * (Math.log(val) / Math.log(2));
            }
        }
        return entropy;
    }

    public double updateScore(int index) {
        Iterator<Node> itr = leafList.iterator();
        int count = 0;
        while(itr.hasNext()) {
            Node current = itr.next();
            anonyLoss += current.count();
            Node[] currentChildren = current.tmpChildren.get(index);
            int smallest = Integer.MAX_VALUE;
            for(int i = 0; i < currentChildren.length; i++) {
                if(currentChildren[i].count() < smallest) {
                    smallest = currentChildren[i].count();
                }
            }
            anonyLoss -= smallest;
            count++;
        }
        anonyLoss /= count;

        if(anonyLoss == 0) {
            score = infoGain;
        }
        else {
            score = infoGain / anonyLoss;
        }
        return score;
    }

    public boolean validCheck(int kValue) {
        for(int i = 0; i < children.length; i++) {
            Iterator<Node> itr = children[i].leafList.iterator();
            while (itr.hasNext()) {

                Node current = itr.next();
                int count = current.count();
                if(count < kValue && count != 0) {
                    valid = false;
                    return valid;
                }
            }
        }
        valid = true;
        return true;
    }

    public void generateChildren() {
        if(t.children == null) {
            children = null;
        }
        else {
            children = new Cut[t.children.size()];
            for (int i = 0; i < t.children.size(); i++) {
                children[i] = new Cut(t.children.get(i), attribute);
            }
        }
    }
}
