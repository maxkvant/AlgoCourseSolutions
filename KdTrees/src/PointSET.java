import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {
    private final SET<Point2D> points = new SET<>();

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        validateArg(p);
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        validateArg(p);
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D point: points) {
            StdDraw.circle(point.x(), point.y(), 0.01);
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validateArg(rect);
        final Stack<Point2D> result = new Stack<>();
        for (Point2D point: points) {
            if (rect.contains(point)) {
                result.push(point);
            }
        }
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        validateArg(p);
        if (isEmpty()) {
            return null;
        }
        Point2D minDistPoint = points.min();
        for (Point2D point: points) {
            if (p.distanceSquaredTo(point) < p.distanceSquaredTo(minDistPoint)) {
                minDistPoint = point;
            }
        }
        return minDistPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        final PointSET pointSET = new PointSET();
        pointSET.insert(new Point2D(0.5, 0.3));
        pointSET.draw();
    }

    private void validateArg(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("arguments must be not null");
        }
    }
}