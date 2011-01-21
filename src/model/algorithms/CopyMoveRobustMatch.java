package model.algorithms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import model.algorithms.utils.Block;
import model.algorithms.utils.DCTWorkerpool;

import com.sun.xml.internal.fastinfoset.tools.PrintTable;

import edu.emory.mathcs.jtransforms.dct.FloatDCT_1D;
import edu.emory.mathcs.jtransforms.dct.FloatDCT_2D;

/**
 * @author huber
 * 
 */
public class CopyMoveRobustMatch extends ICopyMoveDetection {
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
		DCTWorkerpool pool = new DCTWorkerpool(grayscale, width, height, quality, 4);
		List<Block> dcts = pool.getResult();
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

		int rgbArray[] = new int[DCTWorkerpool.BLOCK_SIZE * DCTWorkerpool.BLOCK_SIZE];
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
							rgbArray, 0, DCTWorkerpool.BLOCK_SIZE);
					input.setRGB(b2.getPos_x(), b2.getPos_y(), 16, 16,
							rgbArray, 0, DCTWorkerpool.BLOCK_SIZE);
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
	
	private class DCTWorker extends Thread {
		public void run() {
			
		}
	}
}
