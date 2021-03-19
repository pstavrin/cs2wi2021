package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IStack;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Iterator;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }


    @Override
    public boolean isPrefix(K key) {
        TrieNode<A, V> starNode = this.root;
        if (this.root == null) {
            return false;
        }
        for (A a : key) {
            if (!(starNode.pointers.containsKey(a))) {
                return false;
            }
            else {
                starNode = starNode.pointers.get(a);
            }
        }
        return true;
    }

    private void getCompletionsHelper(TrieNode<A, V> node, IDeque<V> completions) {
        if (node.value != null) {
            completions.add(node.value);
        }
        for (A a : node.pointers.keySet()) {
            getCompletionsHelper(node.pointers.get(a), completions);
        }

    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        if (!(isPrefix(prefix))) {
            return new ArrayDeque<>();
        }
        TrieNode<A, V> now = this.root;
        for (A a : prefix) {
            now = now.pointers.get(a);
        }
        IDeque<V> outCompletions = new ArrayDeque<>();
        getCompletionsHelper(now, outCompletions);
        return outCompletions;

    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;


    }

    @Override
    public V get(K key) {
        if (this.root == null) {
            return null;
        }
        TrieNode<A, V> starNode = this.root;
        for (A a : key) {
            if (!(starNode.pointers.containsKey(a))) {
                return null;
            }
            else {
                starNode = starNode.pointers.get(a);
            }
        }
        return starNode.value;
    }

    @Override
    public V remove(K key) {
        if (!(containsKey(key))) {
            return null;
        }
        TrieNode<A, V> now = this.root;
        if (now == null) {
            return null;
        }
        IStack<A> keyStorageUnit = new ArrayDeque<>();
        IStack<TrieNode<A, V>> storage = new ArrayDeque<>();
        storage.push(now);
        for (A a : key) {
            keyStorageUnit.push(a);
            now = now.pointers.get(a);
            storage.push(now);
        }
        V valueToRemove = now.value;
        now.value = null;
        while (storage.size() != 0) {
            TrieNode<A, V> next = storage.pop();
            if (next.value != null || !next.pointers.isEmpty()) {
                break;
            }
            if (next == this.root) {
                this.root = null;
            }
            else {
                TrieNode<A, V> parent = storage.peek();
                A removed = keyStorageUnit.pop();
                parent.pointers.remove(removed);
            }
        }
        this.size--;
        return valueToRemove;
    }

    @Override
    public V put(K key, V value) {
        if (this.root == null) {
            this.root = new TrieNode<>();
        }
        TrieNode<A, V> starNode = this.root;
        for (A a : key) {
            if (!starNode.pointers.containsKey(a)) {
                starNode.pointers.put(a, new TrieNode<>());
            }
            starNode = starNode.pointers.get(a);
        }
        if (!(starNode.value == null)) {
            V prevValue = starNode.value;
            starNode.value = value;
            return prevValue;
        }
        else {
            starNode.value = value;
            this.size++;
            return null;
        }
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }


    private void keysHelper(ICollection<K> keys, TrieNode<A, V> node, IDeque<A> acc) {
        if (node.value != null) {
            keys.add(this.collector.apply(acc));
        }
        if (!node.pointers.isEmpty()) {

            for (A a : node.pointers.keySet()) {
                acc.addBack(a);
                keysHelper(keys, node.pointers.get(a), acc);
                acc.removeBack();
            }
        }
    }

    @Override
    public ICollection<K> keys() {
        TrieNode<A, V> now = this.root;
        ICollection<K> outKeys = new ArrayDeque<>();
        IDeque<A> acc = new ArrayDeque<>();
        if (now == null) {
            return new ArrayDeque<>();
        }
        keysHelper(outKeys, now, acc);
        return outKeys;

    }

    private void valuesHelper(ICollection<V> values, TrieNode<A, V> node) {
        if (node.pointers != null) {
            if (node.value != null) {
                values.add(node.value);
            }
        }
        for (A a : node.pointers.keySet()) {
            valuesHelper(values, node.pointers.get(a));
        }
    }

    @Override
    public ICollection<V> values() {
        TrieNode<A, V> now = this.root;
        ICollection<V> outValues = new ArrayDeque<>();
        if (now == null) {
            return new ArrayDeque<>();
        }
        valuesHelper(outValues, now);
        return outValues;
    }

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }

    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}

