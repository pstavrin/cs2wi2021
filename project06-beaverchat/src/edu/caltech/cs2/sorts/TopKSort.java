package edu.caltech.cs2.sorts;

import edu.caltech.cs2.datastructures.MinFourHeap;
import edu.caltech.cs2.interfaces.IPriorityQueue;


public class TopKSort {
    /**
     * Sorts the largest K elements in the array in descending order. Modifies the array in place.
     * @param array - the array to be sorted; will be manipulated.
     * @param K - the number of values to sort
     * @param <E> - the type of values in the array
     * @throws IllegalArgumentException if K < 0
     */
    public static <E> void sort(IPriorityQueue.PQElement<E>[] array, int K) {
        if (K < 0) {
            throw new IllegalArgumentException("K cannot be negative!");
        }
        if (K == 0) {
            for (int i = 0; i < array.length; i++) {
                array[i] = null;
            }
        }
        else {
            if (K > array.length) {
                K = array.length;
            }
            IPriorityQueue<E> theHeap = new MinFourHeap<>();
            IPriorityQueue.PQElement<E>[] out = new IPriorityQueue.PQElement[array.length];
            for (int i = 0; i < K; i++) {
                theHeap.enqueue(array[i]);
            }
            for (int j = K; j < array.length; j++) {
                if (array[j].priority > theHeap.peek().priority) {
                    theHeap.dequeue();
                    theHeap.enqueue(array[j]);
                }
            }
            for (int i = K - 1; i >= 0; i--) {
                out[i] = theHeap.dequeue();
            }
            for (int i = K; i < out.length; i++) {
                out[i] = null;
            }
            System.arraycopy(out, 0, array, 0, array.length);
        }
    }

}
