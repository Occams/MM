package model.algorithms;

public class CopyMoveRMFactory implements CopyMoveFactory {

	@Override
	public ICopyMoveDetection getInstance() {
		return new CopyMoveRobustMatch();
	}

}
