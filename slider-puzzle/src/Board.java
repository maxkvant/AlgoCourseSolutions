import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public final class Board {
    private static final int[] DI = {0, -1, 0,  1};
    private static final int[] DJ = {-1, 0, 1,  0};

    private final int[][] tiles;
    private final int hammingDistance;
    private final int manhattanDistance;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("tiles must not be null");
        }
        if (tiles.length < 2 || tiles[0].length != tiles.length) {
            throw new IllegalArgumentException("tiles must be a square matrix of size >= 2");
        }
        this.tiles = copyTiles(tiles);

        hammingDistance = computeHammingDist(tiles);
        manhattanDistance = computeManhattanDist(tiles);
    }

    // string representation of this board
    public String toString() {
        int n = dimension();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                stringBuilder.append(String.format("%2d ", tiles[i][j]));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hammingDistance == 0;
    }

    // does this board equal y?
    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }

        if (that == null || that.getClass() != Board.class) {
            return false;
        }
        final Board thatBoard = (Board) that;
        if (thatBoard.dimension() != this.dimension()) {
            return false;
        }
        int n = dimension();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != thatBoard.tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        final int n = dimension();
        int blankI = -1;
        int blankJ = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    blankI = i;
                    blankJ = j;
                    break;
                }
            }
        }
        assert blankI != -1;
        final Stack<Board> neighbors = new Stack<>();
        for (int ind = 0; ind < DI.length; ind++) {
            int exchangeI = blankI + DI[ind];
            int exchangeJ = blankJ + DJ[ind];
            if (!(0 <= exchangeI && exchangeI < n && 0 <= exchangeJ && exchangeJ < n)) {
                continue;
            }

            final int[][] tilesCopy = copyTiles(tiles);
            tilesCopy[blankI][blankJ] = tiles[exchangeI][exchangeJ];
            tilesCopy[exchangeI][exchangeJ] = 0;
            neighbors.push(new Board(tilesCopy));
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int row = (tiles[0][0] == 0 || tiles[0][1] == 0) ? 1 : 0;

        int[][] tileCopy = copyTiles(tiles);
        tileCopy[row][0] = tiles[row][1];
        tileCopy[row][1] = tiles[row][0];
        return new Board(tileCopy);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        final Board board = new Board(new int[][]{
                {0,  1,  3},
                {4,  2,  5},
                {7,  8,  6}
        });

        StdOut.println(board);
        StdOut.println("hammingDist = " + board.hamming() + ", manhattanDist = " + board.manhattan());
        StdOut.println(board.twin());
        for (Board neighbor: board.neighbors()) {
            StdOut.println(neighbor.isGoal());
            StdOut.println(neighbor);
        }
    }

    private static int computeHammingDist(int[][] tiles) {
        int n = tiles.length;
        int result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    continue;
                }
                if (tiles[i][j] != i * n + j + 1) {
                    result += 1;
                }
            }
        }
        return result;
    }

    // sum of Manhattan distances between tiles and goal
    private static int computeManhattanDist(int[][] tiles) {
        final int n = tiles.length;
        int result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    continue;
                }
                int tile = tiles[i][j];
                int targetI = (tile - 1) / n;
                int targetJ = (tile - 1) % n;
                result += Math.abs(targetI - i) + Math.abs(targetJ - j);
            }
        }
        return result;
    }

    private static int[][] copyTiles(int[][] tiles) {
        int n = tiles.length;
        int[][] tilesCopy = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, tilesCopy[i], 0, n);
        }
        return tilesCopy;
    }
}
