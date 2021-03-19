package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class AIHangmanGuesser implements IHangmanGuesser {
  private static final String scrabble = "data/scrabble.txt";


  @Override
  public char getGuess(String pattern, Set<Character> guesses) throws FileNotFoundException {
    File scrabble = new File(this.scrabble);
    Scanner scan = new Scanner(scrabble);
    SortedSet<String> dictionary = new TreeSet<>();
    while (scan.hasNext()) {
      String wrd = scan.nextLine();
      dictionary.add(wrd);
    }
    SortedSet<Character> guess = new TreeSet<>(guesses);
    SortedSet<String> possibilities = new TreeSet<>();
    for (String s : dictionary) {
      if (getPattern(s, guesses).equals(pattern)) {
        possibilities.add(s);
      }
    }
    SortedSet<Character> notGuessed = new TreeSet<>();
    for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
      if (!(guesses.contains(c))) {
        notGuessed.add(c);
      }
    }
    int letterFreq = 0;
    TreeMap<Character, Integer> freq = new TreeMap<>();
    for (char c : notGuessed) {
      for (String p : possibilities) {
        for (int i = 0; i < p.length(); i++) {
          if (p.charAt(i) == c) {
            letterFreq++;
          }
        }
      }
      freq.put(c, letterFreq);
      letterFreq = 0;
    }
    char leadingLetter = ' ';
    int leadingCount = 0;
    for (char x : freq.keySet()) {
      int temp = freq.get(x);
      if (temp > leadingCount) {
        leadingCount = temp;
        leadingLetter = x;
      }
    }
    return leadingLetter;
  }


  public String getPattern(String word, Set<Character> guesses) {
    String out = "";
    for (int i = 0; i < word.length(); i++) {
      if (guesses.contains(word.charAt(i))) {
        out += word.charAt(i);
      }
      if (!(guesses.contains(word.charAt(i)))) {
        out += "-";
      }
    }
    return out;
  }
}
