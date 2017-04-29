## Biwaf MVC 

> **biwaf mvc** is simple and minimal *java web request/action MVC framework* with a dependency injection container
> the framework is based on servlets and comes with embedded tomcat

configuration of some biwaf application
```java
@Biwaf(basePackage = "org.someproject.application")
public class App {
	
	public static void main(String... args) {
		
		BiwafApplication conifg = new BiwafApplication.Configure()
				.applicationMain(App.class)
				.registerErrorPage(new BiwafApplication.Error(404, "/notFound.html"),
						new BiwafApplication.Error(500, "/error/serverError.html"))
				.defaultPage("/fujo")
				.webContentFolderName("WebContent")
				.port(8080).buildConfiguration();
		conifg.start();
	}
}
```
create injectable services

```java
@Injectable(id="service", scope=Scope.SINGLETON)
public class SomeService {

	public int getRandomNumber() {
		return new Random().nextInt(100);
	}
	
}
```
and use that service in action class

```java
@Action
public class TestAction {
	
	@Inject(reference="service")
	private SomeService service;
	
	@Request("/fujo")
	public Response responseTest(){
		return new TextResponse("request to fujo brought you here "+ service.getRandomNumber());
	}
	
	@Request("/user/hello")
	public Response helloResponse(@RequestParam("name") String name) {
		return new TextResponse("Hello "+ name);
	}


}
```

> biwaf is very early in development and not everything is working so fork, improve and create a pull request





