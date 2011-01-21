package model;

import java.awt.Image;
import java.awt.image.BufferedImage;

public final class Event {
	public enum EventType {
		/**
		 * This type indicates that the copy move detection is finished. The
		 * data of this event contains a {@link BufferedImage} which contains
		 * the regions that were detected as copied.
		 */
		COPY_MOVE_DETECTION_FINISHED,
		
		ABORT,

		/**
		 * In this case an error occurred. What data is is undefined.
		 */
		ERROR;
	}

	private Result result;
	private EventType type;

	public class Result {
		private int time;
		private Image blocks, vectors;
		
		public Result(int time, Image blocks,Image vectors) {
			this.time = time;
			this.blocks = blocks;
			this.vectors = vectors;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

		public Image getBlocks() {
			return blocks;
		}

		public void setBlocks(Image blocks) {
			this.blocks = blocks;
		}

		public Image getVectors() {
			return vectors;
		}

		public void setVectors(Image vectors) {
			this.vectors = vectors;
		}
		
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
