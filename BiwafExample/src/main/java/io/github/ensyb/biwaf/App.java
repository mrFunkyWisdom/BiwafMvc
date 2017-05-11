package io.github.ensyb.biwaf;

import io.github.ensyb.biwaf.application.bootstrap.Biwaf;
import io.github.ensyb.biwaf.application.bootstrap.BiwafApplication;

@Biwaf(basePackage = "io.github.ensyb.biwaf")
public class App {
	
	public static void main(String[] args) {
		BiwafApplication app = new BiwafApplication.Configure()
					.applicationMain(App.class)
					.registerErrorPage(new BiwafApplication.Error(404, "/notFound.html"),
									new BiwafApplication.Error(500, "/serverError.html"))
					.port(8080).buildConfiguration();
		
		app.start();
	}
}
