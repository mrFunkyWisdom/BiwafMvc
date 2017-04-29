package io.github.ensyb.biwaf.injection;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.scan.ClassPathScann;

public class ClassScannerTest {
	private static final String BASE_PACKAGE = "io.github.ensyb.biwaf.injection";
	
	private static final String CLASS_NAME = BASE_PACKAGE + ".resources.TestBean";
	
	@Test
	public void testLoadClasess(){
		ClassPathScann scanner = new ClassPathScann(BASE_PACKAGE);
		List<Class<?>> loadedClasess = scanner.loadClassesWithAnnotation(Injectable.class);
	
		Class<?> classFile = loadedClasess.stream()
			  //.peek(s-> System.out.println(s.getName()))
				.filter(s -> s.getName().equals(CLASS_NAME))
				.findFirst().get();
		
		assertEquals("name are not equal",CLASS_NAME, classFile.getName());
	}
}
