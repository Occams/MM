package model.algorithms;

import java.awt.image.BufferedImage;

public interface ICopyMoveDetection {
	public void detect(BufferedImage input, float quality, int threshold,
			BufferedImage output);
}
