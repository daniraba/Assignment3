import java.io.*;
import java.util.*;

public class Assignment3 {
    
    // Badness method
    public static int badness(String[] W, int i, int j, int width) {
        int length = 0;
        for (int k = i; k < j; k++) {
            length += W[k].length();
            if (k < j - 1) length++; // Space between words
        }
        int extraSpace = width - length;
        return (extraSpace >= 0) ? (int) Math.pow(extraSpace, 3) : Integer.MAX_VALUE;
    }

    // Dynamic Programming approach to find the optimal line breaks
    public static int memoizedMinBadness(String[] W, int i, int[] memo, int[] lineBreaks, int width) {
        if (memo[i] >= 0) return memo[i];
        if (i == W.length) {
            memo[i] = 0;
            lineBreaks[i] = W.length;
            return 0;
        }
        int minBadness = Integer.MAX_VALUE;
        int indexOfMin = 0;
        for (int j = i + 1; j <= W.length; j++) {
            int temp = badness(W, i, j, width);
            if (temp == Integer.MAX_VALUE) break;
            temp += memoizedMinBadness(W, j, memo, lineBreaks, width);
            if (temp < minBadness) {
                minBadness = temp;
                indexOfMin = j;
            }
        }
        memo[i] = minBadness;
        lineBreaks[i] = indexOfMin;
        return minBadness;
    }

    // Method to determine line breaks
    public static List<Integer> split(String[] W, int width) {
        int n = W.length;
        int[] memo = new int[n + 1];
        int[] lineBreaks = new int[n + 1];
        Arrays.fill(memo, -1);
        memoizedMinBadness(W, 0, memo, lineBreaks, width);
        
        List<Integer> breaks = new ArrayList<>();
        for (int i = 0; i < n; i = lineBreaks[i]) {
            breaks.add(i);
        }
        return breaks;
    }

    // Justify text based on line breaks
    public static void justify(String[] W, int width, List<Integer> breaks, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < breaks.size(); i++) {
            int start = breaks.get(i);
            int end = (i + 1 < breaks.size()) ? breaks.get(i + 1) : W.length;
            int length = 0;
            for (int j = start; j < end; j++) {
                length += W[j].length();
            }
            int spaces = width - length;
            int gaps = end - start - 1;
            StringBuilder line = new StringBuilder();
            for (int j = start; j < end; j++) {
                line.append(W[j]);
                if (j < end - 1) {
                    int spaceToAdd = spaces / gaps + (j - start < spaces % gaps ? 1 : 0);
                    line.append(" ".repeat(spaceToAdd));
                }
            }
            writer.write(line.toString());
            writer.newLine();
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of words (n): ");
        int n = scanner.nextInt();
        System.out.print("Enter page width (ω): ");
        int width = scanner.nextInt();
        scanner.close();
        
        Random random = new Random();
        String[] W = new String[n];
        for (int i = 0; i < n; i++) {
            int length = 1 + random.nextInt(15);
            W[i] = "a".repeat(length);
        }
        
        List<Integer> breaks = split(W, width);
        justify(W, width, breaks, "just.txt");
        
        BufferedWriter writer = new BufferedWriter(new FileWriter("unjust.txt"));
        for (String word : W) {
            writer.write(word + " ");
        }
        writer.close();
    }
}
