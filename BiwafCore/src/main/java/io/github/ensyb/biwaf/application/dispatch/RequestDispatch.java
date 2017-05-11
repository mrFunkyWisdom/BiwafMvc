package io.github.ensyb.biwaf.application.dispatch;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.ensyb.biwaf.application.dispatch.ActionClass.RequestMethod;
import io.github.ensyb.biwaf.application.dispatch.response.BiwafContext;
import io.github.ensyb.biwaf.application.dispatch.response.Redirect;
import io.github.ensyb.biwaf.application.dispatch.response.Response;

public final class RequestDispatch extends HttpServlet {
	private static final long serialVersionUID = 7457837972799802365L;
	private final String CHAR_ENCODING = "UTF-8";
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		this.handleRequest(request, response);
	}
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException{
		RenderView render = new RenderView();
		
		if(response.isCommitted())
			return;
		
		request.setCharacterEncoding(CHAR_ENCODING);
		
		String method = request.getMethod();
		String path = this.getRequestPath(request);

		if("/".equals(path)){
			Response redirect = new Redirect((String)request.getAttribute("defaultpage"));
			redirect.render(new BiwafContext(request, response));
			return;
		}
		try{
			ActionClass representation = getCommand(method, path, request);
			render.invokeController(request, response, representation);
			
		}catch(BiwafWebException e){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	private String getRequestPath(HttpServletRequest request){
		  String servletPath = request.getServletPath(); 
	      String pathInfo = request.getPathInfo();
	      return ((servletPath != null) ? servletPath : "") 
	    		  + ((pathInfo != null) ? pathInfo : ""); 
	}
	
	private ActionClass getCommand(String method, String path, HttpServletRequest servletRequest) {
		@SuppressWarnings("unchecked")
		Map<ActionClass.RequestMethod, ActionClass> commands = 
				(Map<ActionClass.RequestMethod, ActionClass>)servletRequest.getServletContext().getAttribute("commands");
		
		for (Map.Entry<RequestMethod, ActionClass> entry : commands.entrySet()) {
			if(entry.getKey().getPath().equals(path) && entry.getKey().getMethod().name().equalsIgnoreCase(method))
				return entry.getValue();
		}
		throw new BiwafWebException("there is no command for this request");
		/*
		RequestMethod key = commands
				.keySet()
				.stream()
				.filter(
				request -> request.getPath().equals(path) && request.getMethod().name().equalsIgnoreCase(method))
				.findFirst().get();

		return commands.get(key);
		*/
	}
	

	
}
