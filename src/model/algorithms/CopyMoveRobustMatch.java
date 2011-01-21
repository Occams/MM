package model.algorithms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.sun.xml.internal.fastinfoset.tools.PrintTable;

import edu.emory.mathcs.jtransforms.dct.FloatDCT_1D;
import edu.emory.mathcs.jtransforms.dct.FloatDCT_2D;

/**
 * @author huber
 * 
 */
public class CopyMoveRobustMatch extends ICopyMoveDetection {
	private static final int BLOCK_SIZE = 16;

	private static final float QUANT[] = new float[] { 32.0f, 27.5f, 25.0f,
			40.0f, 60.0f, 100.0f, 127.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 30.0f, 30.0f, 35.0f, 47.5f,
			65.0f, 145.0f, 150.0f, 137.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 152.5f, 152.5f, 35.0f, 32.5f, 40.0f, 60.0f, 100.0f,
			142.5f, 172.5f, 140.0f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 152.5f, 35.0f, 42.5f, 55.0f, 72.5f, 127.5f, 217.5f,
			200.0f, 155.0f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 45.0f, 55.0f, 92.5f, 140.0f, 170.0f, 272.5f,
			257.5f, 192.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 60.0f, 87.5f, 137.5f, 160.0f, 202.5f, 260.0f,
			282.5f, 230.0f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 122.5f, 160.0f, 195.0f, 217.5f, 257.5f, 302.5f,
			300.0f, 252.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 180.0f, 230.0f, 237.5f, 245.0f, 280.0f, 250.0f,
			257.5f, 247.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f, 152.5f,
			152.5f, 152.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f, 180.0f,
			180.0f, 180.0f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f, 247.5f,
			247.5f, 247.5f };

	private static final float DCT[] = new float[] { 4.0f, 5.65685424949238f,
			5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
			5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
			5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
			5.65685424949238f, 5.65685424949238f, 5.65685424949238f,
			5.65685424949238f, 5.65685424949238f, 1.38777878078145e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, -1.66533453693773e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, -2.35513868802566e-16f,
			-2.35513868802566e-16f, 3.60822483003176e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, 5.10280049072227e-16f,
			5.10280049072227e-16f, -1.38777878078145e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -1.96261557335472e-16f,
			-1.96261557335472e-16f, -2.77555756156289e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -3.92523114670944e-16f,
			-3.92523114670944e-16f, -4.71844785465692e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, -6.67289294940604e-16f,
			-6.67289294940604e-16f, 6.38378239159465e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, 9.02803163743171e-16f,
			9.02803163743171e-16f, -5.55111512312578e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, -7.85046229341888e-17f,
			-7.85046229341888e-17f, 6.93889390390723e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, 9.81307786677359e-16f,
			9.81307786677359e-16f, -2.22044604925031e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, -3.14018491736755e-16f,
			-3.14018491736755e-16f, 1.38777878078145e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, 1.96261557335472e-16f,
			1.96261557335472e-16f, -2.51187959321442e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, -3.55233418777204e-15f,
			-3.55233418777204e-15f, 4.02455846426619e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 5.69158516272869e-16f,
			5.69158516272869e-16f, 1.42941214420489e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.02149404055536e-15f,
			2.02149404055536e-15f, 2.59167687310935e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f,
			3.66518458323994e-15f, 3.66518458323994e-15f, 3.66518458323994e-15f };

