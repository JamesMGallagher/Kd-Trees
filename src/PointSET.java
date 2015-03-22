/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author James
 */
public class PointSET<Key extends Comparable<Key>, Value> {

    private final SET<Point2D> set;

    /**
     *
     * Construct an empty set of points
     */
    public PointSET() {
        set = new SET<Point2D>();

    }

    /**
     *
     * is the set empty?
     */
    public boolean isEmpty() {
        return set.isEmpty();

    }

    /**
     *
     * The number of points in the set.
     *
     * @return
     */
    public int size() {

        return set.size();
    }

    /**
     *
     * Add the point to the set (if it is not already in the set).
     *
     * @param p
     */
    public void insert(Point2D p) {
        if (set.contains(p)) {
            set.delete(p);
        }
        set.add(p);

    }

    /**
     * Does the set contain point p?
     *
     * @param p
     * @return
     */
    public boolean contains(Point2D p) {

        return set.contains(p);
    }

    /**
     * Draw all points to standard draw.
     */
    public void draw() {

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        for (Point2D p : set) {
            p.draw();
        }
    }

    /**
     * Iterable object containing all points inside the rectangle
     *
     * @param rect
     * @return
     */
    public Iterable<Point2D> range(RectHV rect) {

        Queue<Point2D> queue = new Queue<Point2D>();

        if (rect == null) {
            return queue;
        }
        for (Point2D p : set) {

            if (rect.contains(p)) {
                queue.enqueue(p);
            }
        }
        return queue;
    }

    /**
     * A nearest neighbour to point p. Null if the set is empty.
     *
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p) {

        if (set.size() < 2) {
            return null;
        }
        Point2D nearestPoint = set.min();
        for (Point2D q : set) {
            if (p.distanceSquaredTo(q) < p.distanceSquaredTo(nearestPoint)) {
                nearestPoint = p;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) {
        PointSET pointSet = new PointSET();

        Point2D A = new Point2D(0.5, 0.5);
        Point2D B = new Point2D(1, 0);

        pointSet.insert(A);
        pointSet.insert(B);
        pointSet.draw();

    }

}
