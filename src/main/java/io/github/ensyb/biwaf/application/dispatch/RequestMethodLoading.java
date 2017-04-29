package io.github.ensyb.biwaf.application.dispatch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ensyb.biwaf.application.annotations.Action;
import io.github.ensyb.biwaf.application.annotations.Request;
import io.github.ensyb.biwaf.application.dispatch.ActionClass.RequestMethod;
import io.github.ensyb.biwaf.application.dispatch.response.Response;
import io.github.ensyb.biwaf.application.injection.scan.ClassPathScann;

@WebListener
public final class RequestMethodLoading implements ServletContextListener {
	
	public Logger LOG = LoggerFactory.getLogger(RequestMethodLoading.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		Map<RequestMethod, ActionClass> commands = new HashMap<>();
		
		ClassPathScann scanner = new ClassPathScann((String)sce.getServletContext().getAttribute("basepath"));
		
		List<Class<?>> classList = scanner.loadClassesWithAnnotation(Action.class);
		classList.stream()
		.forEach(klas -> {
			Method[] classMethods = klas.getDeclaredMethods();
			Arrays.stream(classMethods).forEach(metod -> {
				if (metod.isAnnotationPresent(Request.class)) {
					if(!Response.class.isAssignableFrom(metod.getReturnType()))
						throw new BiwafWebException(
								"request methods must return some response class not "+metod.getReturnType().getName()+" class");
					
					String requestPath = metod.getAnnotation(Request.class).value();
					

					Request.HttpMethod requestMethod = metod.getAnnotation(Request.class).method();
					
					commands.put(new RequestMethod(requestMethod, requestPath),
							new ActionClass(metod, klas));
				}
			});

		});
		
		ServletContext context = sce.getServletContext();
		context.setAttribute("commands", commands);

		LOG.info("biwaf is loaded request methods");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

}
