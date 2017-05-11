package io.github.ensyb.biwaf.application.injection;

import java.lang.reflect.Field;
import java.util.Optional;

import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.meta.Inject;

public final class Injector {

	private final String basePath;

	public Injector(String basePath) {
		this.basePath = basePath;
	}
	
	public Optional<Object> createInstanceWithInjectedField(Class<?> klazz){
		ProvideBeans beanBindings = new ProvideBeans
				.StandardProvider(new LoadBindings
						.AnnotationBinded()
						.load(this.basePath));
		
		Object instance = null;

			if(!klazz.isAnnotationPresent(Injectable.class)){
				Field[] fields = klazz.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					if(field.isAnnotationPresent(Inject.class)){
						Inject annotation = field.getAnnotation(Inject.class);
						Object valueToSet = null;
						if (annotation.value() != null && !annotation.value().trim().isEmpty()) 
							valueToSet = annotation.value();
						 else if (annotation.reference() != null && !annotation.reference().trim().isEmpty()) 
							 valueToSet = beanBindings.getBean(annotation.reference());
						try {
							instance = klazz.newInstance();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						FieldInjection injection = new FieldInjection(instance, field.getName(), valueToSet);
						injection.setProperty();
					}
				}
			}
	
			return Optional.ofNullable(instance);
	}
	
}
