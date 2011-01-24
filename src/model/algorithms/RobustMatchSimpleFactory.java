package model.algorithms;

public class RobustMatchSimpleFactory implements CopyMoveDetectionFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new RobustMatchSimple();
	}

}