	public static void main(String[] args) {
		ICopyMoveDetection d = new CopyMoveRobustMatch();
		try {
			d.detect(ImageIO
					.read(new File("Testbilder/EncampmentSelfTamp.bmp")), 0.5f,
					10, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CopyMoveRobustMatch() {

	}

	public void detect(BufferedImage input, float quality, int threshold,
			BufferedImage output) {
		if (!checkImage(input))
			return;

		printTime();
		int blockCount_x = input.getWidth() - BLOCK_SIZE + 1;
		int blockCount_y = input.getHeight() - BLOCK_SIZE + 1;
		int height = input.getHeight();
		int width = input.getWidth();

		/*
		 * The image can be processed. First calc grayscale image...
		 */
		float[] grayscale = new float[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = input.getRGB(x, y);
				grayscale[y * width + x] = (float) (((pixel >> 16) & 0xff)
						* 0.299 + ((pixel >> 8) & 0xff) * 0.587 + ((pixel) & 0xff) * 0.114);
			}
		}
		System.out.println("Grayscale Matrix: ");
		printTime();

		/*
		 * Calculate the dcts of each block...
		 */
		FloatDCT_1D tr = new FloatDCT_1D(BLOCK_SIZE * BLOCK_SIZE);
		List<Block> dcts = new ArrayList<Block>(blockCount_x * blockCount_y);
		for (int y = 0; y < blockCount_y; y++) {
			for (int x = 0; x < blockCount_x; x++) {
				Block b = new Block(dct(x, y, width, grayscale, quality), x, y);
				dcts.add(b);
			}
		}
		System.out.println("DCTs: ");
		printTime();

		/*
		 * Sort the dcts lexicographically...
		 */
		Collections.sort(dcts);
		System.out.println("Sorting DCTs: ");
		printTime();

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
		System.out.println("Shift Vectors: ");
		printTime();

		int rgbArray[] = new int[BLOCK_SIZE * BLOCK_SIZE];

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
					input.setRGB(b1.getPos_x(), b1.getPos_y(), 16, 16,
							rgbArray, 0, BLOCK_SIZE);
					input.setRGB(b2.getPos_x(), b2.getPos_y(), 16, 16,
							rgbArray, 0, BLOCK_SIZE);
				}
			}
		}
		System.out.println("Drawing detected copies ");
		printTime();

		try {
			ImageIO.write(input, "jpg", new File("output.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the copy move detection can be performed on the given image.
	 * 
	 * @param image
	 * @return
	 */
	private boolean checkImage(BufferedImage image) {
		return true;
	}

	/**
	 * Computes the dct of the given block...
	 * 
	 * @param offs_x
	 * @param offs_y
	 */
	private float[] dct(int offs_x, int offs_y, int width, float[] image,
			float quality) {
		float dct[] = new float[BLOCK_SIZE * BLOCK_SIZE];
		float tmp[] = new float[BLOCK_SIZE * BLOCK_SIZE];

		for (int y = 0; y < BLOCK_SIZE; y++) {
			for (int x = 0; x < BLOCK_SIZE; x++) {
				tmp[y * BLOCK_SIZE + x] = 0.0f;
				for (int i = 0; i < BLOCK_SIZE; i++) {
					tmp[y * BLOCK_SIZE + x] += DCT[y * BLOCK_SIZE + i]
							* image[(offs_y + i) * width + offs_x + x];
				}
			}
		}

		for (int y = 0; y < BLOCK_SIZE; y++) {
			for (int x = 0; x < BLOCK_SIZE; x++) {
				dct[y * BLOCK_SIZE + x] = 0.0f;
				for (int i = 0; i < BLOCK_SIZE; i++) {
					dct[y * BLOCK_SIZE + x] += tmp[y * BLOCK_SIZE + i]
							* DCT[i * BLOCK_SIZE + x];
				}

				// Quantise the dct coefficient
				dct[y * BLOCK_SIZE + x] = dct[y * BLOCK_SIZE + x]
						/ (QUANT[y * BLOCK_SIZE + x] * quality);
			}
		}

		return dct;
	}

	private class Block implements Comparable<Block> {
		private float[] values;
		private int pos_x;
		private int pos_y;

		public Block(float[] vals, int x, int y) {
			this.values = vals;
			pos_x = x;
			pos_y = y;
		}

		public float[] getValues() {
			return values;
		}

		public void setValues(float[] values) {
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
			float[] b1 = getValues();
			float[] b2 = o.getValues();

			for (int i = 0; i < b1.length; i++) {
				if (b1[i] < b2[i]) {
					return -1;
				} else if (b1[i] > b2[i]) {
					return 1;
				}
			}
			return 0;
		}

		public String toString() {
			StringBuilder b = new StringBuilder();
			for (float f : getValues()) {
				b.append(f);
				b.append(",");
			}
			return b.toString();
		}
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	private long oldtime = 0;

	private void printTime() {
		if (oldtime != 0) {
			System.out
					.println((Calendar.getInstance().getTimeInMillis() - oldtime)
							/ 1000.0f + "s");
		}
		oldtime = Calendar.getInstance().getTimeInMillis();
	}
}
