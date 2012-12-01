package jenkem.client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public abstract class AbstractReflectionTestCase extends TestCase {

    /**
     * Executes private methods from other classes.
     *
     * @param instance
     *            Instance of the class that is tests
     * @param methodName
     *            Name of the method to test
     * @param parameters
     *            Parameters to pass to the method that is tested
     * @return Object Object with the return value of the executed method
     * @throws InvocationTargetException
     *             throwable
     * @throws IllegalAccessException
     *             throwable
     * @throws IllegalArgumentException
     *             throwable
     */
    public final Object invokePrivateMethod(final Object instance,
            final String methodName, final Object[] parameters)
            throws IllegalAccessException, InvocationTargetException {
        return invokePrivateMethod(instance, methodName, parameters, Object.class);
    }

    /**
     * Executes private methods from another classes.
     *
     * @param instance
     *            Instance of the class that is tests
     * @param methodName
     *            Name of the method to test
     * @param parameters
     *            Parameters to pass to the method that is tested
     * @param parameterTypes
     *            Parameter type
     * @return Object Object with the return value of the executed method
     * @throws InvocationTargetException
     *             throwable
     * @throws IllegalAccessException
     *             throwable
     * @throws IllegalArgumentException
     *             throwable
     */
    public final Object invokePrivateMethod(final Object instance,
            final String methodName, final Object[] parameters,
            final Class<Object> parameterTypes)
            throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        final Method[] methods = instance.getClass().getDeclaredMethods();
        for (final Method method : methods) {
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
     *
     * @param instance
     *            instance of the object on which the method is invoked
     * @param methodName
     *            name of the method to invoke
     * @param parameters
     *            parameters to pass to the invoked method
     * @return Object that was returned by the invoked method
     * @throws IllegalArgumentException
     *             throwable
     * @throws IllegalAccessException
     *             throwable
     * @throws InvocationTargetException
     *             throwable
     */
    public final Object invokePrivateMethodWithIntegerParameters(
            final Object instance, final String methodName,
            final Object[] parameters) throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        final Method[] methods = instance.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.setAccessible(true);
                result = method.invoke(instance,
                        convertIntegerArray(parameters));
                break;
            }
        }
        return result;
    }

    /**
     * Retrieves value of a private field.
     *
     * @param instance
     *            Instance of the class that is tested
     * @param fieldName
     *            Name of the field to retrieve
     * @return Object value of the field
     * @throws IllegalArgumentException
     *             throwable
     * @throws IllegalAccessException
     *             throwable
     */
    public final Object retrievePrivateField(final Object instance,
            final String fieldName) throws IllegalAccessException {
        Object result = null;
        final Field[] fields = instance.getClass().getDeclaredFields();
        for (final Field field : fields) {
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
     *
     * @param instance
     *            Instance of the class that is tested
     * @param fieldName
     *            Name of the boolean field to be changed
     * @param value
     *            new value to set
     * @return Object with changed field value
     * @throws SecurityException
     *             throwable
     * @throws NoSuchFieldException
     *             throwable
     * @throws IllegalArgumentException
     *             throwable
     * @throws IllegalAccessException
     *             throwable
     */
    public final Object changePrivateBooleanField(final Object instance,
            final String fieldName, final boolean value)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(instance, Boolean.valueOf(value));
        return instance;
    }

    /**
     * Returns an int[] with the same values as the provided Integers.
     *
     * @param input
     *            an Object[] with Integers
     * @return int[] with same values
     */
    private int[] convertIntegerArray(final Object[] input) {
        final int[] result = new int[input.length];
        int index = 0;
        for (final Object value : input) {
            result[index] = ((Integer) value).intValue();
            index++;
        }
        return result;
    }
}
