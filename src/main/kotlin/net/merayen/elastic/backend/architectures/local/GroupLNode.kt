package net.merayen.elastic.backend.architectures.local

/**
 * LNodes implementing GroupLNode are capable to hold other children nodes.
 * Those children nodes can get data from LNodes implementing GroupLNode, by using getParent()
 */
interface GroupLNode {
	/**
	 * Returns the current BPM for the frame being processed.
	 *
	 * We do not support changing of BPM inside a frame, so if buffer is at 1024 samples, all those 1024 samples will
	 * have the same BPM for all the samples. This simplifies processing and gives probably better performance.
	 */
	fun getCurrentFrameBPM(): Double

	/**
	 * Returns how many beats there is in a bar, at current location.
	 * Division can change from bar to bar.
	 */
	fun getCurrentBarDivision(): Int

	/**
	 * Get playback cursor position in beats.
	 * E.g, when BPM is 120 from start to current position, and we are 1 minute into the song, this will return 120.0.
	 * When isPlaying() is false, this value will never change, unless user changes the BPM manually.
	 * Will not increase isPlaying() returns false.
	 */
	fun getCursorBeatPosition(): Double

	/**
	 * Get playback cursor position in time from frame start.
	 * Will not increase isPlaying() returns false.
	 */
	fun getCursorTimePosition(): Double

	/**
	 * Get position in samples from beginning.
	 * E.g: Time is 1 min exactly, 44100Hz sampling, this will return 44100*60=2646000.
	 * Increases regardless of isPlaying() is true or false.
	 */
	fun getCursorSamplePosition(): Long

	/**
	 * Returns sample position, that starts at 0 and increases every frame for the whole life of the DSP Supervisor.
	 * Never resets.
	 */
	fun getSamplePosition(): Long

	/**
	 * Returns the current beat position.
	 * Overflow to 0 whenever reaching the bar divison count (getCurrentBarDivision())
	 */
	fun getCurrentBeatPosition(): Double

	/**
	 * If we are playing or not.
	 */
	fun isPlaying(): Boolean

	/**
	 * Returns how many times play has started.
	 * Nodes should check if this number has changed. If yes, read getTimePosition(), getBeatPosition() etc to set
	 * correct inner state.
	 */
	fun playStartedCount(): Long
}