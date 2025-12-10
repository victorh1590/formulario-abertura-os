package br.com.grupojgv.query;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseQuery;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringJoiner;

@JBossLog
public class ProcessoQuery extends BaseQuery {
    public String findIdTarefa(BigDecimal idinstprn, String nome) throws Exception {
        String idTarefa = null;
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            jdbc.openSession();

            PreparedStatement pstm = jdbc.getPreparedStatement(
                (new StringJoiner(System.lineSeparator()))
                    .add("SELECT IDELEMENTO ")
                    .add("FROM TWFELE JOIN TWFIPRN USING(CODPRN, VERSAO) ")
                    .add("WHERE IDINSTPRN = ? AND NOME = ? AND TIPO = 'T' ")
                    .toString()
            );

            pstm.setBigDecimal(1, idinstprn);
            pstm.setString(2, nome);

            try (ResultSet rs = pstm.executeQuery();) {
                if(rs.next()) {
                    idTarefa = rs.getString("IDELEMENTO");
                }
            } finally {
                pstm.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inserting into TNF table: " + e.getMessage());
        } finally {
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
        }
        return idTarefa;
    }
}
