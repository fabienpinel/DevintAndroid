package com.polytech.devintandroid;

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
	public void monte(int foot){
		this.setY(this.getY()+foot);
	}
	public void tourne(int i) {
		this.setX(this.getX()+i);;
		
	}
}
