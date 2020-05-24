import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    //                                  0  1  2  3  4  5  6  7  8+
    private final static int[] score = {0, 0, 0, 1, 1, 2, 3, 5, 11};
    private Set<String> dictionary;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        final Set<String> words = new HashSet<>();

        for (String word: dictionary) {
            if (word.length() < 3) {
                continue;
            }
            words.add(word);
        }
        this.dictionary = words;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        final Trie trie = new Trie();
        for (String word: dictionary) {
            trie.insert(word);
        }
        final Queue<String> result = new Queue<>();
        int n = board.rows();
        int m = board.cols();
        final boolean[][] used = new boolean[n][m];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                char letter = board.getLetter(x, y);
                final Trie.Node node = (letter == 'Q') ? trie.next(trie.root.next('Q'), 'U') : trie.root.next(letter);
                if (node != null && node.count > 0) {
                    dfs(x, y, node, trie, board, used, result);
                }
            }
        }
        return result;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dictionary.contains(word)) {
            return 0;
        }
        int scoreIndex = Math.min(word.length(), score.length - 1);
        return score[scoreIndex];
    }

    public static void main(String[] args) {
        final In in = new In(args[0]);
        final String[] dictionary = in.readAllStrings();
        final BoggleSolver solver = new BoggleSolver(dictionary);
        final BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private void dfs(int x, int y, Trie.Node node, Trie trie, BoggleBoard board, boolean[][] used, Queue<String> result) {
        int n = board.rows();
        int m = board.cols();

        used[x][y] = true;
        if (node.value != null) {
            result.enqueue(node.value);
            trie.remove(node.value);
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int xTo = x + dx;
                int yTo = y + dy;

                if ((dx == 0 && dy == 0)
                    || !(0 <= xTo && xTo < n && 0 <= yTo && yTo < m)
                    || used[xTo][yTo]) {
                    continue;
                }
                char letter = board.getLetter(xTo, yTo);
                final Trie.Node nextNode = (letter == 'Q') ? trie.next(node.next('Q'), 'U') : node.next(letter);

                if (nextNode != null && nextNode.count > 0) {
                    dfs(xTo, yTo, nextNode, trie, board, used, result);
                }
            }
        }
        used[x][y] = false;
    }

    private static class Trie {
        private final Node root = new Node();

        public Node getRoot() {
            return root;
        }

        public Node next(Node node, char c) {
            if (node == null) {
                return null;
            }
            return node.next(c);
        }

        public void insert(String s) {
            Node node = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                node.count += 1;
                node = node.nextOrCreate(c);
            }
            node.count += 1;
            node.value = s;
        }

        public void remove(String s) {
            Node node = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                node.count -= 1;
                node = node.next(c);
            }
            node.count -= 1;
            node.value = null;
        }

        public boolean contains(String s) {
            Node node = root;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                node = node.next(c);
                if (node == null || node.count == 0) {
                    return false;
                }
            }
            return node.value != null;
        }

        public static class Node {
            private static final int R = 26;
            private final Node[] next = new Node[R];
            private String value;
            private int count = 0;

            public Node next(char c) {
                int idx = toIndex(c);
                return next[idx];
            }

            public Node nextOrCreate(char c) {
                int idx = toIndex(c);
                if (next[idx] == null) {
                    next[idx] = new Node();
                }
                return next[idx];
            }

            private int toIndex(char c) {
                return (int) c - (int) 'A';
            }
        }
    }
}
