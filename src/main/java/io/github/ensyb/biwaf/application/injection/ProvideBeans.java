package io.github.ensyb.biwaf.application.injection;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ensyb.biwaf.application.injection.BeanRepresenation.PropertyRepresentation;
import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.meta.InitBean;

public interface ProvideBeans {

	public Object getBean(String id);

	public <T> T getBean(String name, Class<T> classToLoad);

	public static class StandardProvider implements ProvideBeans {

		private final Logger LOG = LoggerFactory.getLogger(StandardProvider.class);
		private final Map<String, BeanRepresenation> beans;
		
		public StandardProvider(Map<String, BeanRepresenation> bindings){
			this.beans = bindings;
		}
		
	    public StandardProvider(String basePath) {
	    	synchronized (this) {
	    		this.beans = new LoadBindings.AnnotationBinded().load(basePath);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getBean(String id, Class<T> classToLoad) {
			return (T) getBean(id);
		}

		@Override
		public Object getBean(String id) {
			Object beanInstance = null;
			BeanRepresenation bean = beans.get(id);
			if (bean != null) {
				beanInstance = bean.BeanInstance;
				
				if (beanInstance == null) {
					beanInstance = createNewInstance(bean.ClassName);
					if (beanInstance == null) {
						throw new BiwafInjectionException(
								"Can not instantiate object from class " + bean.ClassName);
					}
					setPropertyFields(bean.Properties, beanInstance);

					// if there is lifecycle method for initialization run
					if (beanInstance != null && beanInstance instanceof InitBean)
						((InitBean) beanInstance).init();

					if (Injectable.Scope.SINGLETON.equals(bean.Scope)){
						bean = bean.changeBeanInstance(beanInstance);
						beans.put(id, bean);
					}
				}
			}
			if (beanInstance == null) {
				LOG.warn("there is no bean with id ", id);
				return null;
			}
			return beanInstance;
		}
		
		public void setPropertyFields(List<PropertyRepresentation> beanProperties, Object instanceToSet) {
			beanProperties.stream().forEach(property -> {
				String nameOfProperty = property.NameOfProperty;
				Object valueForProperty = property.ValueOfProperty;

				if (!isEmptyString(property.ValueOfProperty)) 
					valueForProperty = property.ValueOfProperty;
				 else if (!isEmptyString(property.BeanReference)) 
					valueForProperty = getBean(property.BeanReference);
				FieldInjection injection = new FieldInjection(instanceToSet, nameOfProperty, valueForProperty);
				
				injection.setProperty();
				LOG.debug("Injected --> name : " + nameOfProperty + " value :" + valueForProperty);
			});
		}
		
		private boolean isEmptyString(String str) {
			return str == null || str.trim().isEmpty();
		}
		
		private Object createNewInstance(String className) {
			Object obj = null;
			try {
				Class<?> clazz = Class.forName(className);
				obj = clazz.newInstance();
				LOG.debug("new instance of", className);
			} catch (Exception e) {
				throw new BiwafInjectionException("failed to create new instance");
			}
			return obj;
		}

	}

}
