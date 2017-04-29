package io.github.ensyb.biwaf.application.dispatch.response;

import java.io.IOException;

import io.github.ensyb.biwaf.application.dispatch.BiwafWebException;

public class Redirect implements Response{

	private String redirectPath;

	public Redirect(String redirect) {
		this.redirectPath = redirect;
	}
	
	@Override
	public void render(BiwafContext context) {
		try {
			context.currentResponse().sendRedirect(this.redirectPath);
		} catch (IOException e) {
			throw new BiwafWebException("redirect to "+ redirectPath+" failed");
		}
	}


}
