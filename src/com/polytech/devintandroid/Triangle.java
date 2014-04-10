package com.polytech.devintandroid;

import java.util.LinkedList;
import java.util.List;
/**
 * 
 * @author Tom Guillermin
 *
 */
public class Triangle {
	private mPoint p1, p2, p3;
	private List<mPoint> points;
	
	public Triangle(mPoint p1, mPoint p2, mPoint p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		points = new LinkedList<mPoint>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
	}
	
	public void translate(int x, int y) {
		for (mPoint p : points) {
			p.tourne(x);
			p.monte(y);
		}
	}
	
	public List<mPoint> getPoints() {
		return points;
	}
}
