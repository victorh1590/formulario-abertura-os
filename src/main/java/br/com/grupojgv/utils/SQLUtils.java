package br.com.grupojgv.utils;

import javax.annotation.Nullable;

public class SQLUtils {
    public static String formatNULL(@Nullable Object o) {
        if(o == null) {
            return "NULL";
        }
        String s = o.toString();
        if(s.isEmpty()) {
            return "NULL";
        }
        return s;
    }
}
