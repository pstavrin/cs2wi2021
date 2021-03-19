package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    private void percolateUp(int index) {
        if (index > 0) {
            int parent = (index - 1) / 4;
            while (this.data[index].priority < this.data[parent].priority) {
                PQElement<E> whatTheParentHas = this.data[parent];
                this.data[parent] = this.data[index];
                this.data[index] = whatTheParentHas;
                this.keyToIndexMap.put(this.data[index].data, index);
                this.keyToIndexMap.put(this.data[parent].data, parent);
                index = parent;
                parent = (index - 1) / 4;
            }
        }
    }

    private void percolateDown(int index) {
        if (index >= this.size) {
            return;
        }
        PQElement<E> bestBefore = null;
        int id = 4 * index  + 1;
        int bestIndex = index;
        if (id >= this.size) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (id + i < this.size) {
                if (this.data[id + i].priority < this.data[bestIndex].priority) {
                    bestIndex = id + i;
                }
            }
        }
        if (bestIndex == index) {
            return;
        }
        bestBefore = this.data[bestIndex];
        this.data[bestIndex] = this.data[index];
        this.data[index] = bestBefore;
        this.keyToIndexMap.put(this.data[index].data, index);
        this.keyToIndexMap.put(this.data[bestIndex].data, bestIndex);
        percolateDown(bestIndex);
    }

    @Override
    public void increaseKey(PQElement<E> key) {
        if (!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException();
        }
        int nowIdx = this.keyToIndexMap.get(key.data);
        this.keyToIndexMap.remove(key.data);
        this.data[nowIdx] = key;
        if (nowIdx < this.size) {
            this.data[nowIdx] = key;
            this.keyToIndexMap.put(key.data, nowIdx);
            percolateDown(nowIdx);
        }
    }

    @Override
    public void decreaseKey(PQElement<E> key) {
        if (!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException();
        }
        Integer nowIdx = this.keyToIndexMap.get(key.data);
        if (nowIdx != null) {
            PQElement<E> before = this.data[nowIdx];
            if (key.priority < before.priority) {
                this.data[nowIdx] = key;
                percolateUp(nowIdx);
            }
        }
    }

    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        if (this.keyToIndexMap.containsKey(epqElement.data)) {
            throw new IllegalArgumentException();
        }
        if (this.size >= this.data.length) {
            PQElement<E>[] databox = new PQElement[this.data.length * 2];
            System.arraycopy(this.data, 0, databox, 0, this.data.length);
            this.data = databox;
        }
        this.data[this.size] = epqElement;
        this.keyToIndexMap.put(epqElement.data, this.size);
        percolateUp(this.size);
        this.size++;
        return this.data[this.size - 1] != null;
    }

    @Override
    public PQElement<E> dequeue() {
        if (this.size == 0) {
            throw new IllegalArgumentException();
        }
        if (this.size == 1) {
            PQElement<E> outE = this.data[0];
            this.keyToIndexMap.remove(this.data[0].data);
            this.data[0] = null;
            this.size = 0;
            return outE;
        }
        this.keyToIndexMap.remove(this.data[0].data);
        PQElement<E> outElement = this.data[0];
        this.data[0] = this.data[this.size - 1];
        this.keyToIndexMap.put(this.data[0].data, 0);
        this.data[this.size - 1] = null;
        this.size--;
        percolateDown(0);
        return outElement;
    }

    @Override
    public PQElement<E> peek() {
        return this.data[0];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<PQElement<E>> iterator() {
        throw new UnsupportedOperationException();
    }
}