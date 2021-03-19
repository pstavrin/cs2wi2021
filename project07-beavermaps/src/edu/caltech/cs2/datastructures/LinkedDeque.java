package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {


    private static final class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> prev;

        Node(E data) {
            this.data = data;
        }
    }

    private Node<E> head;
    private Node<E> end;
    private int size;

    public LinkedDeque() {
        this.head = null;
        this.end = null;
        this.size = 0;
    }

    @Override
    public void addFront(E e) {
        this.size++;
        if (this.size == 1) {
            this.head = new Node<>(e);
            this.end = this.head;
        }
        else {
            Node<E> front = new Node<>(e);
            front.prev = null;
            front.next = this.head;
            this.head.prev = front;
            this.head = front;
        }
    }

    @Override
    public void addBack(E e) {
        this.size++;
        if (this.size == 1) {
            this.end = new Node<>(e);
            this.head = this.end;
        }
        else {
            Node<E> back = new Node<>(e);
            back.prev = this.end;
            back.next = null;
            this.end.next = back;
            this.end = back;
        }
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        }
        E out = this.head.data;
        if (this.size == 1) {
            this.head = null;
            this.end = null;
        }
        else {
            this.head = this.head.next;
            this.head.prev = null;
        }
        this.size--;
        return out;


    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        E out = this.end.data;
        if (this.size == 1) {
            this.head = null;
            this.end = null;
        }
        else {
            this.end = this.end.prev;
            this.end.next = null;
        }
        this.size--;
        return out;
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
        if (this.head == null) {
            return null;
        }
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
        return this.head.data;
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.end.data;
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> iter = new Iterator<E>() {
            private Node<E> tempo = LinkedDeque.this.head;
            @Override
            public boolean hasNext() {
                if (LinkedDeque.this.size == 0) {
                    return false;
                }
                return this.tempo != null;
            }

            @Override
            public E next() {
                if (LinkedDeque.this.size == 0 || this.tempo == null) {
                    return null;
                }
                E out = this.tempo.data;
                this.tempo = this.tempo.next;
                return out;
            }
        };
        return iter;
    }

    @Override
    public int size() {
        if (this.head == null) {
            return 0;
        }
        return this.size;
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        String out = "";
        Node<E> tempo = this.head;
        for (int i = 0; i < this.size - 1; i++) {
            out += tempo.data + ", ";
            tempo = tempo.next;
        }
        return "[" + out + this.end.data + "]";
    }
}


