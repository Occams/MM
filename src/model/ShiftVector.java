package model;

/**
 * @author huber
 * 
 */
public class ShiftVector {
	/**
	 * Start point x.
	 */
	public int sx;
	/**
	 * Start point y.
	 */
	public int sy;
	/**
	 * Delta x.
	 */
	public int dx;
	/**
	 * Delta y.
	 */
	public int dy;

	/**
	 * Block width and height.
	 */
	public int bs;

	public ShiftVector(int sx, int sy, int dx, int dy, int bs) {
		this.sx = sx;
		this.sy = sy;
		this.dx = dx;
		this.dy = dy;
		this.bs = bs;
	}
}