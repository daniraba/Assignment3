public class TextJustifier {

    public static int totalLength(String[] W, int i, int j) {
        int length = 0;
        for (int k = i; k < j; k++) {
            length += W[k].length();
        }

        length += (j - i - 1); // Add spaces between words
        return length;
    }

    public static int badness(String[] W, int i , int j, int width) {
        int lineLength = totalLength(W, i, j);
        if (lineLength > width) {
            return Integer.MAX_VALUE; // Badness is infinite if line exceeds width
        } else {
            int extraSpaces = width - lineLength;
            return extraSpaces * extraSpaces; // Badness is the square of extra spaces
        }
    }

    public static void main (String[] args) {
        String[] W = {"This", "is", "an", "example", "line"};
        int width = 20;

        System.out.println("Badness: " + badness(W, 0, 3, width)); // Example usage
    }
}