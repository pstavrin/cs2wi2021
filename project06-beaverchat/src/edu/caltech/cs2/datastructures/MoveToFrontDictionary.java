package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private int size;
    private Node<K, V> head;

    private static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> nextNode;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public MoveToFrontDictionary() {
        this.size = 0;
        this.head = null;
    }

    @Override
    public V remove(K key) {
        if (!this.keys().contains(key)) {
            return null;
        }
        Node<K, V> now = this.head;
        if (now == null) {
            return null;
        }
        else if (now.key.equals(key)) {
            V outValue = now.value;
            size--;
            if (this.size != 0) {
                this.head = this.head.nextNode;
                return outValue;
            }
            if (this.size == 0) {
                this.head = null;
            }
            return outValue;

        }
        else {
            while (now.nextNode != null) {
                if (now.nextNode.key.equals(key)) {
                    V out = now.nextNode.value;
                    size--;
                    now.nextNode = now.nextNode.nextNode;
                    return out;
                }
                now = now.nextNode;
            }
        }
        return null;

    }

    @Override
    public V put(K key, V value) {
        Node<K, V> now = this.head;
        while (now != null) {
            if (now.key.equals(key)) {
                V toReplace = now.value;
                now.value = value;
                return toReplace;
            }
            now = now.nextNode;
        }
        this.size++;
        now = new Node<>(key, value);
        now.nextNode = this.head;
        this.head = now;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        if (this.size == 0 || this.head == null) {
            return new ArrayDeque<>();
        }
        ICollection<K> keysOut = new ArrayDeque<>();
        Node<K, V> now = this.head;
        while (now != null) {
            keysOut.add(now.key);
            now = now.nextNode;
        }
        return keysOut;
    }

    @Override
    public ICollection<V> values() {
        if (this.size == 0 || this.head == null) {
            return new ArrayDeque<>();
        }
        ICollection<V> outValues = new ArrayDeque<>();
        Node<K, V> now = this.head;
        while (now != null) {
            outValues.add(now.value);
            now = now.nextNode;
        }
        return outValues;


    }

    public V get(K key) {
        if (this.size == 0 || this.head == null) {
            return null;
        }
        else if (this.head.key.equals(key)) {
            return this.head.value;
        }
        else {
            Node<K, V> now = this.head;
            Node<K, V> before = null;
            while (now != null) {
                if (now.key.equals(key)) {
                    if (before != null) {
                        before.nextNode = before.nextNode.nextNode;
                        now.nextNode = this.head;
                        this.head = now;
                    }
                    return now.value;
                }
                before = now;
                now = now.nextNode;

            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }
}
