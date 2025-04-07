import java.io.*;
import java.util.*;

public class Assignment3 {

    public static int totalLength(String[] W, int i, int j) {
        int length = 0;
        for (int k = i; k < j; k++) {
            length += W[k].length();
        }
        length += (j - i - 1); // spaces between words
        return length;
    }

    public static int badness(String[] W, int i, int j, int w) {
        int lineLength = totalLength(W, i, j);
        if (lineLength > w) {
            return Integer.MAX_VALUE;
        } else {
            int extraSpaces = w - lineLength;
            return extraSpaces * extraSpaces * extraSpaces;
        }
    }

    public static int memoizedMinBadness(String[] W, int i, int[] memo, int[] linebreaks_memo, int w) {
        if (memo[i] >= 0) return memo[i];
        if (i == W.length) {
            memo[i] = 0;
            linebreaks_memo[i] = W.length;
            return 0;
        }
        int minBadness = Integer.MAX_VALUE;
        int indexOfMin = 0;
        for (int j = i + 1; j <= W.length; j++) {
            int temp = badness(W, i, j, w);
            if (temp == Integer.MAX_VALUE) break;
            temp += memoizedMinBadness(W, j, memo, linebreaks_memo, w);
            if (temp < minBadness) {
                minBadness = temp;
                indexOfMin = j;
            }
        }
        memo[i] = minBadness;
        linebreaks_memo[i] = indexOfMin;
        return minBadness;
    }

    public static List<Integer> split(String[] W, int w) {
        int n = W.length;
        int[] memo = new int[n + 1];
        int[] lineBreaks = new int[n + 1];
        Arrays.fill(memo, -1);
        memoizedMinBadness(W, 0, memo, lineBreaks, w);

        List<Integer> breaks = new ArrayList<>();
        for (int i = 0; i < n; i = lineBreaks[i]) {
            breaks.add(i);
        }
        return breaks;
    }

    public static void justify(String[] W, int w, List<Integer> breaks, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < breaks.size(); i++) {
            int start = breaks.get(i);
            int end = (i + 1 < breaks.size()) ? breaks.get(i + 1) : W.length;
            int length = 0;
            for (int j = start; j < end; j++) {
                length += W[j].length();
            }
            int spaces = w - length;
            int gaps = end - start - 1;
            StringBuilder line = new StringBuilder();
            for (int j = start; j < end; j++) {
                line.append(W[j]);
                if (j < end - 1) {
                    int spaceToAdd = gaps == 0 ? 0 : spaces / gaps + (j - start < spaces % gaps ? 1 : 0);
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
        System.out.print("Enter page width (w): ");
        int width = scanner.nextInt();
        System.out.print("Enter number of words (n): ");
        int n = scanner.nextInt();
        scanner.close();

        // Generate random words
        String[] W = new String[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int wordLength = random.nextInt(15) + 1;
            char[] word = new char[wordLength];
            Arrays.fill(word, 'a');
            W[i] = new String(word);
        }

        // Write unjustified output
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("unjust.txt"))) {
            for (String word : W) {
                writer.write(word + " ");
            }
        }

        // Write justified output
        List<Integer> breaks = split(W, width);
        justify(W, width, breaks, "just.txt");
    }
}