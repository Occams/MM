package model;

import java.util.List;

public final class Event {
	public enum EventType {
		/**
		 * This type indicates that the copy move detection is finished. The
		 * data of this event contains a {@link List} of {@link ShiftVector}s.
		 */
		COPY_MOVE_DETECTION_FINISHED,
		
		/**
		 * This type indicates the current progress of the workerpool. The data 
		 */
		PROGRESS,

		/**
		 * Indicates that the algorithm was aborted. Note: Description field of
		 * result is set.
		 */
		ABORT,

		/**
		 * Indicates the status of the model. Note: Description field of result
		 * is set.
		 */
		STATUS,

		/**
		 * In this case an error occurred. What data is is undefined. Note:
		 * Description field of result is set
		 */
		ERROR;
	}

	private Result result;
	private EventType type;

	public Event(EventType type) {
		setType(type);
	}

	public Event(EventType type, Result result) {
		setType(type);
		setResult(result);
	}

	public Event(EventType type, String result) {
		setType(type);
		setResult(new Result(result));
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}

}
