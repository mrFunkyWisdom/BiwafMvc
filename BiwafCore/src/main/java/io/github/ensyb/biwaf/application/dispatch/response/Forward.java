package io.github.ensyb.biwaf.application.dispatch.response;

import java.util.Map;

import io.github.ensyb.biwaf.application.dispatch.BiwafWebException;

public class Forward implements Response {

	private Map<String, Object> requestMap;
	private Map<String, Object> sessionMap;
	
	public Forward addSessionModel(String name, Object value) {
		this.sessionMap.put(name, value);
		return this;
	}
	
	public Forward addRequestModel(String name, Object value) {
		this.requestMap.put(name, value);
		return this;
	} 
	
	@Override
	public void render(BiwafContext context) {
		throw new BiwafWebException("this is not done yet");
	}
	
	

}
