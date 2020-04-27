import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        final int k = Integer.parseInt(args[0]);
        final RandomizedQueue<String> queue = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            final String string = StdIn.readString();
            queue.enqueue(string);
        }
        for (int i = 0; i < k; i++) {
            final String item = queue.dequeue();
            StdOut.println(item);
        }
    }
}
