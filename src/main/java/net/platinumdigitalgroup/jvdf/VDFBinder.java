/*
Copyright 2017 Platinum Digital Group LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.platinumdigitalgroup.jvdf;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Binds VDF documents to Java objects.
 * @author Brendan Heinonen
 */
public class VDFBinder {

    private final VDFNode rootNode;

    /**
     * Initializes the VDF binder with a VDF root node.
     * @param root the VDF root node
     */
    public VDFBinder(VDFNode root) {
        this.rootNode = root;
    }

    /**
     * Binds the root node to the specified POJO object.
     * @param obj the POJO to bind the VDF node to
     */
    public void bindTo(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Arrays.stream(fields)
                .parallel()
                .filter(f -> f.isAnnotationPresent(VDFBindField.class))
                .forEach(f -> bindField(obj, f));
    }

    /**
     * Binds the VDF node to a field
     * @param f the field to bind
     */
    public void bindField(Object obj, Field f) {
        VDFBindField annotation = f.getAnnotation(VDFBindField.class);

        String keyName = annotation.keyName();

        // If the annotation keyname is not defined, use the field's name as the key
        if(keyName.length() == 0) {
            keyName = f.getName();
        }

        if(!rootNode.containsKey(keyName))
            return;

        try {
            Type t = f.getType();
            if(t == String.class) {
                bindString(obj, f, keyName);
            } else if(t == VDFNode.class) {
                bindNodeSimple(obj, f, keyName);
            } else if(t == int.class) {
                bindInt(obj, f, keyName);
            } else if(t == float.class) {
                bindFloat(obj, f, keyName);
            } else if(t == long.class) {
                bindLong(obj, f, keyName);
            } else if(t == Color.class) {
                bindColor(obj, f, keyName);
            } else {
                bindNode(obj, f, keyName);
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    private void bindNode(Object obj, Field f, String key)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        VDFNode node = rootNode.getSubNode(key);
        Class<?> type = f.getType();

        Object newObj = createType(obj, type);
        new VDFBinder(node).bindTo(newObj);

        f.set(obj, newObj);
    }

    private Object createType(Object parent, Class<?> type)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        if(type.isMemberClass()) {
            Class<?> parentType = type.getDeclaringClass();
            Constructor<?> ctor = type.getDeclaredConstructor(parentType);

            if(ctor != null) {
                return ctor.newInstance(parent);
            }

            return null;
        } else {
            return type.newInstance();
        }
    }

    private void bindInt(Object obj, Field f, String key) throws IllegalAccessException {
        f.setInt(obj, rootNode.getInt(key));
    }

    private void bindFloat(Object obj, Field f, String key) throws IllegalAccessException {
        f.setFloat(obj, rootNode.getFloat(key));
    }

    private void bindLong(Object obj, Field f, String key) throws IllegalAccessException {
        f.setLong(obj, rootNode.getLong(key));
    }

    private void bindString(Object obj, Field f, String key) throws IllegalAccessException {
        f.set(obj, rootNode.getString(key));
    }

    private void bindNodeSimple(Object obj, Field f, String key) throws IllegalAccessException {
        f.set(obj, rootNode.getSubNode(key));
    }

    private void bindColor(Object obj, Field f, String key) throws IllegalAccessException {
        f.set(obj, rootNode.getColor(key));
    }

}
