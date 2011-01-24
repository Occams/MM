package model.algorithms;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * @author Huber Bastian
 * 
 *         An abstract interface for a copy-move-detection algorithm that can be
 *         observed.
 * 
 */
public abstract class ICopyMoveDetection extends Observable {
	public abstract void detect(BufferedImage input, float quality,
			int threshold,int minLength, int threads);

	public abstract void abort();
}
