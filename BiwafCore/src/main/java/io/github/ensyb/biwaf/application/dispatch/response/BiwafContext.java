package io.github.ensyb.biwaf.application.dispatch.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class BiwafContext {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public BiwafContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest currentRequest(){
		return this.request;
	}
	
	public HttpServletResponse currentResponse(){
		return this.response;
	}
}
