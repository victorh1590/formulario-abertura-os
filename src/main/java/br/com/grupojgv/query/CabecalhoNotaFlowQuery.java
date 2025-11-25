package br.com.grupojgv.query;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseQuery;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.util.StringJoiner;

public class CabecalhoNotaFlowQuery extends BaseQuery {
    public BigDecimal findPk(Object idinstprn, Object idinsttar) throws Exception {
        String[] fields = new String[] {
            "NUNOTA"
        };

        String query = new StringJoiner(System.lineSeparator())
            .add(String.format("SELECT %s", StringUtils.join(fields, ", ")))
            .add("FROM TGFCAB_TNF")
            .add(String.format("WHERE TNF_IDINSTPRN = %s AND TNF_IDINSTTAR = %s", idinstprn, idinsttar))
            .toString();

        try (CachedRowSet rs = executeQuery(query)) {
            if(rs.next()) {
                return BigDecimalUtil.getValue(rs.getBigDecimal("NUNOTA"), null);
            }
        }

        return null;
    }
}
