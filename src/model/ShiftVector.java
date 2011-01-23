package model;

/**
 * @author huber
 * 
 */
public class ShiftVector {
	/**
	 * Start point x.
	 */
	private int sx;
	/**
	 * Start point y.
	 */
	private int sy;
	/**
	 * Delta x.
	 */
	private int dx;
	/**
	 * Delta y.
	 */
	private int dy;

	/**
	 * Block width and height.
	 */
	private int bs;

	public int getSx() {
		return sx;
	}

	public void setSx(int sx) {
		this.sx = sx;
	}

	public int getSy() {
		return sy;
	}

	public void setSy(int sy) {
		this.sy = sy;
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public int getBs() {
		return bs;
	}

	public void setBs(int bs) {
		this.bs = bs;
	}

	public ShiftVector(int sx, int sy, int dx, int dy, int bs) {
		this.sx = sx;
		this.sy = sy;
		this.dx = dx;
		this.dy = dy;
		this.bs = bs;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ShiftVector) {
			ShiftVector v = (ShiftVector) o;
			return dx == v.dx && dy == v.dy;
		} else {
			return false;
		}
	}
}