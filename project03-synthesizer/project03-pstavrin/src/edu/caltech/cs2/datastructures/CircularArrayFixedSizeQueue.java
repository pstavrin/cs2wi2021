package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Iterator;

public class CircularArrayFixedSizeQueue<E> implements IFixedSizeQueue<E> {
    private int startIdx;
    private E[] data;
    private int size;

    public CircularArrayFixedSizeQueue(int capacity) {
        this.data = (E[]) new Object[capacity];
        this.startIdx = 0;
        this.size = 0;
    }

    private int lastIndexGetter() {
        int lastIdx = (this.startIdx + this.size) % this.data.length;
        return lastIdx;
    }


    @Override
    public boolean isFull() {
        if (this.size() >= this.data.length) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int capacity() {
        return this.data.length;
    }

    @Override
    public boolean enqueue(E e) {
        if (isFull()) {
            return false;
        }
        else if (this.size == 0) {
            this.startIdx = 0;

        }
        this.data[lastIndexGetter()] = e;
        if (this.data[lastIndexGetter()] == e) {
            this.size++;
            return true;
        }
        return false;
    }


    @Override
    public E dequeue() {
        if (this.size == 0) {
            return null;
        }
        if (this.startIdx == lastIndexGetter()) {
            this.startIdx++;
            this.size--;
            return this.data[lastIndexGetter()];
        }
        else if (this.startIdx < this.data.length) {
            E out = this.data[this.startIdx];
            this.startIdx++;
            this.size--;
            return out;
        }
        else {
            this.startIdx = 0;
            E out = this.data[this.startIdx];
            this.size--;
            this.startIdx++;
            return out;
        }
    }

    @Override
    public E peek() {
        if (this.size() == 0) {
            return null;
        }
        else if (this.startIdx >= this.data.length) {
            return this.data[0];
        }
        return this.data[this.startIdx];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> iter = new Iterator<E>() {

            private int currIdx = CircularArrayFixedSizeQueue.this.startIdx;
            private int elements = CircularArrayFixedSizeQueue.this.size;

            @Override
            public boolean hasNext() {
                if (this.elements == 0) {
                    return false;
                }
                return true;
            }

            @Override
            public E next() {
                this.elements--;
                if (this.currIdx >= CircularArrayFixedSizeQueue.this.data.length) {
                    E out = CircularArrayFixedSizeQueue.this.data[this.currIdx - CircularArrayFixedSizeQueue.this.data.length];
                    this.currIdx++;
                    return out;
                }
                return CircularArrayFixedSizeQueue.this.data[this.currIdx++];
            }
        };
        return iter;
    }

    @Override
    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        String out = "[";
        Iterator<E> iter = this.iterator();
        while (iter.hasNext()) {
            E next = iter.next();
            out += next + ", ";
        }
        out = out.substring(0, out.length() - 2);
        return out + "]";
    }
}
