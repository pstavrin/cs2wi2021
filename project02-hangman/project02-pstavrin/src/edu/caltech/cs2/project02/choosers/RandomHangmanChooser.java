package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class RandomHangmanChooser implements IHangmanChooser {

  private int wordLength;
  private int maxGuesses;
  private static final Random RANDOM = new Random();
  private final String chosenWord;
  private SortedSet<Character> guesses;

  public RandomHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
      this.guesses = new TreeSet<>();
      this.wordLength = wordLength;
      this.maxGuesses = maxGuesses;
      if (this.wordLength < 1) {
        throw new IllegalArgumentException("Length of word cannot be zero");
      }
      if (this.maxGuesses < 1) {
        throw new IllegalArgumentException("Number of guesses cannot be zero");
      }
      File scrabble = new File("data/scrabble.txt");
      Scanner scan = new Scanner(scrabble);
      SortedSet<String> words = new TreeSet<>();
      while(scan.hasNext()) {
        String wrd = scan.nextLine();
        if (wrd.length() == this.wordLength) {
          words.add(wrd);
        }
      }
      if (words.isEmpty()) {
        throw new IllegalStateException("No words of desired length exist");
      }
      int currentIndex = 0;
      int ran = RANDOM.nextInt(words.size());
      String found = null;
      for (String s : words) {
        if (currentIndex == ran) {
          found = s;
        }
        currentIndex++;
      }
      this.chosenWord = found;

  }    

  @Override
  public int makeGuess(char letter) {
      if (this.maxGuesses < 1) {
          throw new IllegalStateException("No more guesses left :(");
      }
      if (this.guesses.contains(letter)) {
          throw new IllegalArgumentException("Character was already guessed before");
      }
      this.guesses.add(letter);
      if (letter < 'a' || letter > 'z') {
          throw new IllegalArgumentException("Invalid character. Must be a lowercase letter");
      }
      int letterCount = 0;
      for (int i = 0; i < this.chosenWord.length(); i++) {
          if (this.chosenWord.charAt(i) == letter) {
              letterCount++;
          }
      }
      if (letterCount == 0) {
          this.maxGuesses -= 1;
      }
    return letterCount;
  }

  @Override
  public boolean isGameOver() {
      String soFar = getPattern();
      if (this.chosenWord.equals(soFar) || this.maxGuesses == 0) {
          return true;
      }
      return false;
  }

  @Override
  public String getPattern() {
      String out = "";
      for (int i = 0; i < this.chosenWord.length(); i++) {
          if (this.guesses.contains(chosenWord.charAt(i))) {
              out += this.chosenWord.charAt(i);
          }
          if (!(this.guesses.contains(chosenWord.charAt(i)))) {
              out += "-";
          }
      }
    return out;
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
      return this.chosenWord;

  }
}