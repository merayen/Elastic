package net.merayen.merasynth.exceptions;

public class MeraSynthException extends RuntimeException {
	public MeraSynthException(Throwable e) {
		super(e);
	}

	public MeraSynthException() {
		super();
	}
}
