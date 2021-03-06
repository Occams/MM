package model;

import java.util.LinkedList;
import java.util.List;

public class Result {
	private long time;
	private String description;
	private List<ShiftVector> vectors;
	private Float progress;

	public Result(String desc) {
		setDescription(desc);
	}

	public Result(Float progress) {
		this.progress = progress;
	}

	public List<ShiftVector> getVectors() {
		return vectors != null ? vectors : new LinkedList<ShiftVector>();
	}

	public Result(String desc, long time) {
		setDescription(desc);
		this.time = time;
	}

	public Result(long time) {
		this.time = time;
	}

	public void addShiftVector(ShiftVector v) {
		if (vectors == null) {
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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}
}