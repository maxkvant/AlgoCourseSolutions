import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solver {
    private final List<Board> moves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("initial Board must be not null");
        }
        moves = solve(initial);
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return moves != null;
    }

    // min number of moves to solve initial board
    public int moves() {
        return moves.size() - 1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return new ArrayList<>(moves);
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        final In in = new In(args[0]);
        final int n = in.readInt();
        final int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        final Board initial = new Board(tiles);

        // solve the puzzle
        final Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }

    private static List<Board> solve(Board initialBoard) {
        final MinPQ<BoardPriority> priorityQueue = new MinPQ<>();
        final MinPQ<BoardPriority> twinPriorityQueue = new MinPQ<>();

        priorityQueue.insert(new BoardPriority(initialBoard, 0, null));
        twinPriorityQueue.insert(new BoardPriority(initialBoard.twin(), 0, null));

        while (!priorityQueue.isEmpty() && !twinPriorityQueue.isEmpty()) {
            final BoardPriority top     =     priorityQueue.delMin();
            final BoardPriority topTwin = twinPriorityQueue.delMin();

            if (top.board.isGoal()) {
                BoardPriority cur = top;
                final List<Board> moves = new ArrayList<>();
                while (cur != null) {
                    moves.add(cur.board);
                    cur = cur.prev;
                }
                Collections.reverse(moves);
                assert moves.get(0).equals(initialBoard);
                return moves;
            }
            for (Board neighbor : top.board.neighbors()) {
                if (top.prev != null && neighbor.equals(top.prev.board)) {
                    continue;
                }
                priorityQueue.insert(new BoardPriority(neighbor, top.graphDistance + 1, top));
            }

            if (topTwin.board.isGoal()) {
                return new ArrayList<>();
            }
            for (Board neighbor: topTwin.board.neighbors()) {
                if (topTwin.prev != null && neighbor.equals(topTwin.prev.board)) {
                    continue;
                }
                twinPriorityQueue.insert(new BoardPriority(neighbor, topTwin.graphDistance + 1, topTwin));
            }
        }

        throw new IllegalStateException("there must be a solution for either initial board or its twin");
    }

    private static final class BoardPriority implements Comparable<BoardPriority> {
        private final Board board;
        private final BoardPriority prev;
        private final int graphDistance;
        private final int manhattanDistance;

        public BoardPriority(Board board, int graphDistance, BoardPriority prev) {
            this.board = board;
            this.graphDistance = graphDistance;
            this.prev = prev;
            this.manhattanDistance = board.manhattan();
        }

        public int getPriority() {
            return graphDistance + manhattanDistance;
        }

        @Override
        public int compareTo(BoardPriority that) {
            // >= 1 if this > that
            return getPriority() - that.getPriority();
        }
    }
}