package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TreeNode<T> implements Iterable<TreeNode<T>> {
    private T data;
    private List<TreeNode<T>> children;
    private TreeNode<T> parent;

    public TreeNode(T data) {
        setData(data);
        children = new ArrayList<>();
        parent = null;
    }

    public TreeNode(T data, Collection<T> children) {
        this(data);
        for (T child : children) {
            add(child);
        }
    }

    public void add(T child) {
        TreeNode<T> node = new TreeNode<>(child);
        node.parent = this;
        children.add(node);
    }

    public void addNode(TreeNode<T> node) {
        if (node != null) {
            node.parent = this;
            children.add(node);
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return children.iterator();
    }
}
