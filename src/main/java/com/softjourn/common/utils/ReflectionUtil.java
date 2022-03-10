package com.softjourn.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.persistence.Id;
import org.springframework.util.ClassUtils;

public class ReflectionUtil {

  public static final Map<Class, Class> WRAPPERS = Collections
      .unmodifiableMap(new HashMap<Class, Class>(){{
        put(byte.class, Byte.class);
        put(char.class, Character.class);
        put(short.class, Short.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(boolean.class, Boolean.class);
      }});

  /**
   * Get name of ID property of entity class
   * @param entityClass entity class mapped with annotations
   * @return name of Id property
   * @throws IllegalArgumentException if class is not entity
   */
  public static String getIdFieldName(Class entityClass) {
    return getIdFieldProperty(entityClass, Field::getName);
  }

  /**
   * Get type of ID property of entity class
   * @param entityClass entity class mapped with annotations
   * @return Class of Id property
   * @throws IllegalArgumentException if class is not entity
   */
  public static Class<?> getIdFieldType(Class entityClass) {
    return getIdFieldProperty(entityClass, Field::getType);
  }

  /**
   * Cast provided value value to provided lass if it is possible
   *
   * Supports casting numeric values from other numeric types (Integer -> BigInt, Integer -> Float)
   * and properly formatted strings,
   * Java8 date-time values from strings formatted in accordance with ISO 8601
   * In general any object that has appropriate constructor can be created
   *
   * @param valueClass class that value needs to be casted to
   * @param value value that needs to be casted
   * @return casted value
   * @throws IllegalArgumentException if value can't be casted ti required Class
   */
  @SuppressWarnings("unchecked")
  public static Object tryToCastValue(Class valueClass, Object value) {
    try {
      if (valueClass.isInstance(value) || isWrapperFor(valueClass, value)) {
        return value;
      } else if (hasAppropriateConstructor(valueClass, value)) {
        return getInstanceByConstructor(valueClass, value);
      } else if (hasValueOfFactoryMethod(valueClass, value)) {
        return getInstanceByValueOf(valueClass, value);
      } else if (value instanceof String && hasParseFactoryMethod(valueClass)) {
        return getInstanceByParse(valueClass, value);
      } else if (value instanceof Integer && Number.class.isAssignableFrom(valueClass)) {
        return valueClass.getConstructor(String.class).newInstance(value.toString());
      } else if (!(value instanceof String)) {
        return tryToCastValue(valueClass, Objects.toString(value));
      }
      throw new IllegalArgumentException("Can't create value of class "
          + valueClass.getName() + " from value " + value.toString());
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Can't create value of class "
          + valueClass.getName() + " from value " + value.toString(), e);
    }
  }

  private static  <P> P getIdFieldProperty(Class entityClass, Function<Field, P> propertyMapper) {
    return Stream.of(entityClass.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .map(propertyMapper)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Can't get ID field of entity " + entityClass));
  }

  @SuppressWarnings("unchecked")
  private static Object getInstanceByConstructor(Class valueClass, Object value) {
    return Stream.of(valueClass.getConstructors())
        .filter(constructor -> constructor.getParameterCount() == 1)
        .filter(
            constructor -> ClassUtils.isAssignableValue(constructor.getParameterTypes()[0], value))
        .findAny()
        .map(constructor -> getInstanceByConstructor(value, constructor))
        .orElseThrow(() -> new IllegalArgumentException(
            "Can't create value of class " + valueClass.getName() + " from value " + value));
  }

  private static Object getInstanceByConstructor(Object value, Constructor constructor) {
    try {
      return constructor.newInstance(value);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("Can't create value of class "
          + constructor.getDeclaringClass().getName() + " from value " + value);
    }
  }

  private static boolean isWrapperFor(Class valueClass, Object value) {
    return valueClass.isPrimitive() && getWrapperClass(valueClass).isInstance(value);
  }

  private static Class getWrapperClass(Class valueClass) {
    return WRAPPERS.get(valueClass);
  }

  @SuppressWarnings("unchecked")
  private static boolean hasAppropriateConstructor(Class valueClass, Object value) {
    return Stream.of(valueClass.getConstructors())
        .filter(constructor -> constructor.getParameterCount() == 1)
        .anyMatch(
            constructor -> ClassUtils.isAssignableValue(constructor.getParameterTypes()[0], value));
  }

  private static Object getInstanceByValueOf(Class valueClass, Object value) {
    return getInstanceByFactoryMethod(valueClass, value, "valueOf");
  }

  private static Object getInstanceByParse(Class valueClass, Object value) {
    return getInstanceByFactoryMethod(valueClass, value, "parse");
  }

  @SuppressWarnings("unchecked")
  private static Object getInstanceByFactoryMethod(
      Class valueClass, Object value, String methodName
  ) {
    return Stream.of(valueClass.getMethods())
        .filter(method -> method.getName().equals(methodName))
        .filter(method -> method.getParameterCount() == 1)
        .map(method -> invokeFactoryMethod(method, value))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(
            "Class " + valueClass + " doesn't contain method " + methodName));
  }

  private static Object invokeFactoryMethod(Method method, Object value) {
    try {
      return method.invoke(null, value);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("Can't create value of class "
          + method.getDeclaringClass().getName() + " from value " + value);
    }
  }

  private static boolean hasValueOfFactoryMethod(Class valueClass, Object value) {
    return hasFactoryMethod(valueClass, "valueOf", value.getClass());
  }

  private static boolean hasParseFactoryMethod(Class valueClass) {
    return hasFactoryMethod(valueClass, "parse", String.class);
  }

  private static boolean hasFactoryMethod(Class clazz, String methodName, Class argumentClass) {
    return Stream.of(clazz.getMethods())
        .filter(method -> method.getName().equals(methodName))
        .filter(method -> method.getParameterCount() == 1)
        .filter(ReflectionUtil::isStatic)
        .anyMatch(method -> method.getParameterTypes()[0].isAssignableFrom(argumentClass));
  }

  private static boolean isStatic(Method method) {
    return Modifier.isStatic(method.getModifiers());
  }
}
