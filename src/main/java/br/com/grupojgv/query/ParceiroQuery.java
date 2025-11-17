package br.com.grupojgv.query;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseQuery;
import br.com.grupojgv.model.Parceiro;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import lombok.extern.jbosslog.JBossLog;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.StringJoiner;

@JBossLog
public class ParceiroQuery extends BaseQuery {
    public Parceiro findOne(BigDecimal codparc) throws Exception {
        String[] fields = new String[] { "CODPARC", "NOMEPARC", "RAZAOSOCIAL", "CGC_CPF" };
        String query = new StringJoiner(System.lineSeparator())
            .add(String.format("SELECT %s", StringUtils.join(fields, ", ")))
            .add("FROM TGFPAR")
            .add(String.format("WHERE CODPARC = %s", codparc.toPlainString()))
            .toString();

        try (CachedRowSet rs = executeQuery(query)) {
            if(rs.next()) {
                return Parceiro.builder()
                    .CODPARC(BigDecimalUtil.getValueOrZero(rs.getBigDecimal("CODPARC")))
                    .NOMEPARC(StringUtils.getEmptyAsNull(rs.getString("NOMEPARC")))
                    .RAZAOSOCIAL(StringUtils.getEmptyAsNull(rs.getString("RAZAOSOCIAL")))
                    .CGC_CPF(StringUtils.getEmptyAsNull(rs.getString("CGC_CPF")))
                    .build();
            }
        }

        return null;
    }
}
