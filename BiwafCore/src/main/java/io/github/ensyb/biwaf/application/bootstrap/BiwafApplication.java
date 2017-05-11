package io.github.ensyb.biwaf.application.bootstrap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import io.github.ensyb.biwaf.application.dispatch.BiwafWebException;
import io.github.ensyb.biwaf.application.dispatch.RequestDispatch;
import io.github.ensyb.biwaf.application.dispatch.RequestMethodLoading;

public final class BiwafApplication {

	public static class Error{
		private final String errorPath;
		private final int errorCode;
		
		public Error(int errorCode, String errorPagePath) {
			this.errorCode = errorCode;
			this.errorPath = errorPagePath;
		}
		
		public ErrorPage useErrorPage(){
			ErrorPage page = new ErrorPage();
			page.setErrorCode(this.errorCode);
			page.setLocation(this.errorPath);
			return page;
		}
		
	}
	
	private final Class<?> applicationClass;
	private final int port;
	private final Error[] errorPages;
	private final String webContntentFolder;
	private final String defaultPage;
	
	public final static class Configure{
		private Class<?> applicationClass;
		private int port;
		private Error[] errorPages;
		private String webContentFolderName;
		private String defaultPage;
		
		public Configure applicationMain(Class<?> mainApplicationClass){
			this.applicationClass = mainApplicationClass;
			return this;
		}
		
		public Configure port(int port){
			this.port = port;
			return this;
		}
		
		public Configure registerErrorPage(Error...errorpages){
			this.errorPages = errorpages;
			return this;
		}
		
		public Configure webContentFolderName(String folderName){
			this.webContentFolderName = folderName;
			return this;
		}
		
		public Configure defaultPage(String path){
			this.defaultPage = path;
			return this;
		}

		
		public BiwafApplication buildConfiguration(){
			if(applicationClass == null)
				throw new BiwafWebException("There is no main class specified inside configuration");
			if(webContentFolderName == null)
				webContentFolderName = "WebContent";
			
			return new BiwafApplication(this);
		}
	}
	
	private BiwafApplication(Configure conf){
		this.applicationClass = conf.applicationClass;
		this.port = conf.port;
		this.errorPages = conf.errorPages;
		this.webContntentFolder = conf.webContentFolderName;
		this.defaultPage = conf.defaultPage;
	}

	public void start() {
		this.startTomcat();
	}

	private void startTomcat(){
		try {
			System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");

			Tomcat webServer = new Tomcat();
			Path tomcatPath = Files.createTempDirectory("tomcat");

			webServer.setBaseDir(tomcatPath.toString());
			webServer.setPort(this.port);

			File webContentFolder = new File(this.webContntentFolder);
			if (!webContentFolder.exists())
				webContentFolder = Files.createTempDirectory(this.webContntentFolder).toFile();

			Context applicationConatiner = webServer.addWebapp("/",
					webContentFolder.getAbsolutePath());
			applicationConatiner.setParentClassLoader(Thread.currentThread().getContextClassLoader());
			
			applicationConatiner.addApplicationListener(RequestMethodLoading.class.getName());
			Tomcat.addServlet(applicationConatiner, "dispatcherServlet", new RequestDispatch());
			applicationConatiner.addServletMapping("/*", "dispatcherServlet");
			
			ServletContext context = applicationConatiner.getServletContext();	
			context.setAttribute("basepath", getBasePath());
			context.setAttribute("defaultpage", this.defaultPage);
			
			if(this.errorPages != null)
				setupErrorPages(applicationConatiner);
			
			webServer.start();
			webServer.getServer().await();

		} catch (Exception e) {
			throw new BiwafWebException(e);
		}
	}
	
	private void setupErrorPages(Context container){
		for (Error error : errorPages) {
			container.addErrorPage(error.useErrorPage());
		}
	}


	
	private String getBasePath(){
		Biwaf annotation = this.applicationClass.getAnnotation(Biwaf.class);
		if(annotation == null || annotation.basePackage().isEmpty())
			throw new BiwafWebException("there is no biwaf annotaion on class " + this.applicationClass.getName());
		return annotation.basePackage();
	}

	
}
