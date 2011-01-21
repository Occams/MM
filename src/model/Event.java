package model;

import java.awt.image.BufferedImage;

public final class Event {
	public enum EventType {
		/**
		 * This type indicates that the copy move detection is finished. The
		 * data of this event contains a {@link BufferedImage} which contains
		 * the regions that were detected as copied.
		 */
		COPY_MOVE_DETECTION_FINISHED,

		/**
		 * In this case an error occurred. What data is is undefined.
		 */
		ERROR;
	}

	private Object data;
	private EventType type;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Event(EventType type, Object data) {
		this.type = type;
		this.data = data;
	}

}
