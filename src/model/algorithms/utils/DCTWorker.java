package model.algorithms.utils;

import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DCTWorker extends Observable implements Runnable {

	private static final float DCT[][] = new float[][] {
			{ 4.0f, 5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
					5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
					5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
					5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
					5.65685424949238f, 5.65685424949238f, 5.65685424949238f },
			{ 1.38777878078145e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f },
			{ -1.66533453693773e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f,
					-2.35513868802566e-16f, -2.35513868802566e-16f },
			{ 3.60822483003176e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f,
					5.10280049072227e-16f, 5.10280049072227e-16f },
			{ -1.38777878078145e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f,
					-1.96261557335472e-16f, -1.96261557335472e-16f },
			{ -2.77555756156289e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f,
					-3.92523114670944e-16f, -3.92523114670944e-16f },
			{ -4.71844785465692e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f,
					-6.67289294940604e-16f, -6.67289294940604e-16f },
			{ 6.38378239159465e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f,
					9.02803163743171e-16f, 9.02803163743171e-16f },
			{ -5.55111512312578e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f,
					-7.85046229341888e-17f, -7.85046229341888e-17f },
			{ 6.93889390390723e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f,
					9.81307786677359e-16f, 9.81307786677359e-16f },
			{ -2.22044604925031e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f,
					-3.14018491736755e-16f, -3.14018491736755e-16f },
			{ 1.38777878078145e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f,
					1.96261557335472e-16f, 1.96261557335472e-16f },
			{ -2.51187959321442e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f,
					-3.55233418777204e-15f, -3.55233418777204e-15f },
			{ 4.02455846426619e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f,
					5.69158516272869e-16f, 5.69158516272869e-16f },
			{ 1.42941214420489e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f,
					2.02149404055536e-15f, 2.02149404055536e-15f },
			{ 2.59167687310935e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f,
					3.66518458323994e-15f, 3.66518458323994e-15f } };

	private static float QUANT[][] = new float[][] {
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

	private ConcurrentLinkedQueue<Integer> queue;
	private int width;
	private int[] image;
	private float quality;
	private boolean abort;
	private float tmp[];

	public DCTWorker(ConcurrentLinkedQueue<Integer> queue, int[] input,
			int width, float quality) {
		this.queue = queue;
		this.width = width;
		this.quality = quality;
		tmp = new float[DCTWorkerpool.BLOCK_SIZE * DCTWorkerpool.BLOCK_SIZE];
		image = input;
		abort = false;
	}

	public void run() {
		while (!queue.isEmpty() && !abort) {
			int b_num = queue.poll();
			int b_y = b_num / (width - DCTWorkerpool.BLOCK_SIZE + 1);
			int b_x = b_num % (width - DCTWorkerpool.BLOCK_SIZE + 1);

			float[] matrix = dct(b_x, b_y);
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
	 */
	private float[] dct(int offs_x, int offs_y) {
		int bsize = DCTWorkerpool.BLOCK_SIZE;
		float dct[] = new float[bsize * bsize];

		float f;

		for (int y = 0; y < bsize; y++) {
			for (int x = 0; x < bsize; x++) {
				f = 0;
				for (int i = 0; i < bsize; i++) {
					f += DCT[y][i] * image[(offs_y + i) * width + offs_x + x];
				}
				tmp[y * bsize + x] = f;
			}
		}

		for (int y = 0; y < bsize; y++) {
			for (int x = 0; x < bsize; x++) {
				f = 0;
				for (int i = 0; i < bsize; i++) {
					f += tmp[y * bsize + i] * DCT[i][x];
				}

				// Quantise the dct coefficient
				dct[y * bsize + x] = (float) (f / (QUANT[y][x] * quality));
			}
		}

		return dct;
	}

}
