package br.com.grupojgv.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

public class ClobConverter {
    public static char[] clobToCharArray(Clob clob) throws IOException, SQLException {
        if(clob == null) {
            return null;
        }
        try (Reader reader = clob.getCharacterStream()) {
            return IOUtils.toCharArray(reader);
        }
    }
}
