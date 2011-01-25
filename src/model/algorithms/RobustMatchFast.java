package model.algorithms;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import model.Event;
import model.Event.EventType;
import model.Result;
import model.ShiftVector;
import model.algorithms.utils.Block;
import model.algorithms.utils.DCTWorkerpool;

public class RobustMatchFast extends RobustMatch implements Observer {

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
	public void detect(BufferedImage input, float quality, int threshold, int threads) {

		if (!checkImage(input)) {
			setChanged();
			notifyObservers(new Event(Event.EventType.ERROR,
					"The image could not be processed!"));
			return;
		}

		long start = System.currentTimeMillis();
		takeTime();
		height = input.getHeight();
		width = input.getWidth();

		/*
		 * The image can be processed. First calc grayscale image...
		 */
		final int[][] grayscale = getGrayscale(input);

		setChanged();
		notifyObservers(new Event(Event.EventType.STATUS,
				"Luminance matrix of image calculated in " + takeTime() + "ms"));

		final float[][][][] constants = new float[16][16][16][16];

		for (int u = 0; u < 16; u++) {
			float alphau = (float) (u == 0 ? Math.sqrt(1.0f / 16.0f) : Math.sqrt(2.0f / 16.0f));
			for (int v = 0; v < 16; v++) {
				float alphav = (float) (v == 0 ? Math.sqrt(1.0f / 16.0f) : Math.sqrt(2.0f / 16.0f));
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						constants[u][v][i][j] = (float) (alphau * alphav
								* Math.cos((Math.PI * ( i + 0.5f) * u) / 16.0f) * Math
								.cos((Math.PI * (i * j + 0.5f) * v) / 16.0f));
					}
				}
				QUANT[u][v] *= quality;
			}
		}

		List<Block> refineList = new ArrayList<Block>();
		int amount = 1;
		do {
			amount *= 4;
			
			/*
			 * Calculate the dcts of each block...
			 */
			Thread[] tA = new Thread[threads];

			for (int t = 0; t < threads; t++) {
				Worker w = new Worker(t, threads, width, height, grayscale,
						constants, amount, refineList);
				w.addObserver(this);
				Thread th = new Thread(w);
				tA[t] = th;
				th.start();
			}

			for (int t = 0; t < threads; t++) {
				try {
					tA[t].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			setChanged();
			notifyObservers(new Event(Event.EventType.STATUS,
					amount+"x"+amount+"-DCT of each block was calculated in " + takeTime() + "ms"));

			/*
			 * Sort the dcts lexicographically...
			 */
			Collections.sort(dcts);

			/*
			 * Do same elements into the refine list...
			 */
			refineList.clear();
			int putLast = -1;
			for (int i = 0; i < dcts.size() - 1; i++) {
				if (dcts.get(i).compareTo(dcts.get(i + 1)) == 0) {
					if (putLast != i) {
						refineList.add(dcts.get(i));
					}
					refineList.add(dcts.get(i + 1));
					putLast = i + 1;
				}
			}

			setChanged();
			notifyObservers(new Event(Event.EventType.STATUS,
					"Lexicographically sorted all "+amount+"x"+amount+"-DCTs in " + takeTime() + "ms"));

		} while (amount < 16 || refineList.isEmpty());

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
								new ShiftVector(b1.getPos_x(), b1.getPos_y(),
										-b1.getPos_x() + b2.getPos_x(), -b1
												.getPos_y() + b2.getPos_y(),
										DCTWorkerpool.BLOCK_SIZE));
					}
			}
		}

		setChanged();
		notifyObservers(event);
	}

	private class Worker extends Observable implements Runnable {
		private final int num, threads, width, height, grayscale[][], amount;
		private final float[][][][] constants;
		private final List<Block> blocks;

		public Worker(final int num, final int threads, final int width,
				final int height, final int grayscale[][],
				final float[][][][] constants, final int amount,
				List<Block> refine) {
			this.num = num;
			this.threads = threads;
			this.width = width;
			this.height = height;
			this.grayscale = grayscale;
			this.constants = constants;
			this.amount = amount;
			this.blocks = refine;
		}

		@Override
		public void run() {

			if (blocks.isEmpty()) {
				for (int yy = num; yy < width - 15 && !abort; yy += threads) {

					for (int xx = 0; xx < height - 15; xx++) {
						float[] dct = new float[256];

						for (int u = 0; u < amount; u++) {
							for (int v = 0; v < amount; v++) {
								float f = 0f;
								for (int i = 0; i < 16; i++) {
									for (int j = 0; j < 16; j++) {
										f += grayscale[(xx + i)][yy + j]
												* constants[u][v][i][j];
									}
								}

								dct[u * 16 + v] = (float) Math.rint(f
										/ QUANT[u][v]);
							}
						}

						setChanged();
						notifyObservers(new Block(dct, yy, xx));
					}
				}
			} else {
				/*
				 * We should refine specific blocks
				 */
				for (int n = num; n < blocks.size(); n += threads) {
					Block b = blocks.get(n);
					float[] dct = blocks.get(n).getValues();
					int xx = b.getPos_x();
					int yy = b.getPos_y();

					for (int u = amount/2; u < amount; u++) {
						for (int v = amount/2; v < amount; v++) {
							float f = 0f;
							for (int i = 0; i < 16; i++) {
								for (int j = 0; j < 16; j++) {
										f += grayscale[(yy + i)][xx + j]
												* constants[u][v][i][j];
								}
							}

							dct[u * 16 + v] = (float) Math.rint(f
									/ QUANT[u][v]);
						}
					}

					b.setValues(dct);
				}
			}
		}
	}

}
