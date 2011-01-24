package model.algorithms.utils;

public class Block implements Comparable<Block> {
	private float[][] values;
	private int pos_x;
	private int pos_y;

	public Block(float[][] vals, int x, int y) {
		this.values = vals;
		pos_x = x;
		pos_y = y;
	}

	public float[][] getValues() {
		return values;
	}

	public void setValues(float[][] values) {
		this.values = values;
	}

	public int getPos_x() {
		return pos_x;
	}

	public void setPos_x(int posX) {
		pos_x = posX;
	}

	public int getPos_y() {
		return pos_y;
	}

	public void setPos_y(int posY) {
		pos_y = posY;
	}

	@Override
	public int compareTo(Block o) {
		float[][] b1 = getValues();
		float[][] b2 = o.getValues();

		for (int i = 0; i < b1.length; i++) {
			for (int j = 0; j < b1[0].length; j++) {
				if (b1[i][j] < b2[i][j]) {
					return -1;
				} else if (b1[i][j] > b2[i][j]) {
					return 1;
				}
			}
		}
		
		return 0;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		for (float[] a : getValues()) {
			for (float f : a) {
				b.append(f);
				b.append(",");
			}
		}
		return b.toString();
	}

}
