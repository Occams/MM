package model.algorithms;

public class RobustMatchPartialDCTFactory implements CopyMoveDetectionFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new RobustMatchPartialDCT();
	}

}
