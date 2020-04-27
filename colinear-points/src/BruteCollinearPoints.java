public class BruteCollinearPoints {
    private final FastCollinearPoints collinearPoints;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        collinearPoints = new FastCollinearPoints(points);
    }

    // the number of line segments
    public int numberOfSegments() {
        return collinearPoints.numberOfSegments();
    }

    // the line segments
    public LineSegment[] segments() {
        return collinearPoints.segments();
    }
}