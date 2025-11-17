package br.com.grupojgv.query;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseQuery;
import br.com.grupojgv.model.FormAberturaOS;
import br.com.grupojgv.utils.ClobConverter;
import com.sankhya.util.StringUtils;
import lombok.extern.jbosslog.JBossLog;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.util.StringJoiner;

@JBossLog
public class FormAberturaOSQuery extends BaseQuery {
    public FormAberturaOS buscar(
        BigDecimal idinstprn,
        BigDecimal idinsttar,
        BigDecimal codregistro
    ) throws Exception {
        String[] fields = new String[] {
            "IDINSTPRN",
            "IDINSTTAR",
            "CODREGISTRO",
            "IDTAREFA",
            "SOLICITACAO",
            "CODPARC",
            "NOMEPARC",
            "RAZAOSOCIAL",
            "CGC_CPF",
            "CODVEICULO",
            "CODMARCA",
            "CODMODELO",
            "CODTIPO",
            "HORIMETRO",
            "PROBLEMA"
        };

        String sql = (new StringJoiner(System.lineSeparator()))
            .add("SELECT " + StringUtils.join(fields, ", "))
            .add("FROM AD_ABERTURAOS")
            .add(
                "WHERE " +
                "IDINSTPRN = " + idinstprn + " AND " +
                "IDINSTTAR = " + idinsttar + " AND " +
                "CODREGISTRO = " + codregistro
            )
            .toString();

        log.info("SQL: " + sql);
        try(CachedRowSet rs = executeQuery(sql)) {
            if(rs.next()) {
                return FormAberturaOS.builder()
                    .IDINSTPRN(rs.getBigDecimal("IDINSTPRN"))
                    .IDINSTTAR(rs.getBigDecimal("IDINSTTAR"))
                    .CODREGISTRO(rs.getBigDecimal("CODREGISTRO"))
                    .IDTAREFA(rs.getString("IDTAREFA"))
                    .SOLICITACAO(ClobConverter.clobToCharArray(rs.getClob(    "SOLICITACAO")))
                    .CODPARC(rs.getBigDecimal("CODPARC"))
                    .NOMEPARC(rs.getString("NOMEPARC"))
                    .RAZAOSOCIAL(rs.getString("RAZAOSOCIAL"))
                    .CGC_CPF(rs.getString("CGC_CPF"))
                    .CODVEICULO(rs.getBigDecimal("CODVEICULO"))
                    .CODMARCA(rs.getBigDecimal("CODMARCA"))
                    .CODMODELO(rs.getBigDecimal("CODMODELO"))
                    .CODTIPO(rs.getBigDecimal("CODTIPO"))
                    .HORIMETRO(rs.getBigDecimal("HORIMETRO"))
                    .PROBLEMA(ClobConverter.clobToCharArray(rs.getClob("PROBLEMA")))
                    .build();
            }
        }

        return null;
    }
}
