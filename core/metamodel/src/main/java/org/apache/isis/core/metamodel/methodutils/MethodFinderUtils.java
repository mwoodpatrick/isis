/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.metamodel.methodutils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.isis.core.commons.lang.JavaClassUtils;
import org.apache.isis.core.commons.lang.WrapperUtils;

/**
 * TODO: remove duplication with {@link WrapperUtils} and {@link PrimitiveUtils}
 * .
 */
public final class MethodFinderUtils {
    private static Map<Class<?>, Class<?>> boxedClasses = Maps.newHashMap();

    static {
        // TODO: there is a better way of doing this in 1.6 using TypeMirror
        boxedClasses.put(boolean.class, Boolean.class);
        boxedClasses.put(char.class, Character.class);
        boxedClasses.put(byte.class, Byte.class);
        boxedClasses.put(short.class, Short.class);
        boxedClasses.put(int.class, Integer.class);
        boxedClasses.put(long.class, Long.class);
        boxedClasses.put(float.class, Float.class);
        boxedClasses.put(double.class, Double.class);
        boxedClasses.put(void.class, Void.class);
    }

    private MethodFinderUtils() {
    }

    /**
     * Searches the supplied array of methods for specific method and returns
     * it, also removing it from supplied array if found (by setting to
     * <tt>null</tt>).
     * 
     * <p>
     * Any methods that do not meet the search criteria are left in the array of
     * methods.
     * 
     * <p>
     * The search algorithm is as specified in
     * {@link #findMethodIndex(List, MethodScope, String, Class, Class[])}.
     */
    public static Method removeMethod(final List<Method> methods, final MethodScope methodScope, final String name, final Class<?> returnType, final Class<?>[] paramTypes) {
        final int idx = findMethodIndex(methods, methodScope, name, returnType, paramTypes);
        if (idx != -1) {
            final Method method = methods.get(idx);
            methods.set(idx, null);
            return method;
        }
        return null;
    }

    /**
     * Searches the supplied array of methods for specific method and returns
     * it,.
     * 
     * <p>
     * The search algorithm is as specified in
     * {@link #findMethodIndex(List, MethodScope, String, Class, Class[])}.
     */
    public static Method findMethod(final List<Method> methods, final MethodScope methodScope, final String name, final Class<?> returnType, final Class<?>[] paramTypes) {
        final int idx = findMethodIndex(methods, methodScope, name, returnType, paramTypes);
        return idx != -1 ? methods.get(idx) : null;
    }

    /**
     * Searches the supplied array of methods for specific method and returns
     * its index, otherwise returns <tt>-1</tt>.
     * 
     * <p>
     * The search algorithm is:
     * <ul>
     * <li>has the specified prefix</li>
     * <li>has the specified return type, or <tt>void</tt> if canBeVoid is
     * <tt>true</tt> (but see below)</li>
     * <li>has the specified number of parameters</li>
     * </ul>
     * If the returnType is specified as null then the return type is ignored.
     */
    public static int findMethodIndex(final List<Method> methods, final MethodScope methodScope, final String name, final Class<?> returnType, final Class<?>[] paramTypes) {
        int idx = -1;
        method: for (int i = 0; i < methods.size(); i++) {
            if (methods.get(i) == null) {
                continue;
            }

            final Method method = methods.get(i);
            final int modifiers = method.getModifiers();

            // check for public modifier
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }

            // check for static modifier
            if (!inScope(methodScope, method)) {
                continue;
            }

            // check for name
            if (!method.getName().equals(name)) {
                continue;
            }

            // check for return type
            if (returnType != null && returnType != method.getReturnType()) {
                continue;
            }

            // check params (if required)
            if (paramTypes != null) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if (paramTypes.length != parameterTypes.length) {
                    continue;
                }

                for (int c = 0; c < paramTypes.length; c++) {
                    if ((paramTypes[c] != null) && (paramTypes[c] != parameterTypes[c])) {
                        continue method;
                    }
                }
            }
            idx = i;
            break;
        }
        return idx;
    }

    public static boolean inScope(final MethodScope methodScope, final Method method) {
        final boolean isStatic = JavaClassUtils.isStatic(method);
        return isStatic && methodScope == MethodScope.CLASS || !isStatic && methodScope == MethodScope.OBJECT;
    }

    /**
     * Searches the supplied array of methods for all specific methods and
     * returns them, also removing them from supplied array if found.
     * 
     * <p>
     * Any methods that do not meet the search criteria are left in the array of
     * methods.
     * 
     * <p>
     * The search algorithm is:
     * <ul>
     * <li>has the specified prefix</li>
     * <li>has the specified return type, or <tt>void</tt> if canBeVoid is
     * <tt>true</tt> (but see below)</li>
     * <li>has the specified number of parameters</li>
     * </ul>
     * If the returnType is specified as null then the return type is ignored.
     * 
     * @param forClass
     * @param name
     * @param returnType
     * @param paramTypes
     *            the set of parameters the method should have, if null then is
     *            ignored
     * @return Method
     */
    public static List<Method> removeMethods(final List<Method> methods, final MethodScope forClass, final String prefix, final Class<?> returnType, final boolean canBeVoid, final int paramCount) {

        final List<Method> validMethods = new ArrayList<Method>();

        for (int i = 0; i < methods.size(); i++) {
            if (methods.get(i) == null) {
                continue;
            }

            final Method method = methods.get(i);

            if (!inScope(forClass, method)) {
                continue;
            }

            final boolean goodPrefix = method.getName().startsWith(prefix);

            final boolean goodCount = method.getParameterTypes().length == paramCount;
            final Class<?> type = method.getReturnType();
            final boolean goodReturn = returnTypeCompatible(returnType, canBeVoid, type);

            if (goodPrefix && goodCount && goodReturn) {
                validMethods.add(method);
                methods.set(i, null);
            }
        }
        return validMethods;
    }

    private static boolean returnTypeCompatible(final Class<?> returnType, final boolean canBeVoid, final Class<?> type) {
        if (returnType == null) {
            return true;
        }
        if (canBeVoid && (type == void.class)) {
            return true;
        }

        if (type.isPrimitive()) {
            return returnType.isAssignableFrom(boxedClasses.get(type));
        }

        return (returnType.isAssignableFrom(type));
    }

    /**
     * From the supplied method array, finds but <i>does not remove</i> methods
     * that have the required prefix, and adds to the supplied candidates
     * vector.
     */
    public static void findPrefixedInstanceMethods(final Method[] methods, final String prefix, final List<Method> candidates) {
        for (final Method method : methods) {
            if (method == null) {
                continue;
            }

            if (JavaClassUtils.isStatic(method)) {
                continue;
            }

            final boolean goodPrefix = method.getName().startsWith(prefix);
            final boolean goodCount = method.getParameterTypes().length == 0;

            if (goodPrefix && goodCount) {
                candidates.add(method);
            }
        }
    }
}