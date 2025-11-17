package br.com.grupojgv.query;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseQuery;
import br.com.grupojgv.model.Veiculo;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import lombok.extern.jbosslog.JBossLog;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.StringJoiner;

@JBossLog
public class VeiculoQuery extends BaseQuery {
    public Veiculo findOne(BigDecimal codveiculo) throws Exception {
        String[] fields = new String[] {
            "CODVEICULO",
            "AD_CODIGO CODMARCA",
            "AD_NROUNICOMODELO CODMODELO",
            "AD_NROUNICOESPECIE CODTIPO"
        };
        String query = new StringJoiner(System.lineSeparator())
            .add(String.format("SELECT %s", StringUtils.join(fields, ", ")))
            .add("FROM TGFVEI")
            .add(String.format("WHERE CODVEICULO = %s", codveiculo.toPlainString()))
            .toString();

        try (CachedRowSet rs = executeQuery(query)) {
            if(rs.next()) {
                return Veiculo.builder()
                    .CODVEICULO(BigDecimalUtil.getValueOrZero(rs.getBigDecimal("CODVEICULO")))
                    .CODMARCA(BigDecimalUtil.getValueOrZero(rs.getBigDecimal("CODMARCA")))
                    .CODMODELO(BigDecimalUtil.getValueOrZero(rs.getBigDecimal("CODMODELO")))
                    .CODTIPO(BigDecimalUtil.getValueOrZero(rs.getBigDecimal("CODTIPO")))
                    .build();
            }
        }

        return null;
    }
}
