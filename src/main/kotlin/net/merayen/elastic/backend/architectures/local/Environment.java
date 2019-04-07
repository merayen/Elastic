package net.merayen.elastic.backend.architectures.local;

public class Environment {
	public class Playhead {
		/**
		 * Sample count, start of the process buffer
		 */
		public long tick;

		/**
		 * On what tick the playhead is set.
		 */
		public long position;

		public float bpm;

	}
}
