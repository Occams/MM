package model.algorithms.utils;

import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DCTWorker extends Observable implements Runnable {

	private ConcurrentLinkedQueue<Integer> queue;
	private int width;
	private int[] image;
	private float quality;
	private boolean abort;

	public DCTWorker(ConcurrentLinkedQueue<Integer> queue, int[] input,
			int width, float quality) {
		this.queue = queue;
		this.width = width;
		this.quality = quality;
		image = input;
		abort = false;
	}

	public void run() {
		while (!queue.isEmpty() && !abort) {
			int b_num = queue.poll();
			int b_y = b_num / (width - DCTWorkerpool.BLOCK_SIZE + 1);
			int b_x = b_num % (width - DCTWorkerpool.BLOCK_SIZE + 1);

			float[][] matrix = dct(b_x, b_y);
			setChanged();
			notifyObservers(new Block(matrix, b_x, b_y));
		}
	}

	public void abort() {
		abort = true;
	}

	/**
	 * Computes the dct of the given block...
	 * 
	 * @param offs_x
	 * @param offs_y
	 * @param dct_quant
	 *            The DCT quantisation matrix prescaled with the quality
	 *            parameter.
	 */
	private float[][] dct(int offs_x, int offs_y) {
		int bsize = DCTWorkerpool.BLOCK_SIZE;
		float dct[][] = new float[bsize][bsize];
		float tmp[][] = new float[bsize][bsize];
		float f;

		for (int y = 0; y < bsize; y++) {
			for (int x = 0; x < bsize; x++) {
				f = 0;
				for (int i = 0; i < bsize; i++) {
					f += DCTWorkerpool.DCT[y * bsize + i]
							* image[(offs_y + i) * width + offs_x + x];
				}
				tmp[y][x] = f;
			}
		}

		for (int y = 0; y < bsize; y++) {
			for (int x = 0; x < bsize; x++) {
				f = 0;
				for (int i = 0; i < bsize; i++) {
					f += tmp[y][i] * DCTWorkerpool.DCT[i * bsize + x];
				}

				// Quantise the dct coefficient
				dct[y][x] = (float)(f
						/ (DCTWorkerpool.QUANT[y * bsize + x] * quality));
			}
		}
		

		return dct;
	}

}
