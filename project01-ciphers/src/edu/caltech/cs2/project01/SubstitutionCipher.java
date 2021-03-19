package edu.caltech.cs2.project01;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SubstitutionCipher {
    private String ciphertext;
    private Map<Character, Character> key;

    // Use this Random object to generate random numbers in your code,
    // but do not modify this line.
    private static final Random RANDOM = new Random();

    /**
     * Construct a SubstitutionCipher with the given cipher text and key
     * @param ciphertext the cipher text for this substitution cipher
     * @param key the map from cipher text characters to plaintext characters
     */
    public SubstitutionCipher(String ciphertext, Map<Character, Character> key) {
        this.ciphertext = ciphertext;
        this.key = key;
    }

    /**
     * Construct a SubstitutionCipher with the given cipher text and a randomly
     * initialized key.
     * @param ciphertext the cipher text for this substitution cipher
     */
    public SubstitutionCipher(String ciphertext) {
        this.ciphertext = ciphertext;
        this.key = new HashMap<>();
        for (char i = 'A'; i <= 'Z'; i++) {
            this.key.put(i, i);
        }
        SubstitutionCipher newOne = new SubstitutionCipher(this.ciphertext, this.key);
        for (int j = 0; j < 10000; j++) {
             newOne = newOne.randomSwap();
        }
        this.key = newOne.key;

    }

    /**
     * Returns the unedited cipher text that was provided by the user.
     * @return the cipher text for this substitution cipher
     */
    public String getCipherText() {
        return this.ciphertext;
    }

    /**
     * Applies this cipher's key onto this cipher's text.
     * That is, each letter should be replaced with whichever
     * letter it maps to in this cipher's key.
     * @return the resulting plain text after the transformation using the key
     */
    public String getPlainText() {
        String[] letters = this.ciphertext.split("");
        String out = "";
        if (this.ciphertext.equals("")) {
            return "";
        }
        for (int i = 0; i < letters.length; i++) {
            out += this.key.get(letters[i].charAt(0));
        }
        return out;
    }

    /**
     * Returns a new SubstitutionCipher with the same cipher text as this one
     * and a modified key with exactly one random pair of characters exchanged.
     *
     * @return the new SubstitutionCipher
     */
    public SubstitutionCipher randomSwap() {
        int swapNum = RANDOM.nextInt(26);
        int swapNumTwo = RANDOM.nextInt(26);
        while (swapNumTwo == swapNum) {
            swapNumTwo = RANDOM.nextInt(26);
        }
        char[] alphabet = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        char tempLetter = alphabet[swapNum];
        alphabet[swapNum] = alphabet[swapNumTwo];
        alphabet[swapNumTwo] = tempLetter;
        Map<Character, Character> tempKey = new HashMap<>(this.key);
        tempKey.put(alphabet[swapNum], tempKey.get(alphabet[swapNumTwo]));
        tempKey.put(alphabet[swapNumTwo], this.key.get(alphabet[swapNum]));
        return new SubstitutionCipher(this.ciphertext, tempKey);
    }

    /**
     * Returns the "score" for the "plain text" for this cipher.
     * The score for each individual quadgram is calculated by
     * the provided likelihoods object. The total score for the text is just
     * the sum of these scores.
     * @param likelihoods the object used to find a score for a quadgram
     * @return the score of the plain text as calculated by likelihoods
     */
    public double getScore(QuadGramLikelihoods likelihoods) {
        double score = 0.0;
        ArrayList<String> quadgrams = new ArrayList<>();
        String plain = this.getPlainText();
        int start = 0;
        int end = 4;
        int quads = this.ciphertext.length() - 3;
        for (int i = 0; i < quads; i++) {
            String s = plain.substring(start, end);
            quadgrams.add(s);
            start++;
            end++;
        }
        for (int j = 0; j < quadgrams.size(); j++) {
            String toEvaluate = quadgrams.get(j);
            double likelihood = likelihoods.get(toEvaluate);
            score += likelihood;
        }
        return score;
    }

    /**
     * Attempt to solve this substitution cipher through the hill
     * climbing algorithm. The SubstitutionCipher this is called from
     * should not be modified.
     * @param likelihoods the object used to find a score for a quadgram
     * @return a SubstitutionCipher with the same ciphertext and the optimal
     *  found through hill climbing
     */
    public SubstitutionCipher getSolution(QuadGramLikelihoods likelihoods) {
        SubstitutionCipher one = new SubstitutionCipher(this.ciphertext);
        int i = 0;
        while (i < 1000) {
            SubstitutionCipher em = one.randomSwap();
            double scoreC = one.getScore(likelihoods);
            double scoreM = em.getScore(likelihoods);
            if (scoreM > scoreC) {
                one = em;
                i = 0;
            }
            i++;
        }
        return one;
    }
    public static void main(String[] args) throws FileNotFoundException {
        try {
            QuadGramLikelihoods likelihoods = new QuadGramLikelihoods();
            File file = new File("cryptogram.txt");
            File copy = file;
            Scanner scan = new Scanner(copy);
            char[] possibilities = {
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                    'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
            };
            String replacement = "";
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    for (int j = 0; j < possibilities.length; j++) {
                        if (line.charAt(i) == possibilities[j]) {
                            replacement += line.charAt(i);
                        }
                    }
                }
            }
            SubstitutionCipher best = new SubstitutionCipher(replacement);
            for (int i = 0; i < 20; i ++) {
                SubstitutionCipher cipher = best.getSolution(likelihoods);
                if (cipher.getScore(likelihoods) > best.getScore(likelihoods)) {
                    best = cipher;
                }
            }
            System.out.println(best.getPlainText());





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
