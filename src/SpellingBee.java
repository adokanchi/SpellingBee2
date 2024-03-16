import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Spelling Bee
 *
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 *
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 *
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, [ADD YOUR NAME HERE]
 *
 * Written on March 5, 2023 for CS2 @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {
    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];
    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<String>();
    }
    public void generate() {
        makeWords("", letters);
    }
    public void makeWords(String word, String letters) {
        if (letters.isEmpty()) {return;} // Base case
        for (int i = 0; i < letters.length(); i++) { // Recursion
            // Make copies of word/letters so they can be left unmodified
            String tWord = word;
            String tLetters;
            // add a char to end of word, remove from letters
            tWord += letters.charAt(i);
            tLetters = letters.substring(0,i);
            if (i < letters.length() - 1) { // if i is not the last char in string
                tLetters += letters.substring(i + 1);
            }
            words.add(tWord);
            makeWords(tWord, tLetters);
        }
    }
    public void sort() {
        // Convert to Array
        String[] wordsArr = new String[words.size()];
        for (int i = 0; i < words.size(); i++) {
            wordsArr[i] = words.get(i);
        }
        // Sort
        wordsArr = mergeSort(wordsArr);
        // Convert to ArrayList
        while (!words.isEmpty()) {words.remove(0);}
        for (String s : wordsArr) {
            words.add(s);
        }
    }
    // Overload: if start/end indexes aren't given, use  whole array
    public String[] mergeSort(String[] arr) {
        return mergeSort(arr, 0, arr.length - 1);
    }
    // Applies mergeSort to arr from indices l to r
    public String[] mergeSort(String[] arr, int l, int r) {
        if (l == r) { // Base case
            String[] sol = new String[1];
            sol[0] = arr[l];
            return sol;
        }
        // Recursion
        int med = (l + r) / 2;
        String[] arr1 = mergeSort(arr, l, med);
        String[] arr2 = mergeSort(arr, med+1, r);
        return merge(arr1, arr2);
    }
    // Merge sorted arrays arr1 and arr2, return sorted array
    public String[] merge(String[] arr1, String[] arr2) {
        String[] merged = new String[arr1.length + arr2.length];
        int i = 0, j = 0, k = 0;
        while (j < arr1.length && k < arr2.length) { // While there are elements left in both arrays
            if (arr1[j].compareToIgnoreCase(arr2[k]) < 0) {
                merged[i] = arr1[j++];
            }
            else {
                merged[i] = arr2[k++];
            }
            i++;
        }
        // When one array is cleared
        while (j < arr1.length) {
            merged[i++] = arr1[j++];
        }
        while (k < arr2.length){
            merged[i++] = arr2[k++];
        }
        return merged;
    }
    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1)))
                words.remove(i + 1);
            else
                i++;
        }
    }
    // Uses binary search to
    public void checkWords() {
        for (int i = 0; i < words.size(); i++) {
            if (!found(words.get(i))) {
                words.remove(i--);
            }
        }
    }
    // Returns true if s in dictionary, false otherwise
    public boolean found (String s) {
        return isInDictionary(s,0,DICTIONARY_SIZE - 1);
    }
    // Returns true if target is in dict between indices l and r, false otherwise
    public boolean isInDictionary(String target, int l, int r) {
        // Base case
        if (l == r) {return target.equals(DICTIONARY[l]);}
        // Recursion
        int mid = (l + r) / 2;
        int comp = target.compareTo(DICTIONARY[mid]);
        if (comp == 0) {return true;} // if target==dict[mid] return true
        if (comp < 0) {return isInDictionary(target, l, mid);} // if before dict[mid] check first half
        return isInDictionary(target, mid + 1, r); // otherwise check second half
    }
    // Prints all valid words to wordList.txt
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            writer.append(word);
            writer.newLine();
        }
        writer.close();
    }
    public ArrayList<String> getWords() {
        return words;
    }
    public void setWords(ArrayList<String> words) {
        this.words = words;
    }
    public SpellingBee getBee() {
        return this;
    }
    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }
    public static void main(String[] args) {
        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));
        // Load the dictionary
        SpellingBee.loadDictionary();
        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        sb.removeDuplicates();
        sb.checkWords();
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}