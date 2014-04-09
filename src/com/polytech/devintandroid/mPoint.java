package com.polytech.devintandroid;

/**
 * 
 * @author Fabien Pinel
 *
 */
public class mPoint {
	private int x,y;
	public mPoint(int x, int y){
		this.setX(x);
		this.setY(y);
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void translate(int x, int y) {
		this.x += x;
		this.y += y;
	}
	public void translateX(int x) {
		this.x += x;
	}
	public void translateY(int y) {
		this.x += y;
	}
	
	public void monte(int footo){
		this.setY(this.y+footo);
	}
	public void tourne(int i) {
		this.setX(this.getX()+i);

	}
	
	public String toString() {
		return "("+x+","+ y+")";
	}
}
