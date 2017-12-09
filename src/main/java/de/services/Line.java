package de.services;

/**
 * This class represents a line as a linear function
 * y = mx + b
 * 
 * @author simon_000
 *
 */
public class Line {
	// y = mx +b
	/**
	 * Slope
	 */
	private double m;
	/**
	 * y-Intercept
	 */
	private double b;
	// if the line is vertical
	/**
	 * Indicates if a line is a vertical
	 */
	private boolean vertical;
	/**
	 * x-Intercept (only necessary if vertical == true)
	 */
	private double x;

	// --------------------------
	// Getter and Setter methods
	// --------------------------

	public boolean isVertical() {
		return vertical;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
}
