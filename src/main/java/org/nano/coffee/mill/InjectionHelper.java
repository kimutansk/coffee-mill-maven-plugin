package org.nano.coffee.mill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Injection helper.
 */
public class InjectionHelper {

    public static Logger LOGGER = LoggerFactory.getLogger(InjectionHelper.class);

    public static void inject(Object obj, Class clazz, String field, Object value) {
        try {
            Field theField = clazz.getDeclaredField(field);
            theField.setAccessible(true);
            theField.set(obj, value);
        } catch (NoSuchFieldException e) {
            try {
                Field theField  = clazz.getField(field);
                theField.setAccessible(true);
                theField.set(obj, value);
            } catch (NoSuchFieldException e1) {
                LOGGER.error("Internal error - Cannot inject " + field + " in " + clazz.getName(), e);
            } catch (IllegalAccessException e1) {
                LOGGER.error("Internal error - Cannot inject " + field + " in " + clazz.getName(), e);
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("Internal error - Cannot inject " + field + " in " + clazz.getName(), e);
        }

    }

}
