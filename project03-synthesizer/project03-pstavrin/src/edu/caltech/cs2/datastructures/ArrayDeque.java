package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private int size;
    private static final int defaultCap = 10;
    private static final int increaseSizeBy = 2;
    private E[] data;


    public ArrayDeque() {
        this(defaultCap);
    }

    public ArrayDeque(int initialCapacity) {
        this.data = (E[]) new Object[initialCapacity];
    }

    @Override
    public void addFront(E e) {
        while (this.data.length < this.size + 1) {
            E[] tempData = (E[]) new Object[(int)(this.data.length * increaseSizeBy)];
            for (int i = 0; i < this.size; i++) {
                tempData[i] = this.data[i];
            }
            this.data = tempData;
        }
        this.size++;
        for (int j = this.size - 1; j >= 1; j--) {
            this.data[j] = this.data[j - 1];
        }
        this.data[0] = e;

    }

    @Override
    public void addBack(E e) {
        while (this.data.length < this.size + 1) {
            E[] tempData = (E[]) new Object[(int)(this.data.length * increaseSizeBy)];
            System.arraycopy(this.data, 0, tempData, 0, this.size);
            this.data = tempData;
        }
        this.size++;
        this.data[this.size - 1] = e;

    }

    @Override
    public E removeFront() {
        if (this.size == 0){
            return null;
        }
        E desireToRemove = this.data[0];
        for (int i = 0; i < this.size - 1; i++) {
            this.data[i] = this.data[i + 1];
        }
        this.size--;
        return desireToRemove;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        this.size--;
        return this.data[this.size];
    }

    @Override
    public boolean enqueue(E e) {
        int startingSize = this.size;
        addFront(e);
        if (this.size - 1 == startingSize) {
            return true;
        }
        return false;

    }

    @Override
    public E dequeue() {
        return removeBack();
    }

    @Override
    public boolean push(E e) {
        int startingSize = this.size;
        addBack(e);
        if (this.size - 1 == startingSize) {
            return true;
        }
        return false;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        return this.data[0];
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.data[this.size - 1];
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> iter = new Iterator<E>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < ArrayDeque.this.size;
            }

            @Override
            public E next() {
                return ArrayDeque.this.data[currentIndex++];
            }
        };
        return iter;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        String out = "";
        for (int idx = 0; idx < this.size - 1; idx++) {
            out += this.data[idx] + ", ";
        }
        return "[" + out + this.data[this.size - 1] + "]";
    }
}

