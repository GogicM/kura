/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kura.core.configuration.util;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.kura.configuration.Password;
import org.eclipse.kura.configuration.metatype.AD;
import org.eclipse.kura.configuration.metatype.OCD;
import org.eclipse.kura.configuration.metatype.Scalar;

public class CollectionsUtil {

    public static Map<String, Object> dictionaryToMap(Dictionary<String, Object> dictionary, OCD ocd) {
        if (dictionary == null) {
            return null;
        }

        Map<String, AD> ads = new HashMap<String, AD>();
        if (ocd != null) {
            for (AD ad : ocd.getAD()) {
                ads.put(ad.getId(), ad);
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration<String> keys = dictionary.keys();
        while (keys.hasMoreElements()) {

            String key = keys.nextElement();
            Object value = dictionary.get(key);
            AD ad = ads.get(key);
            if (ad != null && ad.getType() != null && Scalar.PASSWORD.equals(ad.getType())) {
                if (value instanceof char[]) {
                    map.put(key, new Password((char[]) value));
                } else if (value instanceof String[]) {
                    map.put(key, convertStringsToPasswords((String[]) value));
                } else {
                    map.put(key, new Password(value.toString()));
                }
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private static Password[] convertStringsToPasswords(String[] value) {
        Password[] result = new Password[value.length];

        for (int i = 0; i < value.length; i++) {
            result[i] = new Password(value[i]);
        }
        return result;
    }

    public static Dictionary<String, Object> mapToDictionary(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = map.get(key);
            if (value != null) {
                // TODO: this should be removed in next version. Password values
                // should be kept as they are and not mapped to String objects. This
                // was originally done due to Password class not in APIs, but this is
                // not the condition anymore. This change would cause third party code
                // to receive Password objects instead of strings. At the other side,
                // managing everything with Password objects would make everything
                // more logic and consistent.
                if (value instanceof Password) {
                    dictionary.put(key, value.toString());
                } else if (value instanceof Password[]) {
                    Password[] passwordArray = (Password[]) value;
                    String[] passwords = new String[passwordArray.length];
                    for (int i = 0; i < passwordArray.length; i++) {
                        passwords[i] = passwordArray[i].toString();
                    }
                    dictionary.put(key, passwords);
                } else {
                    dictionary.put(key, value);
                }
            }
        }
        return dictionary;
    }
}
