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
		return invokePrivateMethod(instance, methodName, parameters, Object.class);
	}

	/**
	 * Executes private methods from another classes.
	 * @param instance Instance of the class that is tests
	 * @param methodName Name of the method to test
	 * @param parameters Parameters to pass to the method that is tested
	 * @return Object Object with the return value of the executed method
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public Object invokePrivateMethod(Object instance, String methodName,
			Object parameters[], Class<Object> parameterTypes) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
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
	 * Executes private methods from another classes.
	 * @param instance
	 * @param methodName
	 * @param parameters
	 * @return Object that was returned by the invoked method
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object invokePrivateMethodWithIntegerParameters(Object instance, String methodName,
			Object parameters[]) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object result = null;
		final Method[] methods = instance.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				method.setAccessible(true);
				result = method.invoke(instance, convertIntegerArray(parameters));					
				break;
			}
		}
		return result;
	}
	
	/**
	 * Retrieves value of a private field. 
	 * @param instance Instance of the class that is tested
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
	
	/**
	 * Changes the value of a private boolean field.
	 * @param instance Instance of the class that is tested
	 * @param fieldName Name of the boolean field to be changed
	 * @param value new value to set
	 * @return Object with changed field value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object changePrivateBooleanField(Object instance, String fieldName, boolean value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.setBoolean(instance, Boolean.valueOf(value));
		return instance;
	}
	
	/**
	 * Returns an int[] with the same values as the provided Integers
	 * @param input an Object[] with Integers
	 * @return int[] with same values
	 */
	private int[] convertIntegerArray(Object[] input) {
		int[] result = new int[input.length];
		int index = 0;
		for (Object value : input) {
			result[index] = ((Integer)value).intValue();
			index++;
		}
		return result;
	}
}
