/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill;

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
