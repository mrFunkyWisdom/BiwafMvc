package io.github.ensyb.biwaf.application.dispatch;

public class BiwafWebException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BiwafWebException(Throwable cause) {
		super(cause);
	}

	public BiwafWebException(String message) {
		super(message);
	}


}
