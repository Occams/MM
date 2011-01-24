package model.algorithms;

public class RobustMatchMMFactory implements CopyMoveDetectionFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new RobustMatchMM();
	}

}
