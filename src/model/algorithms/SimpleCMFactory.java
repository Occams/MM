package model.algorithms;

public class SimpleCMFactory implements CopyMoveFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new SimpleRMAlgorithm();
	}

}
