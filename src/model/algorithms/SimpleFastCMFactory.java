package model.algorithms;

public class SimpleFastCMFactory implements CopyMoveFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new SimpleFastRMAlgorithm();
	}

}
