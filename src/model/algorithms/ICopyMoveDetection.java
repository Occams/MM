package model.algorithms;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Observable;

public abstract class ICopyMoveDetection extends Observable {
	public abstract void detect(BufferedImage input, float quality, int threshold, int threads);
	public abstract void abort();
}
