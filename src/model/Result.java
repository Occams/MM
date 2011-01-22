package model;

import java.util.LinkedList;
import java.util.List;

public class Result {
	private long time;
	private String description;
	private List<ShiftVector> vectors;

	public Result(String desc) {
		description = desc;
	}

	public Result(String desc, long time) {
		description = desc;
		this.time = time;
	}

	public Result(long time) {
		this.time = time;
	}

	public void addShiftVector(ShiftVector v) {
		if (vectors != null) {
			vectors = new LinkedList<ShiftVector>();
		}

		vectors.add(v);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}