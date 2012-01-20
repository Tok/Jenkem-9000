package jenkem.client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public abstract class AbstractReflectionTestCase extends TestCase {
	
	/**
	 * Executes private methods from other classes.
	 * @param instance Instance of the class that is tests
	 * @param methodName Name of the method to test
	 * @param parameters Parameters to pass to the method that is tested
	 * @return Object Object with the return value of the executed method
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public Object invokePrivateMethod(Object instance, String methodName,
			Object parameters[]) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object result = null;
		final Method[] methods = instance.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				method.setAccessible(true);
				result = method.invoke(instance, parameters);
				break;
			}
		}
		return result;
	}
	
	/**
	 * Retrieves value of a private field. 
	 * @param instance Instance of the class that is tests
	 * @param fieldName Name of the field to retrieve
	 * @return Object value of the field
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object retrievePrivateField(Object instance, String fieldName) throws IllegalArgumentException, IllegalAccessException {
		Object result = null;
	    final Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				result = field.get(instance);
				break;
			}
		}
		return result;
	}
}
