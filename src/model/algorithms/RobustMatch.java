package model.algorithms;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Event.EventType;
import model.algorithms.utils.Block;

public abstract class RobustMatch extends ICopyMoveDetection {

	protected float QUANT[][] = new float[][] {
			{ 32.0f, 27.5f, 25.0f, 40.0f, 60.0f, 100.0f, 127.5f, 152.5f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 30.0f, 30.0f, 35.0f, 47.5f, 65.0f, 145.0f, 150.0f, 137.5f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 35.0f, 32.5f, 40.0f, 60.0f, 100.0f, 142.5f, 172.5f, 140.0f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 35.0f, 42.5f, 55.0f, 72.5f, 127.5f, 217.5f, 200.0f, 155.0f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 45.0f, 55.0f, 92.5f, 140.0f, 170.0f, 272.5f, 257.5f, 192.5f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 60.0f, 87.5f, 137.5f, 160.0f, 202.5f, 260.0f, 282.5f, 230.0f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 122.5f, 160.0f, 195.0f, 217.5f, 257.5f, 302.5f, 300.0f, 252.5f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 180.0f, 230.0f, 237.5f, 245.0f, 280.0f, 250.0f, 257.5f, 247.5f,
					152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
					152.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f },
			{ 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
					247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
					247.5f } };

	protected boolean abort = false;
	protected long oldtime = 0;
	protected List<Block> dcts = new ArrayList<Block>();
	protected int height, width;

	public void abort() {
		abort = true;
		setChanged();
		notifyObservers(new Event(EventType.ABORT,
				"The detection was aborted successfully!"));
	}

	protected int[][] getGrayscale(BufferedImage input) {
		int[][] grayscale = new int[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = input.getRGB(x, y);
				grayscale[y][x] = (int) (((pixel >> 16) & 0xff) * 0.299
						+ ((pixel >> 8) & 0xff) * 0.587 + ((pixel) & 0xff) * 0.114) - 128;
			}
		}
		return grayscale;
	}
	
	protected boolean checkImage(BufferedImage image) {
		if (image.getWidth() < 16 || image.getHeight() < 16) {
			return false;
		}
		return true;
	}

	protected double getVLenght(int x, int y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	protected long takeTime() {
		if (oldtime != 0) {
			long ret = System.currentTimeMillis() - oldtime;
			oldtime = System.currentTimeMillis();
			return ret;
		} else {
			oldtime = System.currentTimeMillis();
			return -1;
		}
	}

}
