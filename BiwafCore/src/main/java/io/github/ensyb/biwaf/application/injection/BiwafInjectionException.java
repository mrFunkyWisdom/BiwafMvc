package io.github.ensyb.biwaf.application.injection;

public class BiwafInjectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public BiwafInjectionException(Throwable thr) {
		super(thr);
	}
	
	public BiwafInjectionException(String message) {
		super(message);
	}
	

}
