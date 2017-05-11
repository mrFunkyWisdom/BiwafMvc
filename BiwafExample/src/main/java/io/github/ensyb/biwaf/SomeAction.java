package io.github.ensyb.biwaf;

import io.github.ensyb.biwaf.application.annotations.Action;
import io.github.ensyb.biwaf.application.annotations.Request;
import io.github.ensyb.biwaf.application.annotations.RequestParam;
import io.github.ensyb.biwaf.application.annotations.Request.HttpMethod;
import io.github.ensyb.biwaf.application.dispatch.response.Redirect;
import io.github.ensyb.biwaf.application.dispatch.response.Response;
import io.github.ensyb.biwaf.application.dispatch.response.Text;
import io.github.ensyb.biwaf.application.injection.meta.Inject;

@Action
public class SomeAction {
	
	@Inject(reference="randomGenerator")
	private RandomService service;
	
	@Request(value = "/random.html", method=HttpMethod.GET)
	public Response tellMeRandomNumber(){
		return new Text("the random number which i am generated is : "+ service.getRandomNumber(150));
	}
	
	@Request("/parametar/test")
	public Response sayHiTo(@RequestParam("name") String parametar){
		return new Text("hi "+ parametar);
	}
	
	@Request("/not")
	public Response noResp(){
		return new Redirect("/hi.jsp");
	}
}


