/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author James
 */
import java.awt.Color;
import java.lang.Math;

public class KdTree {

    /**
     * The root of the tree.
     *
     * The root is the node for which every other node is contained in a
     * subtree.
     */
    private Node root;
    private int size = 0;
    double lineTop, lineBottom, lineLeft, lineRight;
    double candidateLine;

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    /**
     * A Node consists of a key-value pair, links(possibly null valued) to the
     * left and right subtrees, the number of nodes in the largest subtree
     * included for performance, and a reference to the colour of the link to
     * the parent Node.
     */
    private class Node {

        public final Point2D p;
        public Node up;
        public Node down;
        public final boolean orientation;
        // The line variable is a point, that represents a line. This is because
        // the RectHV class has no method for finding the distance to a line,
        // but does have a methhod for finding the distance to a point.
        // public Point2D line;

        public Node(Point2D p, Node up, Node down, boolean orientation) {

            this.p = p;
            this.up = up;
            this.down = down;
            this.orientation = orientation;

        }

    }

    /**
     *
     * Construct an empty set of points
     */
    public KdTree() {

        root = null;
        size = 0;
    }

    /**
     *
     * is the set empty?
     *
     * @return
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     *
     * The number of points in the set.
     *
     * @return
     */
    public int size() {

        if (isEmpty()) {
            return 0;
        }
        return size;
    }

    /**
     *
     * Add the point to the set (if it is not already in the set).
     */
    public void insert(Point2D p) {
        root = insert(root, p, HORIZONTAL);
    }

    private Node insert(Node node, Point2D p, boolean orientation) {
        if (node == null) {
            return new Node(p, null, null, orientation);
        }
        if (node.orientation == HORIZONTAL) {

            if (p.y() < node.p.y()) {
                node.down = insert(node.down, p, VERTICAL);
            } else if (p.y() > node.p.y()) {
                node.up = insert(node.up, p, VERTICAL);
            }

        } else {
            assert node.orientation == VERTICAL;
            if (p.x() < node.p.x()) {
                node.down = insert(node.down, p, HORIZONTAL);
            } else if (p.x() > node.p.x()) {
                node.up = insert(node.up, p, HORIZONTAL);
            }

        }

        return node;
    }

    /**
     * Does the set contain point p?
     *
     * @param p
     * @return
     */
    public boolean contains(Point2D p) {

        return contains(root, p);
    }

    private boolean contains(Node node, Point2D p) {
        if (node == null) {
            return false;
        }
        if (node.orientation == HORIZONTAL) {

            if (p.y() < node.p.y()) {
                return contains(node.down, p);
            } else if (p.y() > node.p.y()) {
                return contains(node.up, p);
            } else if (p.equals(node.p)) {
                return true;
            } else {
                return contains(node.up, p);
            }

        } else {
            assert node.orientation == VERTICAL;
            if (p.x() < node.p.x()) {
                return contains(node.down, p);
            } else if (p.x() > node.p.x()) {
                return contains(node.up, p);
            } else if (p.equals(node.p)) {
                return true;
            } else {
                return contains(node.up, p);
            }

        }
    }

    /**
     * Draw all points to standard draw.
     */
    public void draw() {
        draw(root);

    }

    private void draw(Node node) {
        StdDraw.setPenColor();
        node.p.draw();
        StdDraw.setPenColor(Color.blue);
        if (node.orientation == HORIZONTAL) {
            StdDraw.line(-1, node.p.y(), 1, node.p.y());
        } else {
            assert node.orientation == VERTICAL;
            StdDraw.line(node.p.x(), -1, node.p.x(), 1);
        }

        if (node.up != null) {
            draw(node.up);
        }
        if (node.down != null) {
            draw(node.down);
        }
    }

    /**
     * Iterable object containing all points inside the rectangle
     *
     * @param rect
     * @return
     */
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> queue;
        queue = new Queue<>();
        range(root, queue, rect);
        return queue;
    }

    private void range(Node node, Queue<Point2D> queue, RectHV rect) {

        if (node == null) {
            return;
        }

        if (node.orientation == HORIZONTAL) {
            if (rect.contains(node.p)) {
                queue.enqueue(node.p);
                lineTop = rect.ymax();
                lineBottom = rect.ymin();
            }
            if (node.p.y() >= rect.ymin()
                    && rect.distanceSquaredTo(node.p) < lineTop - rect.ymax()) {

                candidateLine = Math.min(node.p.y(), lineTop);
                lineTop = Math.max(candidateLine, rect.ymax());
                range(node.down, queue, rect);
            }
            if (node.p.y() <= rect.ymax()
                    && rect.distanceSquaredTo(node.p)
                    < rect.ymin() - lineBottom) {
                candidateLine = Math.max(node.p.y(), lineBottom);
                lineBottom = Math.min(candidateLine, rect.ymin());
                range(node.up, queue, rect);
            }

        } else {
            assert node.orientation == VERTICAL;
            if (rect.contains(node.p)) {
                queue.enqueue(node.p);
                lineLeft = rect.xmin();
                lineRight = rect.xmax();
            }
            if (node.p.x() >= rect.xmin()
                    && rect.distanceSquaredTo(node.p)
                    < lineRight - rect.xmax()) {
                candidateLine = Math.min(node.p.x(), lineRight);
                lineRight = Math.max(node.p.x(), rect.xmax());
                range(node.down, queue, rect);
            }
            if (node.p.x() <= rect.xmax()
                    && rect.distanceSquaredTo(node.p)
                    < rect.xmin() - lineLeft) {
                candidateLine = Math.max(node.p.x(), lineLeft);
                lineLeft = Math.min(node.p.x(), rect.xmin());

                range(node.up, queue, rect);
            }

        }

    }

    /**
     * A nearest neighbour to point p. Null if the set is empty.
     *
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p) {

        return null;
    }

}
