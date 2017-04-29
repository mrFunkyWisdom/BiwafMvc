package io.github.ensyb.biwaf.application.bootstrap;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletContext;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ensyb.biwaf.application.dispatch.BiwafWebException;

public final class BiwafApplication {

	private final Logger LOG = LoggerFactory.getLogger(BiwafApplication.class);
	
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
	private final boolean tldScanning;
	private final String webContntentFolder;
	private final String defaultPage;
	
	public final static class Configure{
		private Class<?> applicationClass;
		private int port;
		private Error[] errorPages;
		private boolean tldScanning;
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
		
		public Configure tldScanning(boolean flag){
			this.tldScanning = flag;
			return this;
		}
		
		public BiwafApplication buildConfiguration(){
			if(applicationClass == null)
				throw new BiwafWebException("There is no main class specified inside configuration");
			
			return new BiwafApplication(this);
		}
	}
	
	private BiwafApplication(Configure conf){
		this.applicationClass = conf.applicationClass;
		this.port = conf.port;
		this.errorPages = conf.errorPages;
		this.tldScanning = conf.tldScanning;
		this.webContntentFolder = conf.webContentFolderName;
		this.defaultPage = conf.defaultPage;
	}

	public void start() {
		this.startTomcat();
	}

	private void startTomcat(){
		try {
			File root = getRootFolder();
			System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");

			Tomcat tomcat = new Tomcat();
			Path tomcatPath = Files.createTempDirectory("tomcat-directory");

			tomcat.setBaseDir(tomcatPath.toString());
			tomcat.setPort(this.port);

			File webContentFolder = new File(root.getAbsolutePath(), this.webContntentFolder);
			if (!webContentFolder.exists())
				webContentFolder = Files.createTempDirectory("default-base").toFile();

			StandardContext applicationConatiner = (StandardContext) tomcat.addWebapp("",
					webContentFolder.getAbsolutePath());
			applicationConatiner.setParentClassLoader(Thread.currentThread().getContextClassLoader());

			ServletContext context = applicationConatiner.getServletContext();	
			
			context.setAttribute("basepath", getBasePath());
			context.setAttribute("defaultpage", this.defaultPage);
			
			if(this.tldScanning)
				tldScanning(webContentFolder, applicationConatiner);
			
			webinfClasses(root, applicationConatiner);
			if(this.errorPages != null)
				setupErrorPages(applicationConatiner);
			tomcat.start();
			tomcat.getServer().await();

		} catch (Exception e) {
			throw new BiwafWebException(e);
		}
	}
	
	private void setupErrorPages(StandardContext container){
		for (Error error : errorPages) {
			container.addErrorPage(error.useErrorPage());
		}
	}
	
	private void webinfClasses(File root, StandardContext applicationConatiner) {
		File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
		WebResourceRoot resources = new StandardRoot(applicationConatiner);

		WebResourceSet resourceSet;
		if (additionWebInfClassesFolder.exists()) {
			resourceSet = new DirResourceSet(resources, "/WEB-INF/classes",
					additionWebInfClassesFolder.getAbsolutePath(), "/");
			LOG.info("loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
		} else {
			resourceSet = new EmptyResourceSet(resources);
		}
		resources.addPreResources(resourceSet);
		applicationConatiner.setResources(resources);
	}

	private void tldScanning(File webContentFolder, StandardContext applicationConatiner) {
			StandardJarScanFilter jarScanFilter = (StandardJarScanFilter) applicationConatiner.getJarScanner()
					.getJarScanFilter();
			jarScanFilter.setTldSkip("*");
		LOG.info("tomcat scanning for tag librarys are enabled" + webContentFolder.getAbsolutePath());
	}

	private File getRootFolder() {
		try {
			String runningJarPath = Main.class
					.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath().replaceAll("\\\\", "/");
			int lastIndexOf = runningJarPath.lastIndexOf("/target/");
			File root = (lastIndexOf < 0 ) ? new File("") : new File(runningJarPath.substring(0, lastIndexOf));  
	
			LOG.info("application resolved root folder: " + root.getAbsolutePath());
			return root;
		} catch (URISyntaxException ex) {
			throw new BiwafWebException(ex);
		}
	}
	
	private String getBasePath(){
		Biwaf annotation = this.applicationClass.getAnnotation(Biwaf.class);
		if(annotation == null || annotation.basePackage().isEmpty())
			throw new BiwafWebException("there is no biwaf annotaion on class " + this.applicationClass.getName());
		return annotation.basePackage();
	}
	
	
}
