import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static final RectHV RECT = new RectHV(0., 0.,  1., 1.);

    private Node root = null;
    private int size = 0;

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        validateArg(p);
        root = insertHelper(root, p, true, RECT);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        validateArg(p);
        Node node = root;
        while (node != null) {
            if (node.point.equals(p)) {
                return true;
            } else if (node.toLeft(p)) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return false;
    }

    // draw all points to standard draw
    public void draw() {
        drawHelper(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validateArg(rect);
        final Stack<Point2D> result = new Stack<>();
        rangeHelper(root, rect, result);
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        validateArg(p);
        if (isEmpty()) {
            return null;
        }
        return nearestHelper(root, p, root.point);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        final KdTree kdTree = new KdTree();
        kdTree.insert(new Point2D(0.5, 0.3));
        kdTree.draw();
    }

    private Node insertHelper(Node node, Point2D p, boolean splitX, RectHV rect) {
        if (node == null) {
            size += 1;
            return new Node(p, splitX, rect);
        }
        if (node.point.equals(p)) {
            return node;
        }
        if (node.toLeft(p)) {
            node.left =  insertHelper(node.left, p, !splitX, node.leftRect());
        } else {
            node.right = insertHelper(node.right, p, !splitX, node.rightRect());
        }
        return node;
    }

    private void drawHelper(Node node) {
        if (node == null) {
            return;
        }
        StdDraw.circle(node.point.x(), node.point.y(), 0.01);
        if (node.splitX) {
            StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
        } else {
            StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
        }
        drawHelper(node.left);
        drawHelper(node.right);
    }

    private Point2D nearestHelper(Node node, Point2D p, Point2D curAnswer) {
        if (node == null) {
            return curAnswer;
        }
        double curDistSquared = p.distanceSquaredTo(curAnswer);

        if (node.rect.distanceSquaredTo(p) > curDistSquared) {
            return curAnswer;
        }

        if (p.distanceSquaredTo(node.point) < curDistSquared) {
            curAnswer = node.point;
        }

        if (node.toLeft(p)) {
            curAnswer = nearestHelper(node.left, p, curAnswer);
            curAnswer = nearestHelper(node.right, p, curAnswer);
        } else {
            curAnswer = nearestHelper(node.right, p, curAnswer);
            curAnswer = nearestHelper(node.left, p, curAnswer);
        }
        return curAnswer;
    }

    private void rangeHelper(Node node, RectHV range, Stack<Point2D> result) {
        if (node == null) {
            return;
        }
        if (!node.rect.intersects(range)) {
            return;
        }
        if (range.contains(node.point)) {
            result.push(node.point);
        }
        rangeHelper(node.left, range, result);
        rangeHelper(node.right, range, result);
    }

    private static class Node {
        private final Point2D point;
        private final RectHV rect;
        private final boolean splitX;

        private Node left;
        private Node right;

        public Node(Point2D point, boolean splitX, RectHV rect) {
            this.point = point;
            this.splitX = splitX;
            this.rect = rect;
        }

        public boolean toLeft(Point2D p) {
            if (splitX) {
                return p.x() < point.x();
            } else {
                return p.y() < point.y();
            }
        }

        public RectHV leftRect() {
            if (left != null) {
                return left.rect;
            }
            if (splitX) {
                return new RectHV(rect.xmin(), rect.ymin(), point.x(), rect.ymax());
            } else {
                return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), point.y());
            }
        }

        public RectHV rightRect() {
            if (right != null) {
                return right.rect;
            }
            if (splitX) {
                return new RectHV(point.x(), rect.ymin(), rect.xmax(), rect.ymax());
            } else {
                return new RectHV(rect.xmin(), point.y(), rect.xmax(), rect.ymax());
            }
        }
    }

    private void validateArg(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("arguments must be not null");
        }
    }
}
