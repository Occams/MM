package model.algorithms;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import model.Event;
import model.Result;
import model.ShiftVector;
import model.Event.EventType;
import model.algorithms.utils.Block;
import model.algorithms.utils.DCTWorkerpool;
import model.algorithms.utils.QuickSort;

public class RobustMatchPartialDCT extends RobustMatch implements Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null && arg1 instanceof Block) {
			synchronized (dcts) {
				dcts.add((Block) arg1);
			}

			if (dcts.size() % (height - 15) == 0) {
				setChanged();
				notifyObservers(new Event(EventType.PROGRESS, new Result(
						(float) dcts.size()
								/ (float) ((width - 15) * (height - 15)))));
			}
		}
	}

	@Override
	public void detect(BufferedImage input, float quality, int threshold,
			int minLength, int threads) {

		/* Check if image is at least 16x16 in size */
		if (!checkImage(input)) {
			setChanged();
			notifyObservers(new Event(Event.EventType.ERROR,
					"The image could not be processed!"));
			return;
		}
		
		width = input.getWidth();
		height = input.getHeight();
		long start = System.currentTimeMillis();
		takeTime();

		/* Compute grayscale values */
		final int[][] grayscale = getGrayscale(input);
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Luminance matrix of image calculated in " + takeTime() + "ms"));

		/*
		 * Precompute values needed for DCT computation and update the
		 * quantization matrix according to quality
		 */
		final float[][][][] constants = preComputeConstants(quality);
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Precomputed constant values in " + takeTime() + "ms"));

		/* Calculate 2x2 DCTs of each block */
		float[][] blocks = new float[(width - 15) * (height - 15)][256];
		boolean[] flagged = new boolean[blocks.length];

		Thread[] tA = new Thread[threads];

		for (int t = 0; t < threads; t++) {
			Worker w = new Worker(t, threads, width, height, grayscale,
					constants, blocks, flagged, 0, 4);
			w.addObserver(this);
			Thread th = new Thread(w);
			tA[t] = th;
			th.start();
		}

		/* Barrier */
		for (int t = 0; t < threads; t++) {
			try {
				tA[t].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"2x2 DCTs of each block were calculated in " + takeTime()
						+ "ms"));

		/*
		 * Sort the 2x2 DCTs of each block.
		 */
		QuickSort.sort(blocks);
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Lexicographically sorted all 2x2 DCTs in " + takeTime() + "ms"));
		
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Number of DCT blocks: " + blocks.length));

		/* Mark unique elements */
		for (int i = 0; i < blocks.length - 1; i++) {
			if (compareDCT(blocks[i], blocks[i + 1]) != 0) {
				// flagged[i] = true;
			}
		}

		int c = 0;
		for (int i = 0; i < blocks.length - 1; i++) {
			if (flagged[i])
				c++;
		}

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Number of unique DCT blocks (" + c + ") computed in "
						+ takeTime() + " ms"));

		/* Calculate 16x16 DCTs of non-unique blocks */
		for (int t = 0; t < threads; t++) {
			Worker w = new Worker(t, threads, width, height, grayscale,
					constants, blocks, flagged, 5, 16);
			w.addObserver(this);
			Thread th = new Thread(w);
			tA[t] = th;
			th.start();
		}

		/* Barrier */
		for (int t = 0; t < threads; t++) {
			try {
				tA[t].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"16x16 DCTs of non-unique blocks were calculated in "
						+ takeTime() + "ms"));

		/* Sort the 16x16 DCTs of each block */
		QuickSort.sort(blocks);
		System.out.println(22342);
		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Lexicographically sorted all non-unique 16x16 DCTs in "
						+ takeTime() + "ms"));

		/*
		 * Collect vectors... The Shift Vector is an array double of the
		 * original image size. The position in the array identifies the vector
		 * itself, the value at a given position represents the count of the
		 * shift vector.
		 */
		int shiftVectors[][] = new int[height * 2][width];

		for (int i = 0; i < blocks.length - 1; i++) {
			float[] a = blocks[i];
			float[] b = blocks[i + 1];

			if (!flagged[i] && compareDCT(a, b) == 0) {
				int aBy = i / width, aBx = i % width;
				int bBy = (i + 1) / width, bBx = (i + 1) % width;
				int sx = aBx - bBx;
				int sy = aBy - bBy;

				if (getVLenght(sx, sy) >= minLength) {

					if (sx < 0) {
						sx = -sx;
						sy = -sy;
					}

					/*
					 * This has to be done because sy may be negative...
					 */
					sy += height;

					shiftVectors[sy][sx]++;
				}
			}
		}

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Calculated the shift vectors in " + takeTime() + "ms"));

		Event event = new Event(EventType.COPY_MOVE_DETECTION_FINISHED,
				new Result(System.currentTimeMillis() - start));

		/*
		 * Collect shiftvectors
		 */
		for (int i = 0; i < blocks.length - 1; i++) {
			float[] a = blocks[i];
			float[] b = blocks[i + 1];

			if (!flagged[i] && compareDCT(a, b) == 0) {
				int aBy = i / width, aBx = i % width;
				int bBy = (i + 1) / width, bBx = (i + 1) % width;
				int sx = aBx - bBx;
				int sy = aBy - bBy;

				if (getVLenght(sx, sy) >= minLength) {
					if (sx < 0) {
						sx = -sx;
						sy = -sy;
					}

					/*
					 * This has to be done because sy may be negative...
					 */
					sy += height;

					if (shiftVectors[sy][sx] > threshold) {
						event.getResult().addShiftVector(
								new ShiftVector(aBy, aBy, bBx - aBx, bBy - aBy,
										DCTWorkerpool.BLOCK_SIZE));
					}
				}
			}
		}

		setChanged();
		notifyObservers(event);
	}

	private class Worker extends Observable implements Runnable {
		private final int num, threads, width, height, grayscale[][], dctStart,
				dctEnd;
		private final float[][][][] constants;
		private float[][] blocks;
		private boolean[] flagged;

		public Worker(final int num, final int threads, final int width,
				final int height, final int grayscale[][],
				final float[][][][] constants, float[][] blocks,
				boolean[] flagged, final int dctStart, final int dctEnd) {
			this.num = num;
			this.threads = threads;
			this.width = width;
			this.height = height;
			this.grayscale = grayscale;
			this.constants = constants;
			this.dctStart = dctStart;
			this.dctEnd = dctEnd;
			this.blocks = blocks;
			this.flagged = flagged;

		}

		@Override
		public void run() {

			for (int yy = num; yy < width - 15 && !abort; yy += threads) {
				for (int xx = 0; xx < height - 15; xx++) {
					int idx = xx * (width - 15) + yy;

					if (!flagged[idx]) {
						
						for (int u = dctStart; u < dctEnd; u++) {
							for (int v = dctStart; v < dctEnd; v++) {
								
								float f = 0f;
								for (int i = 0; i < 16; i++) {
									for (int j = 0; j < 16; j++) {
										f += grayscale[(xx + i)][yy + j]
												* constants[u][v][i][j];
									}
								}

								blocks[idx][u * 16 + v] = (float) Math.round(f
										/ QUANT[u][v]);
							}
						}
					}
				}
			}

		}
	}

	private int compareDCT(final float[] a, final float[] b) {
		for (int i = 0; i < 256; i++) {
			if (a[i] < b[i]) {
				return -1;
			} else if (a[i] > b[i]) {
				return 1;
			}
		}

		return 0;
	}

	private void bubblesortBlocks(float[][] blocks) {
		int n = blocks.length;
		boolean change = false;

		do {
			
			change = false;
			for (int i = 0; i < n - 1; i++) {
				if (compareDCT(blocks[i], blocks[i + 1]) > 0) {
					change = true;
					float[] tmp = blocks[i];
					blocks[i] = blocks[i + 1];
					blocks[i + 1] = tmp;
				}
			}

			n--;
			System.out.println(n);
		} while (n > 1 && change);
	}

}
