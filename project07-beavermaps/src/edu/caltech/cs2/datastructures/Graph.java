package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IGraph;
import edu.caltech.cs2.interfaces.ISet;

public class Graph<V, E> implements IGraph<V, E> {
    protected IDictionary<V, IDictionary<V, E>> vertices;

    public Graph() {
        this.vertices = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

    }

    @Override
    public boolean addVertex(V vertex) {
        if (this.vertices().contains(vertex)) {
            return false;
        }
        this.vertices.put(vertex, new ChainingHashDictionary<>(MoveToFrontDictionary::new));
        return true;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {
        if (!this.vertices().contains(src) || !this.vertices().contains(dest)) {
            throw new IllegalArgumentException();
        }
        if (!this.vertices.get(src).containsKey(dest)) {
            this.vertices.get(src).put(dest, e);
            return true;
        }
        E whatWeHave = this.vertices.get(src).get(dest);
        if (!whatWeHave.equals(e)) {
            this.vertices.get(src).put(dest, e);
        }
        return false;


    }

    @Override
    public boolean addUndirectedEdge(V n1, V n2, E e) {
        if (!this.vertices().contains(n1) || !this.vertices().contains(n2)) {
            throw new IllegalArgumentException();
        }
        E enOne = this.vertices.get(n1).get(n2);
        E enTwo = this.vertices.get(n2).get(n1);
        this.vertices.get(n1).put(n2, e);
        this.vertices.get(n2).put(n1, e);
        E testOne = this.vertices.get(n1).get(n2);
        E testTwo = this.vertices.get(n2).get(n1);
        if (enOne != null || enTwo != null) {
            return false;
        }
        return enOne == null || enTwo == null || !enOne.equals(testOne) || !enTwo.equals(testTwo);


    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if (!this.vertices().contains(src) || !this.vertices().contains(dest)) {
            throw new IllegalArgumentException();
        }
        if (!this.vertices.get(src).containsKey(dest)) {
            return false;
        }
        this.vertices.get(src).remove(dest);
        return true;
    }

    @Override
    public ISet<V> vertices() {
        return this.vertices.keySet();
    }

    @Override
    public E adjacent(V i, V j) {
        if (!this.vertices.containsKey(i) || !this.vertices.containsKey(j)) {
            throw new IllegalArgumentException();
        }
        return this.vertices.get(i).get(j);

    }

    @Override
    public ISet<V> neighbors(V vertex) {
        if (!this.vertices().contains(vertex)) {
            throw new IllegalArgumentException();
        }
        return this.vertices.get(vertex).keySet();
    }
}