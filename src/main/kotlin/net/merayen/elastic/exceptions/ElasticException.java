package net.merayen.elastic.exceptions;

public class ElasticException extends RuntimeException {
	public ElasticException(Throwable e) {
		super(e);
	}

	public ElasticException() {
		super();
	}
}
