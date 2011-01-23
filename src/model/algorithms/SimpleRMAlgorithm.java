package model.algorithms;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import model.Event;
import model.Result;
import model.ShiftVector;
import model.Event.EventType;
import model.algorithms.utils.Block;
import model.algorithms.utils.DCTWorkerpool;

public class SimpleRMAlgorithm extends ICopyMoveDetection {

	@Override
	public void abort() {
		setChanged();
		notifyObservers(new Event(EventType.ABORT,
				"The detection was aborted successfully!"));
	}

	@Override
	public void detect(BufferedImage input, float quality, int threshold,
			int threads) {

		if (!checkImage(input)) {
			setChanged();
			notifyObservers(new Event(Event.EventType.ERROR,
					"The image could not be processed!"));
			return;
		}

		long start = System.currentTimeMillis();
		takeTime();
		int height = input.getHeight();
		int width = input.getWidth();

		/*
		 * The image can be processed. First calc grayscale image...
		 */
		int[] grayscale = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = input.getRGB(x, y);
				grayscale[y * width + x] = (int) (((pixel >> 16) & 0xff)
						* 0.299 + ((pixel >> 8) & 0xff) * 0.587 + ((pixel) & 0xff) * 0.114) - 128;
			}
		}

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Luminance matrix of image calculated in " + takeTime() + "ms"));
		List<Block> dcts = new LinkedList<Block>();

		/*
		 * Calculate the dcts of each block...
		 */
		for (int yy = 0; yy < width - 17; yy++) {
			for (int xx = 0; xx < height - 17; xx++) {
				float[] dct = new float[256];
				
				for (int u = 0; u < 16; u++) {
					for (int v = 0; v < 16; v++) {
						float alphau = (float) (u == 0 ? 1 / Math.sqrt(16)
								: 1 / Math.sqrt(32));
						float alphav = (float) (v == 0 ? 1 / Math.sqrt(16)
								: 1 / Math.sqrt(32));
						dct[u * 16 + v] = 0f;
						for (int i = 0; i < 16; i++) {
							for (int j = 0; j < 16; j++) {
								dct[u * 16 + v] += grayscale[(xx + i) * width + yy
										+ j]
										* Math.cos((Math.PI * (2 * i + 1) * u) / 32)
										* Math.cos((Math.PI * (2 * j + 1) * v) / 32);
							}
						}
					}
				}
				dcts.add(new Block(dct, xx, yy));
			}
			
			setChanged();
			notifyObservers(new Event(EventType.PROGRESS, new Result((float)yy / (float)(width - 17) )));
		}
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"DCT of each block was calculated in " + takeTime() + "ms"));

		/*
		 * Sort the dcts lexicographically...
		 */
		Collections.sort(dcts);
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Lexicographically sorted all DCTs in " + takeTime() + "ms"));

		/*
		 * Collect vectors... The Shift Vector is an array double of the
		 * original image size. The position in the array identifies the vector
		 * itself, the value at a given position represents the count of the
		 * shift vector.
		 */
		int shiftVectors[] = new int[width * height * 2];
		for (int i = 0; i < dcts.size() - 1; i++) {
			Block b1 = dcts.get(i);
			Block b2 = dcts.get(i + 1);
			if (b1.compareTo(b2) == 0) {
				int sx = b1.getPos_x() - b2.getPos_x();
				int sy = b1.getPos_y() - b2.getPos_y();

				if (sx < 0) {
					sx = -sx;
					sy = -sy;
				}

				/*
				 * This has to be done because sy may be negative...
				 */
				sy += height;

				shiftVectors[sy * width + sx]++;
			}
		}

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Calculated the shift vectors in " + takeTime() + "ms"));

		/*
		 * Save time that will be notified along with the result of the
		 * shiftvectors
		 */
		long end = System.currentTimeMillis();

		Event event = new Event(EventType.COPY_MOVE_DETECTION_FINISHED,
				new Result(end - start));
		/*
		 * Mark the detected copies...
		 */
		for (int i = 0; i < dcts.size() - 1; i++) {
			Block b1 = dcts.get(i);
			Block b2 = dcts.get(i + 1);
			if (b1.compareTo(b2) == 0) {
				int sx = b1.getPos_x() - b2.getPos_x();
				int sy = b1.getPos_y() - b2.getPos_y();
				if (sx < 0) {
					sx = -sx;
					sy = -sy;
				}

				/*
				 * This has to be done because sy may be negative...
				 */
				sy += height;

				if (shiftVectors[sy * width + sx] > threshold) {
					event.getResult().addShiftVector(
							new ShiftVector(b1.getPos_x(), b1.getPos_y(), -b1
									.getPos_x() + b2.getPos_x(), -b1.getPos_y()
									+ b2.getPos_y(), DCTWorkerpool.BLOCK_SIZE));
				}
			}
		}

		setChanged();
		notifyObservers(event);
	}

	/**
	 * Checks if the copy move detection can be performed on the given image.
	 * 
	 * @param image
	 * @return
	 */
	private boolean checkImage(BufferedImage image) {
		if (image.getWidth() < 16 || image.getHeight() < 16) {
			return false;
		}
		return true;
	}

	private long oldtime = 0;

	private long takeTime() {
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