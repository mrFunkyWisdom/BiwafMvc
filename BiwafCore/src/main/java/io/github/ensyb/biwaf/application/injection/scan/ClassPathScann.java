package io.github.ensyb.biwaf.application.injection.scan;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import io.github.ensyb.biwaf.application.injection.BiwafInjectionException;

public class ClassPathScann {

	private final static String CLASS_FILE_SUFFIX = ".class";
	private final static String FILE_PROTOCOL = "file";
	private final static String JAR_PROTOCOL = "jar";

	private final String basePackage;

	public ClassPathScann(String basePackage) {
		this.basePackage = basePackage;
	
	}
	
	public List<Class<?>> loadClassesWithAnnotation(Class<? extends Annotation> annotation) {
		return getClassNames(this.getLoader(), this.basePackage).stream()
				.map(name -> tryLoadClass(this.getLoader(), name))
				.filter(klasa -> klasa.isAnnotationPresent(annotation))
				.collect(Collectors.toList());
	}
	
	public List <Class<?>> loadClasses(){
		return getClassNames(this.getLoader(), this.basePackage).stream()
				.map(clName -> tryLoadClass(this.getLoader(), clName))
				.collect(Collectors.toList());
	}
	
	private List<String> getClassNames(ClassLoader classLoader, String packageName) {
		List<String> classNameList = new ArrayList<>();
		String folderName = packageName.replace('.', '/');
		URL url = classLoader.getResource(folderName);
		if (url == null)
			throw new BiwafInjectionException("Can not find package of name " + folderName);

		String protocol = url.getProtocol();

		try {
			if (FILE_PROTOCOL.equals(protocol)) {
				Arrays.stream(new File(url.toURI()).listFiles())
						.forEach(file -> scanClassNamesFromFile(packageName, file, classNameList));

			}

			if (JAR_PROTOCOL.equals(protocol)) {
				JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
				JarFile jarFile = jarURLConnection.getJarFile();
				scanPackageClassInJar(jarFile, packageName, classNameList);
			}

		} catch (URISyntaxException | IOException e) {
			throw new BiwafInjectionException(e.getMessage());
		}
		return classNameList;
	}

	private void scanClassNamesFromFile(String rootPackageName, File rootFile, List<String> listOfNames) {
		String realName = rootPackageName + "." + rootFile.getName();

		if (rootFile.isFile() && realName.endsWith(CLASS_FILE_SUFFIX)) {
			String getOnlyName = realName.substring(0, realName.length() - CLASS_FILE_SUFFIX.length());
			listOfNames.add(getOnlyName);
		}
		else if (rootFile.isDirectory()) {
			String nextPackageForScanning = rootPackageName + "." + rootFile.getName();
			for (File file : rootFile.listFiles()) {
				scanClassNamesFromFile(nextPackageForScanning, file, listOfNames);
			}
		}
	}

	private void scanPackageClassInJar(JarFile jar, String packageDirName, List<String> classNames) {
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName().replace('/', '.');
			if (name.startsWith(packageDirName) && name.endsWith(CLASS_FILE_SUFFIX)) {
				classNames.add(name.substring(0, name.length() - 6));
			}
		}
	}

	private Class<?> tryLoadClass(final ClassLoader loader, final String className) {
		Class<?> klass = null;
		try {
			klass = (loader != null) ? Class.forName(className, false, loader) 
					: Class.forName(className, false, ClassLoader.getSystemClassLoader());
		} catch (ClassNotFoundException e) {
			throw new BiwafInjectionException("Cannot load class with name " + className);
		}
		return klass;
	}

	private ClassLoader getLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassLoader.class.getClassLoader();
		}
		return loader;
	}
}
