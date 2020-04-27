import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int n;
    private final int topId;
    private final int bottomId;
    private final boolean[][] open;
    private final int[] dRow = { -1, 1,  0, 0};
    private final int[] dCol = {  0, 0, -1, 1};
    private final boolean[] connectedToBottom;
    private final WeightedQuickUnionUF unionFindTop;

    private int openSites = 0;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }

        this.n = n;
        this.topId = n * n;
        this.bottomId = n * n + 1;
        this.open = new boolean[n + 1][n + 1];
        int vertices = n * n + 2;
        this.unionFindTop = new WeightedQuickUnionUF(vertices);
        this.connectedToBottom = new boolean[vertices];
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);

        if (isOpen(row, col)) {
            return;
        }

        openSites += 1;
        open[row][col] = true;
        for (int i = 0; i < dRow.length; i++) {
            int rowTo = row + dRow[i];
            int colTo = col + dCol[i];
            if (!(check(rowTo, colTo))) {
                continue;
            }
            if (open[rowTo][colTo]) {
                union(toId(row, col), toId(rowTo, colTo));
            }
        }
        if (row == 1) {
            union(toId(row, col), topId);
        }
        if (row == n) {
            connectedToBottom[unionFindTop.find(toId(row, col))] = true;
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);

        return open[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);

        return unionFindTop.connected(toId(row, col), topId);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return connectedToBottom(topId);
    }

    private boolean connectedToBottom(int v) {
        return connectedToBottom[unionFindTop.find(v)];
    }

    private void union(int v, int u) {
        boolean reachesBottom = connectedToBottom(v) || connectedToBottom(u);
        unionFindTop.union(v, u);
        if (reachesBottom) {
            connectedToBottom[unionFindTop.find(v)] = true;
        }
    }

    private boolean check(int row, int col) {
        return (1 <= row && row <= n && 1 <= col && col <= n);
    }

    private void validate(int row, int col) {
        if (!check(row, col)) {
            throw new IllegalArgumentException("row and col must be inside the grid");
        }
    }

    private int toId(int row, int col) {
        return n * (row - 1) + (col - 1);
    }
}
