package model.algorithms;

public class RobustMatchFastFactory implements CopyMoveDetectionFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new RobustMatchFast();
	}

}
