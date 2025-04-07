import java.io.*;
import java.util.*;

/**
 * CS242 - Spring 2025 - Assignment #3
 * 
 * Team Members:
 * [Your Name]
 * [Team Member Name]
 * 
 * Collaborators: Daniella Rabayev and Daisy Molina
 * 
 * This class implements dynamic programming for text justification
 * following LaTeX rules, compared with greedy approach.
 */
public class Assignment3 {

    public static int totalLength(String[] W, int i, int j) { // calculate the total length of words with spaces
        int length = 0;
        for (int k = i; k < j; k++) {
            length += W[k].length();
        }
        if (j > i) {
            length += (j - i - 1); // Adding spaces between words
        }
        return length;
    }

    public static int badness(String[] W, int i, int j, int w) { // calculate the badness of a line of words
        int lineLength = totalLength(W, i, j);
        if (lineLength > w) {
            return Integer.MAX_VALUE;
        } else {
            int extraSpaces = w - lineLength;
            return extraSpaces * extraSpaces * extraSpaces;
        }
    }

    public static int memoizedMinimumBadness(String[] W, int i, int[] memo, int[] linebreaks_memo, int w) { // compute minimum badness using memoization
        if (memo[i] >= 0) {
            return memo[i];
        }
        if (i == W.length) {
            memo[i] = 0;
            linebreaks_memo[i] = W.length;
        } else {
            int minBadness = Integer.MAX_VALUE;
            int indexOfMin = 0;
            for (int j = i + 1; j <= W.length; j++) {
                int temp = badness(W, i, j, w);
                temp += memoizedMinimumBadness(W, j, memo, linebreaks_memo, w);
                if (temp < minBadness) {
                    minBadness = temp;
                    indexOfMin = j;
                }
            }
            memo[i] = minBadness;
            linebreaks_memo[i] = indexOfMin;
        }
        return memo[i];
    }

    public static int[] split(String[] W, int w) { // split words into lines to minimize aggregated badness
        int n = W.length;
        int[] memo = new int[n + 1];
        int[] linebreaks_memo = new int[n + 1]; // list of indices of the badness of all lines in split
        for (int i = 0; i <= n; i++) {
            memo[i] = -1;
        }
        memoizedMinimumBadness(W, 0, memo, linebreaks_memo, w);
        return linebreaks_memo; 
    }

    public static String justify(String[] W, int w, int[] L) { // generated justified text based on indices of split
        StringBuilder justifiedText = new StringBuilder(); // mutable strings
        for (int i = 0; i < L.length - 1; i++) {
            int start = L[i];
            int end = L[i + 1];
            int totalLength = totalLength(W, start, end);
            int spaces = w - totalLength;
            int gaps = end - start - 1;
            StringBuilder line = new StringBuilder();
            if (gaps > 0) {
                int spaceBetweenWords = spaces / gaps; // fix spacing
                int extraSpaces = spaces % gaps;
                for (int j = start; j < end; j++) {
                    line.append(W[j]);
                    if (j < end - 1) {
                        for (int k = 0; k < spaceBetweenWords; k++) {
                            line.append(' ');
                        }
                        if (j - start < extraSpaces) {
                            line.append(' ');
                        }
                    }
                }
            } else {
                line.append(W[start]);
                for (int j = 0; j < spaces; j++) {
                    line.append(' ');
                }
            }
            justifiedText.append(line.toString()).append('\n');
        }
        return justifiedText.toString();
    }

    public static void writeToFile(String filename, String content) throws IOException { // write content to a file for just.txt and unjust.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of words: ");
        int n = scanner.nextInt();
        System.out.print("Enter the page width: ");
        int w = scanner.nextInt();
        String[] W = new String[n];
        Random random = new Random(); // generate random words
        for (int i = 0; i < n; i++) {
            int length = random.nextInt(15) + 1;
            StringBuilder word = new StringBuilder();
            for (int j = 0; j < length; j++) {
                word.append('a');
            }
            W[i] = word.toString();
        }
        int[] L = split(W, w);
        String justifiedText = justify(W, w, L);
        writeToFile("just.txt", justifiedText);
        StringBuilder unjustText = new StringBuilder();
        for (String word : W) {
            unjustText.append(word).append(' ');
        }
        writeToFile("unjust.txt", unjustText.toString().trim());
    }
}