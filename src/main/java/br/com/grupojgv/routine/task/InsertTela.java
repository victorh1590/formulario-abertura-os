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
import java.util.StringJoiner;

@JBossLog
public class InsertTela implements TarefaJava {
    @Override
    public void executar(ContextoTarefa contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO TGFPAR_TNF </START><%%%");
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;
        NativeSql query = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            query = new NativeSql(jdbc);

            StringJoiner fields = (new StringJoiner(", "))
                .add("TNF_IDINSTPRN").add("TNF_IDTAREFA").add("TNF_ESCOPO")
                .add("NOMEPARC");

            StringJoiner params = (new StringJoiner(", "))
                .add(":IDINSTPRN").add(":IDTAREFA").add(":ESCOPO")
                .add(":NOMEPARC");

            query.appendSql("INSERT INTO TGFPAR_TNF (TNF_IDINSTPRN, TNF_IDTAREFA, TNF_ESCOPO, NOMEPARC) ");
            query.appendSql("VALUES (:IDINSTPRN, :IDTAREFA, :ESCOPO, :NOMEPARC)");

            BigDecimal idinstprn = (BigDecimal) contexto.getIdInstanceProcesso();
            @Nullable String idtarefa = SafeUtils.tryGet(() -> {
                ProcessoQuery processoQuery = new ProcessoQuery();
                return processoQuery.IDELEMENTO(idinstprn, "Parceiro");
            }).orElse(null);

            query.setNamedParameter("IDINSTPRN", idinstprn);
            query.setNamedParameter("NOMEPARC", "ABC");
            query.setNamedParameter("IDTAREFA", idtarefa);
            query.setNamedParameter("ESCOPO", "P");

            query.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inserting into TNF table: " + e.getMessage());
        } finally {
            NativeSql.releaseResources(query);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
            log.info("%%%><END> INICIALIZANDO TGFPAR_TNF </END><%%%");
        }
    }
}
