package io.github.ensyb.biwaf.application.injection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ensyb.biwaf.application.injection.BeanRepresenation.PropertyRepresentation;
import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.meta.Inject;
import io.github.ensyb.biwaf.application.injection.scan.ClassPathScann;

public interface LoadBindings {

	public Map<String, BeanRepresenation> load(String path);
	
	static final class AnnotationBinded implements LoadBindings{

		@Override
		public Map<String, BeanRepresenation> load(String basePackage) {
			Map<String, BeanRepresenation> beans = new HashMap<>();
			ClassPathScann scanner = new ClassPathScann(basePackage);
			scanner.loadClassesWithAnnotation(Injectable.class).stream()
			.forEach(clazz -> {
				Injectable bean = clazz.getAnnotation(Injectable.class);

				String orName = clazz.getSimpleName();
				String id = bean.id();
				String idAsClassName = orName.substring(0, 1).toLowerCase() + orName.substring(1);
				if (null == id || id.isEmpty())
					id = idAsClassName;

				BeanRepresenation info = new BeanRepresenation.Construct()
						.id(id)
						.className(clazz.getName())
						.scope(bean.scope())
						.properies(getPropertyFields(clazz.getDeclaredFields()))
						.build();

				beans.put(info.Id, info);

			});
			return beans;
		}
		
		private List<PropertyRepresentation> getPropertyFields(Field[] fields) {
			List<PropertyRepresentation> listOfProperties = new ArrayList<>();
			Arrays.stream(fields).forEach(field ->{
				Inject inject = field.getAnnotation(Inject.class);
				if (inject != null) {
					String name = field.getName();
					String ref = inject.reference();
					String value = inject.value();

					if ((null == ref || ref.isEmpty()) && (null == value || value.isEmpty()))
						ref = name;

					PropertyRepresentation propInfo = new PropertyRepresentation(name, ref, value);
					listOfProperties.add(propInfo);
				}
			});
			return listOfProperties;
		}

	}
}
