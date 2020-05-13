import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int w, h;
    private final Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null || picture.width() <= 0 || picture.height() <= 0) {
            throw new IllegalArgumentException("given redundant picture or null");
        }

        w = picture.width();
        h = picture.height();

        final Picture curPicture = new Picture(w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                curPicture.setRGB(x, y, picture.getRGB(x, y));
            }
        }
        this.picture = curPicture;
    }

    // current picture
    public Picture picture() {
        final Picture res = new Picture(w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                res.setRGB(x, y, picture.getRGB(x, y));
            }
        }
        return res;
    }

    // width of current picture
    public int width() {
        return w;
    }

    // height of current picture
    public int height() {
        return h;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (!(0 <= x && x < w) || !(0 <= y && y < h)) {
            throw new IllegalArgumentException("x or y is outside of image");
        }
        if (x == 0 || x == w - 1 || y == 0 || y == h - 1) {
            return 1000.;
        }
        double deltaX = diff(picture.getRGB(x - 1, y), picture.getRGB(x + 1, y));
        double deltaY = diff(picture.getRGB(x, y - 1), picture.getRGB(x, y + 1));
        return Math.sqrt(deltaX + deltaY);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] energy = getEnergyMatrix();
        final double[][] dp = new double[w][h];
        final int[][] prevY = new int[w][h];

        for (int x = 1; x < w; x++) {
            for (int y = 0; y < h; y++) {
                prevY[x][y] = y;
                dp[x][y] = dp[x - 1][y] + energy[x][y];

                for (int dy = -1; dy <= 1; dy++) {
                    int yBefore = y + dy;
                    if (yBefore == -1 || yBefore == h) {
                        continue;
                    }

                    if (dp[x - 1][yBefore] + energy[x][y] < dp[x][y]) {
                        dp[x][y] = dp[x - 1][yBefore] + energy[x][y];
                        prevY[x][y] = yBefore;
                    }
                }
            }
        }
        int mnY = 0;
        for (int y = 0; y < h; y++) {
            if (dp[w - 1][y] < dp[w - 1][mnY]) {
                mnY = y;
            }
        }
        int[] seam = new int[w];
        int y = mnY;
        for (int x = w - 1; x >= 0; x--) {
            seam[x] = y;
            y = prevY[x][y];
        }
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        final double[][] energy = getEnergyMatrix();
        final double[][] dp = new double[w][h];
        final int[][] prevX = new int[w][h];

        for (int y = 1; y < h; y++) {
            for (int x = 0; x < w; x++) {
                prevX[x][y] = x;
                dp[x][y] = dp[x][y - 1] + energy[x][y];

                for(int dx = -1; dx <= 1; dx++) {
                   int xBefore = x + dx;
                   if (xBefore == -1 || xBefore == w) {
                       continue;
                   }
                   if (dp[xBefore][y - 1] + energy[x][y] < dp[x][y]) {
                       dp[x][y] = dp[xBefore][y - 1] + energy[x][y];
                       prevX[x][y] = xBefore;
                   }
                }
            }
        }
        int mnX = 0;
        for (int x = 0; x < w; x++) {
            if (dp[x][h-1] < dp[mnX][h - 1]) {
                mnX = x;
            }
        }
        int x = mnX;
        int[] seam = new int[h];
        for (int y = h - 1; y >= 0; y--) {
            seam[y] = x;
            x = prevX[x][y];
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam must be not null");
        }
        if (h <= 1) {
            throw new IllegalArgumentException("seam can't be removed");
        }
        if (seam.length != w) {
            throw new IllegalArgumentException("seam of wrong size");
        }
        for (int x = 0; x < w; x++) {
            if (seam[x] < 0 || seam[x] >= h) {
                throw new IllegalArgumentException("seam outside of pic");
            }
            if (x > 0 && Math.abs(seam[x] - seam[x - 1]) > 1) {
                throw new IllegalArgumentException("not valid seam");
            }
        }
        for (int x = 0; x < w; x++) {
            for (int y = seam[x]; y < h - 1; y++) {
                picture.setRGB(x, y, picture.getRGB(x, y + 1));
            }
        }
        h -= 1;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam must be not null");
        }
        if (w <= 1) {
            throw new IllegalArgumentException("seam can't be removed");
        }
        if (seam.length != h) {
            throw new IllegalArgumentException("seam of wrong size");
        }
        for (int y = 0; y < h; y++) {
            if (seam[y] < 0 || seam[y] >= w) {
                throw new IllegalArgumentException("seam outside of pic");
            }
            if (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1) {
                throw new IllegalArgumentException("not valid seam");
            }
        }
        for (int y = 0; y < h; y++) {
            for (int x = seam[y]; x < w - 1; x++) {
                picture.setRGB(x, y, picture.getRGB(x + 1, y));
            }
        }
        w -= 1;
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

    private int diff(int rgb1, int rgb2) {
        int b1 = rgb1 & 0xff;
        int g1 = (rgb1 >> 8)  & 0xff;
        int r1 = (rgb1 >> 16) & 0xff;

        int b2 = rgb2 & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;

        return (b2 - b1) * (b2 - b1) + (g2 - g1) * (g2 - g1) + (r2 - r1) * (r2 - r1);
    }

    private double[][] getEnergyMatrix() {
        final double[][] energyMatrix = new double[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                energyMatrix[x][y] = energy(x, y);
            }
        }
        return energyMatrix;
    }
}