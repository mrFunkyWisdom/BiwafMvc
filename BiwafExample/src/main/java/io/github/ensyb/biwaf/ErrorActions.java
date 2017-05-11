package io.github.ensyb.biwaf;

import io.github.ensyb.biwaf.application.annotations.Action;
import io.github.ensyb.biwaf.application.annotations.Request;
import io.github.ensyb.biwaf.application.dispatch.response.Response;
import io.github.ensyb.biwaf.application.dispatch.response.Text;

@Action
public class ErrorActions {

	@Request("/notFound.html")
	public Response notFoundRequest(){
		return new Text("it seems that there is nothing fun around here");
	}
	
	@Request("/serverError.html")
	public Response serverError(){
		return new Text("our server is on a short vacation, maybe next time when you visit this path it would work");
	}
	
	
}
