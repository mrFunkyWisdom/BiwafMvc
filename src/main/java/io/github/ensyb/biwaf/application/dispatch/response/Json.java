package io.github.ensyb.biwaf.application.dispatch.response;

import io.github.ensyb.biwaf.application.dispatch.BiwafWebException;

public class Json implements Response{

	@Override
	public void render(BiwafContext context) {
		throw new BiwafWebException("json response is not yet implemented");
	}

}
