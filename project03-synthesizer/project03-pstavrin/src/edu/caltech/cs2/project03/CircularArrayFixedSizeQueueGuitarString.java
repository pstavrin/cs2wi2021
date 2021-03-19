package edu.caltech.cs2.project03;

import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Random;


public class CircularArrayFixedSizeQueueGuitarString {

    private IFixedSizeQueue<Double> guitarString;
    private static final double SAMPLING_RATE = 44100;
    private static final double ENERGY_DECAY_FACTOR = 0.996;
    private static Random r = new Random();


    public CircularArrayFixedSizeQueueGuitarString(double frequency) {
        int N = (int)(SAMPLING_RATE / frequency) + 1;
        this.guitarString = new CircularArrayFixedSizeQueue<>(N);

        for (int i = 0; i < N; i++)
            this.guitarString.enqueue(0.0);

    }

    public int length() {
        return this.guitarString.size();
    }

    public void pluck() {
        for(int i=0; i<this.length(); i++) {
            this.guitarString.dequeue();
            this.guitarString.enqueue(r.nextDouble() - 0.5);
        }
    }

    public void tic() {
        this.guitarString.enqueue(ENERGY_DECAY_FACTOR*(this.guitarString.dequeue() + this.guitarString.peek()) / 2);
    }

    public double sample() {
        return this.guitarString.peek();
    }
}
