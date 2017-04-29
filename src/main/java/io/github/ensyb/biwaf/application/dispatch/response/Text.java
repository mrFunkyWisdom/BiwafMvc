package io.github.ensyb.biwaf.application.dispatch.response;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class Text implements Response{

	private final String textToRender;

	public Text(String textToRender) {
		this.textToRender = textToRender;
		
	}
	
	@Override
	public void render(BiwafContext context) {
		try {
			HttpServletResponse response = context.currentResponse();
			PrintWriter writter = response.getWriter();
			writter.write(this.textToRender);
			writter.flush();
			writter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
