import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FastCollinearPoints {
    private final LineSegment[] lineSegments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("points must be not null");
        }

        int n = points.length;
        for (int i = 0; i < n; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("each point must be non null");
            }
        }

        final Point[] ps = new Point[n];
        for (int i = 0; i < n; i++) {
            ps[i] = points[i];
        }
        Arrays.sort(ps);

        for (int i = 0; i < n - 1; i++) {
            if (ps[i].compareTo(ps[i + 1]) == 0) {
                throw new IllegalArgumentException("there must be no repeated points");
            }
        }

        final List<LineSegment> segments = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            final Comparator<Point> slopeOrder = ps[i].slopeOrder();
            Arrays.sort(ps, slopeOrder);

            int l = 1;
            int r = 1;
            while (l < n) {
                Point minPoint = ps[0];
                Point maxPoint = ps[0];
                while (r < n && slopeOrder.compare(ps[l],  ps[r]) == 0) {
                    if (ps[r].compareTo(minPoint) < 0) {
                        minPoint = ps[r];
                    }
                    if (ps[r].compareTo(maxPoint) > 0) {
                        maxPoint = ps[r];
                    }
                    r += 1;
                }
                if (r - l >= 3 && minPoint.compareTo(ps[0]) == 0) {
                    segments.add(new LineSegment(minPoint, maxPoint));
                }
                l = r;
            }
            Arrays.sort(ps);
        }
        final LineSegment[] lineSegments = new LineSegment[segments.size()];
        segments.toArray(lineSegments);
        this.lineSegments = lineSegments;
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(lineSegments, lineSegments.length);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}