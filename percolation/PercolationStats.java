import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double stddev;
    private final double mean;
    private final int trials;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        if (trials <= 0) {
            throw new IllegalArgumentException("trials must be > 0");
        }


        final double[] p = new double[trials];
        for (int i = 0; i < trials; i++) {
            p[i] = runExperiement(n);
        }
        mean = StdStats.mean(p);
        stddev = StdStats.stddev(p);
        this.trials = trials;
    }

    // test client (see below)
    public static void main(String[] args) {
        final PercolationStats stats = new PercolationStats(200, 100);
        System.out.println(stats.confidenceLo() + " < " + stats.mean() + " < " + stats.confidenceHi());
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - 1.96 * stddev / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + 1.96 * stddev / Math.sqrt(trials);
    }

    private double runExperiement(int n) {
        final Percolation percolation = new Percolation(n);
        final int[] ids = new int[n * n];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = i;
        }

        StdRandom.shuffle(ids);
        for (int i = 0; i < ids.length && !percolation.percolates(); i += 1) {
            percolation.open((ids[i] / n) + 1, (ids[i] % n) + 1);
        }
        return percolation.numberOfOpenSites() / (double) ids.length;
    }
}
