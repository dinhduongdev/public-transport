package com.publictransport.utils;

import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String normalizeName(String str) {
        String[] words = str.trim().split("\\s+");
        StringBuilder res = new StringBuilder();
        for (String word : words) {
            res.append(word.substring(0, 1).toUpperCase());
            res.append(word.substring(1).toLowerCase());
            res.append(" ");
        }
        return res.toString().trim();
    }

}
