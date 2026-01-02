package models;

import java.util.ArrayList;

public class Shape {
    private ArrayList<Point> points;

    // Constructor
    public Shape() {
        points = new ArrayList<>();
    }

    // Add a point to the shape
    public void addPoint(Point p) {
        points.add(p);
    }

    // Calculate the perimeter
    public double calculatePerimeter() {
        if (points.size() < 2) return 0;
        double perimeter = 0;
        for (int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size()); // wrap around
            perimeter += current.distance(next);
        }
        return perimeter;
    }

    // Average length of sides
    public double getAverageSide() {
        if (points.size() < 2) return 0;
        return calculatePerimeter() / points.size();
    }

    // Longest side
    public double getLongestSide() {
        if (points.size() < 2) return 0;
        double longest = 0;
        for (int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size());
            double dist = current.distance(next);
            if (dist > longest) longest = dist;
        }
        return longest;
    }
}
