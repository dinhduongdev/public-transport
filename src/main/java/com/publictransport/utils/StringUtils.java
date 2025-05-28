package com.publictransport.utils;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotEmpty(String... strs) {
        for (String str : strs) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean allOrNoneNotEmpty(String... strs) {
        // nếu tất cả đều không rỗng: true
        // nếu tất cả đều rỗng: true
        // Tham khảo: Xnor
        boolean anyNotEmpty = false;
        boolean anyEmpty = false;
        for (String str : strs) {
            if (isEmpty(str)) {
                anyEmpty = true;
            } else {
                anyNotEmpty = true;
            }
            if (anyNotEmpty && anyEmpty) {
                return false;
            }
        }
        return true;
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
