package br.com.grupojgv.utils;

public class StringUtils extends com.sankhya.util.StringUtils {
    public static String asNullOrString(Object s) {
        try {
            return s.toString();
        } catch (Throwable e) {
            return null;
        }
    }
}
