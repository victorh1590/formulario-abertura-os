package br.com.grupojgv.routine.task;

import br.com.grupojgv.query.ProcessoQuery;
import br.com.grupojgv.utils.SafeUtils;
import br.com.sankhya.extensions.flow.ContextoTarefa;
import br.com.sankhya.extensions.flow.TarefaJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import lombok.extern.jbosslog.JBossLog;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@JBossLog
public class InsertNativo implements TarefaJava {
    @Override
    public void executar(ContextoTarefa contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO AD_TESTETAB_WF </START><%%%");
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;
        NativeSql query = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            query = new NativeSql(jdbc);

            query.appendSql("INSERT INTO AD_TESTETAB_WF (SW_IDINSTPRN, SW_IDINSTTAR, SW_IDTAREFA, SW_DBSTATE, SW_SEQREGISTRO, SW_MODIFIED, DESCRICAO) ");
            query.appendSql("VALUES (:IDINSTPRN, :IDINSTTAR, :IDTAREFA, :DBSTATE, :SEQREGISTRO, :MODIFIED, :DESCRICAO)");

            BigDecimal idinstprn = (BigDecimal) contexto.getIdInstanceProcesso();
            @Nullable String idtarefa = SafeUtils.tryGet(() -> {
                ProcessoQuery processoQuery = new ProcessoQuery();
                return processoQuery.IDELEMENTO(idinstprn, "AD_TESTETAB");
            }).orElse(null);

            query.setNamedParameter("IDINSTPRN", idinstprn);
            query.setNamedParameter("IDINSTTAR", BigDecimal.ZERO);
            query.setNamedParameter("IDTAREFA", idtarefa);
            query.setNamedParameter("DBSTATE", "I");
            query.setNamedParameter("SEQREGISTRO", 0);
            query.setNamedParameter("MODIFIED", "DESCRICAO");
            query.setNamedParameter("DESCRICAO", "ABC");
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inserting into TNF table: " + e.getMessage());
        } finally {
            NativeSql.releaseResources(query);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
            log.info("%%%><END> INICIALIZANDO AD_TESTETAB_WF </END><%%%");
        }
    }
}
