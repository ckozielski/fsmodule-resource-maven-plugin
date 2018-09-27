package com.espirit.ps.psci.moduleresourceplugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.maven.artifact.Artifact;
import org.mockito.Mockito;

public class TestHelper {

	public static void injectToPrivateField(Object element, String fieldName, Object elementToInject) {
		boolean injected = injectToPrivateField(element, fieldName, elementToInject, element.getClass());

		if (element.getClass().getSuperclass() != null) {
			injected = injectToPrivateField(element, fieldName, elementToInject, element.getClass().getSuperclass()) || injected;
		}

		if (injected) {
			return;
		}

		throw new RuntimeException(String.format("unable to inject value to field [name: %s]", fieldName));
	}


	private static boolean injectToPrivateField(Object element, String fieldName, Object elementToInject, Class<?> clazz) {
		Field[] camps = clazz.getDeclaredFields();

		for (Field f : camps) {
			if (f.getName().equals(fieldName)) {
				f.setAccessible(true);
				try {
					f.set(element, elementToInject);
					return true;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(String.format("unable to inject value to field [name: %s]", fieldName), e);
				}
			}
		}
		return false;
	}


	public static Object getPrivateField(Object element, String fieldName) {
		Field[] declaredFields = element.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				try {
					return field.get(element);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalArgumentException(String.format("field with name '%s' not accessible", fieldName), e);
				} finally {
					field.setAccessible(false);
				}
			}
		}
		throw new IllegalArgumentException(String.format("field with name '%s' not found", fieldName));
	}


	public static Object invokePrivateMethod(Object element, String methodName, boolean arg1) {
		Method[] methods = element.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
				method.setAccessible(true);
				try {
					return method.invoke(element, arg1);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(String.format("unable to invoke method [name: %s]", e, methodName));
				}
			}
		}
		throw new RuntimeException(String.format("unable to invoke method [name: %s]", methodName));
	}


	public static Object invokePrivateMethod(Object element, String methodName, Object arg1) {
		Method[] methods = element.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
				method.setAccessible(true);
				try {
					return method.invoke(element, method.getParameters()[0].getType().cast(arg1));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(String.format("unable to invoke method [name: %s]", e, methodName));
				}
			}
		}
		throw new RuntimeException(String.format("unable to invoke method [name: %s]", methodName));
	}


	public static Object invokePrivateMethod(Object element, String methodName) {
		Method[] methods = element.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
				method.setAccessible(true);
				try {
					return method.invoke(element);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(String.format("unable to invoke method [name: %s]", e, methodName));
				}
			}
		}
		throw new RuntimeException(String.format("unable to invoke method [name: %s]", methodName));
	}


	public static Object invokePrivateMethod(Object element, String methodName, Object arg1, Object arg2) {
		Method[] methods = element.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == 2) {
				method.setAccessible(true);
				try {
					return method.invoke(element, method.getParameters()[0].getType().cast(arg1), method.getParameters()[1].getType().cast(arg2));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(String.format("unable to invoke method [name: %s]", e, methodName));
				}
			}
		}
		throw new RuntimeException(String.format("unable to invoke method [name: %s]", methodName));
	}


	public static Object invokePrivateMethod(Object element, String methodName, Object arg1, Object arg2, Object arg3) {
		Method[] methods = element.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == 3) {
				method.setAccessible(true);
				try {
					return method.invoke(element, method.getParameters()[0].getType().cast(arg1), method.getParameters()[1].getType().cast(arg2), method.getParameters()[2].getType().cast(arg3));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(String.format("unable to invoke method [name: %s]", e, methodName));
				}
			}
		}
		throw new RuntimeException(String.format("unable to invoke method [name: %s]", methodName));
	}


	public static Object invokePrivateMethod(Object element, String methodName, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		Method[] methods = element.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName) && method.getParameterCount() == 5) {
				method.setAccessible(true);
				try {
					return method.invoke(element, method.getParameters()[0].getType().cast(arg1), method.getParameters()[1].getType().cast(arg2), method.getParameters()[2].getType().cast(arg3), method.getParameters()[3].getType().cast(arg4), method.getParameters()[4].getType().cast(arg5));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(String.format("unable to invoke method [name: %s]", e, methodName));
				}
			}
		}
		throw new RuntimeException(String.format("unable to invoke method [name: %s]", methodName));
	}


	public static Artifact createArtifact(File artifactFile) {
		Artifact artifact = Mockito.mock(Artifact.class);
		Mockito.when(artifact.getGroupId()).thenReturn("groupId");
		Mockito.when(artifact.getArtifactId()).thenReturn("artifactId");
		Mockito.when(artifact.getVersion()).thenReturn("0.8.15");
		Mockito.when(artifact.getFile()).thenReturn(artifactFile);
		Mockito.when(artifact.getScope()).thenReturn("compile");
		return artifact;
	}


	public static Artifact createArtifact() {
		File artifactFile = Mockito.mock(File.class);
		Mockito.when(artifactFile.getName()).thenReturn("filename.ext");
		return createArtifact(artifactFile);
	}
}
