package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {
  private int maxGuesses;
  private static final Random RANDOM = new Random();
  private SortedSet<String> wordFamily;
  private SortedSet<Character> guesses;
  private String pattern;
    
  public EvilHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
    this.guesses = new TreeSet<>();
    this.maxGuesses = maxGuesses;
    if (wordLength < 1) {
      throw new IllegalArgumentException("Length of word cannot be zero");
    }
    if (this.maxGuesses < 1) {
      throw new IllegalArgumentException("Number of guesses cannot be zero");
    }
    File scrabble = new File("data/scrabble.txt");
    Scanner scan = new Scanner(scrabble);
    this.wordFamily = new TreeSet<>();
    while(scan.hasNext()) {
      String wrd = scan.nextLine();
      if (wrd.length() == wordLength) {
        this.wordFamily.add(wrd);
      }
    }
    if (this.wordFamily.isEmpty()) {
      throw new IllegalStateException("No words of desired length exist");
    }
    this.pattern = "-".repeat(wordLength);
  }


  @Override
  public int makeGuess(char letter) {
    if (this.maxGuesses < 1) {
      throw new IllegalStateException("No more guesses left :(");
    }
    if (this.guesses.contains(letter)) {
      throw new IllegalArgumentException("Character was already guessed before");
    }
    if (letter < 'a' || letter > 'z') {
      throw new IllegalArgumentException("Invalid character. Must be a lowercase letter");
    }
    this.guesses.add(letter);
    TreeMap<String, TreeSet<String>> possibilities = new TreeMap<>();
    for (String s : this.wordFamily) {
      String pat = "";
      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == letter) {
          pat += letter;
        }
        else {
          char c = this.pattern.charAt(i);
          pat += c;
        }
      }
      if (possibilities.containsKey(pat)) {
        TreeSet<String> existing = possibilities.get(pat);
        existing.add(s);
      }
      else {
        TreeSet<String> tempSet = new TreeSet<>();
        tempSet.add(s);
        possibilities.put(pat, tempSet);
      }
    }
    int size = 0;
    String leadingKey = null;
    for(String k : possibilities.keySet()) {
      TreeSet<String> tree = possibilities.get(k);
      int treeSize = tree.size();
      if (treeSize > size) {
        size = treeSize;
        leadingKey = k;
      }
    }
    this.wordFamily = possibilities.get(leadingKey);
    this.pattern = leadingKey;
    int count = 0;
    for (int p = 0; p < this.pattern.length(); p++) {
      if (this.pattern.charAt(p) == letter) {
        count++;
      }
    }
    if (count == 0) {
      this.maxGuesses -= 1;
    }
    return count;
  }

  @Override
  public boolean isGameOver() {
    String soFar = this.getPattern();
    if (!(soFar.contains("-")) || this.maxGuesses == 0) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    return this.pattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return this.maxGuesses;
  }

  @Override
  public String getWord() {
    this.maxGuesses = 0;
    return this.wordFamily.first();
  }
}