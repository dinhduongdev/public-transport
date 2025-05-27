package com.publictransport.utils;

import java.util.Map;

public class MapUtils {
    public static void putIfNotEmpty(Map map, Object key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            map.put(key, value);
        }
    }
}
