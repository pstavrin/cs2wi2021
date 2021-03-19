package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Iterator;
import java.util.function.Supplier;


public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private int size;
    private IDictionary<K, V>[] backing;
    private final int[] primes = new int[]{11, 29, 43, 103, 223, 503, 1019, 2039, 4111, 8311, 16741, 32999, 64223, 109903, 127597, 244301, 399989};
    private int sizeIndex;
    private int index;


    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        this.size = 0;
        this.sizeIndex = 0;
        this.backing = new IDictionary[primes[this.sizeIndex]];
        this.index = 0;

    }

    private int hashing(K key) {
        int out = key.hashCode() % backing.length;
        if (out < 0) {
            return out * -1;
        }
        return out;
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int hashIndx = hashing(key);
        if (this.size == 0 || this.backing[hashIndx] == null) {
            return null;
        }
        else {
            return this.backing[hashIndx].get(key);
        }
    }

    @Override
    public V remove(K key) {
        int hashIndx = hashing(key);
        if (this.size == 0 || this.backing[hashIndx] == null) {
            return null;
        }
        else {
            V outReturn = this.backing[hashIndx].remove(key);
            if (outReturn != null) {
                size--;
            }
            return outReturn;
        }
    }

    private void rehash() {
        // new backing array size --> next prime
        // copy everything in the new array
        IDictionary<K, V>[] whatWeHaveNow = this.backing;
        int s = this.sizeIndex++;
        if (s >= primes.length) {
            this.backing = new IDictionary[backing.length * 2 + 1];
        }
        else {
            this.backing = new IDictionary[primes[s]];
        }
        this.size = 0;
        for (IDictionary<K, V> buck : whatWeHaveNow) {
            if (buck != null) {
                Iterator<K> keys = buck.keys().iterator();
                Iterator<V> values = buck.values().iterator();
                while (keys.hasNext()) {
                    put(keys.next(), values.next());
                }
            }
        }
    }

    @Override
    public V put(K key, V value) {
        int hashIndx = hashing(key);
        if (this.backing[hashIndx] == null) {
            this.backing[hashIndx] = chain.get();
        }
        V nowValue = this.backing[hashIndx].put(key, value);
        if (nowValue == null) {
            this.size++;
        }
        double loadFactor = (double) this.size / this.backing.length;
        if (loadFactor > 1) {
            rehash();
        }
        return nowValue;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ICollection<K> outKeys = new ArrayDeque<>();
        for (IDictionary<K, V> dic : this.backing) {
            if (dic != null) {
                for (K key : dic.keys()) {
                    outKeys.add(key);
                }
            }
        }
        return outKeys;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> outValues = new ArrayDeque<>();
        for (IDictionary<K, V> dic : this.backing) {
            if (dic != null) {
                for (V value : dic.values()) {
                    outValues.add(value);
                }
            }
        }
        return outValues;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }
}

