package com.polytech.devintandroid;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tom Guillermin
 *
 */
public class GameShape {
	
	private Triangle t1, t2;
	private int width, previousWidth, height;
	private boolean leftSide;
	private int originX, originY;
	private List<Triangle> triangles;
	
	public GameShape(int width, int previousWidth, int originX, int originY, int height, boolean left) {
		leftSide = left;
		
		this.width = width;
		this.previousWidth = previousWidth;
		this.height = height;
		
		if (! left) {
			width = -width;
			previousWidth = -previousWidth;
		}
		
		t1 = new Triangle(
				new mPoint(0, 0),				// Bordure
				new mPoint(previousWidth, 0),	// Bordure
				new mPoint(width, -height));	// Centre
		t2 = new Triangle(
				new mPoint(0, 0),				// Bordure
				new mPoint(width, -height),		// Centre
				new mPoint(0, -height));		// Centre
		
		t1.translate(originX, originY);
		t2.translate(originX, originY);
		
		triangles = new LinkedList<Triangle>();
		triangles.add(t1);
		triangles.add(t2);
	}
	
	public void translate(int x, int y) {
		List<mPoint> points1 = t1.getPoints(),
				points2 = t2.getPoints();
		// do nothing for point1[0]
		points1.get(0).translate(0, y);
		points1.get(1).translate(x, y);
		points1.get(2).translate(x, y);
		
		points2.get(0).translate(0, y);
		points2.get(1).translate(x, y);
		points2.get(2).translate(0, y);
		
		if (leftSide) {
			width += x;
			previousWidth += x;
		}
		else {
			width -= x;
			previousWidth -= x;
		}
		
	}

	public int getWidth() {
		return width;
	}
	
	public int getPreviousWidth() {
		return previousWidth;
	}
	
	public String toString() {
		List<mPoint> p1 = t1.getPoints(),
				p2 = t2.getPoints();
		return p1.get(0)+", "+p1.get(1)+", "+p2.get(1)+", "+p2.get(2);
	}

	public int getHeight() {
		return height;
	}
	
	public int getOriginY() {
		return t1.getPoints().get(0).getY();
	}
	
	public List<mPoint> getPoints() {
		List<mPoint> list = new LinkedList<mPoint>();
		list.addAll(t1.getPoints());
		list.addAll(t2.getPoints());
		return list;
	}
	
	public List<Triangle> getTriangles() {
		return triangles;
	}
}
