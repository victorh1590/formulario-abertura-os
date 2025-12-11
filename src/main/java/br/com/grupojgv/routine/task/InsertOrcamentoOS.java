package br.com.grupojgv.routine.task;

import br.com.grupojgv.metadata.Flow;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
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
public class InsertOrcamentoOS implements TarefaJava {
    @Override
    public void executar(ContextoTarefa contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO TGFCAB_TNF </START><%%%");
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;
        NativeSql insert = null;
        NativeSql delete = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();

            @Nullable BigDecimal idinstprn = (BigDecimal) contexto.getIdInstanceProcesso();
            String idTarefa = Flow.ORCAMENTO.getIdtarefa();
            delete = new NativeSql(jdbc);
            delete.appendSql("DELETE FROM TGFCAB_TNF WHERE TNF_IDINSTPRN = :IDINSTPRN AND TNF_IDTAREFA = :IDTAREFA");
            delete.setNamedParameter("IDINSTPRN", idinstprn);
            delete.setNamedParameter("IDTAREFA", idTarefa);
            delete.executeUpdate();
            NativeSql.releaseResources(delete);

            insert = new NativeSql(jdbc);
            StringJoiner fields = (new StringJoiner(", "))
                .add("TNF_IDINSTPRN").add("TNF_IDINSTTAR")
                .add("TNF_IDTAREFA").add("TNF_ESCOPO")
                .add("CODPARC").add("CODEMP").add("CODNAT")
                .add("CODCENCUS").add("CODVEICULO").add("AD_CODOAT")
//                .add("AD_TIPODEOS")
                .add("NUMNOTA").add("AD_HORIMETRO")
                .add("AD_FLAGVEIDIFPARC");

            StringJoiner params = (new StringJoiner(", "))
                .add(":IDINSTPRN").add(":IDINSTTAR")
                .add(":IDTAREFA").add(":ESCOPO")
                .add(":CODPARC").add(":CODEMP").add(":CODNAT")
                .add(":CODCENCUS").add(":CODVEICULO").add(":AD_CODOAT")
//                .add(":AD_TIPODEOS")
                .add(":NUMNOTA").add(":AD_HORIMETRO")
                .add(":AD_FLAGVEIDIFPARC");

            insert.appendSql(String.format("INSERT INTO TGFCAB_TNF (%s) ", fields));
            insert.appendSql(String.format("VALUES (%s)", params));

            // Dados da Pré OS
            QueryExecutor qe = contexto.getQuery();
            String aberturaOS = "SELECT CODPARC, CODVEICULO, HORIMETRO FROM AD_ABERTURAOS WHERE IDINSTPRN = " + idinstprn;
            qe.nativeSelect(aberturaOS);
            if(qe.next()) {
                String escopo = "P";
                BigDecimal codparc = qe.getBigDecimal("CODPARC");
                BigDecimal codemp = new BigDecimal(4);
                BigDecimal codnat = new BigDecimal("20301001");
                BigDecimal codcencus = new BigDecimal("010401005");
                BigDecimal codveiculo = qe.getBigDecimal("CODVEICULO");
                // Oficina
                BigDecimal adcodoat = new BigDecimal(6);
                // 1	GARANTIA
                // 2	ATENDIMENTO CLIENTE
//                BigDecimal adtipodeos = new BigDecimal(2);
                BigDecimal numnota = BigDecimal.ZERO;
                BigDecimal adhorimetro = qe.getBigDecimal("HORIMETRO");
                String adflagveidifparc = "S";

                // Add parameters and save instance of task
                insert.setNamedParameter("IDINSTPRN", idinstprn);
//                insert.setNamedParameter("IDINSTTAR", BigDecimal.ZERO);
                insert.setNamedParameter("IDTAREFA", Flow.ORCAMENTO.getIdtarefa());
                insert.setNamedParameter("ESCOPO", escopo);
                insert.setNamedParameter("CODPARC", codparc);
                insert.setNamedParameter("CODEMP", codemp);
                insert.setNamedParameter("CODNAT", codnat);
                insert.setNamedParameter("CODCENCUS", codcencus);
                insert.setNamedParameter("CODVEICULO", codveiculo);
                insert.setNamedParameter("AD_CODOAT", adcodoat);
//                insert.setNamedParameter("AD_TIPODEOS", adtipodeos);
                insert.setNamedParameter("NUMNOTA", numnota);
                insert.setNamedParameter("AD_HORIMETRO", adhorimetro);
                insert.setNamedParameter("AD_FLAGVEIDIFPARC", adflagveidifparc);

                String str = insert.getSqlBuf().toString();
                log.info(str);

                insert.executeUpdate();
            } else {
                throw new Exception("Não foi possível gerar o Orçamento.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inserting into TNF table: " + e.getMessage());
        } finally {
            NativeSql.releaseResources(insert);
            NativeSql.releaseResources(delete);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
            log.info("%%%><END> INICIALIZANDO TGFCAB_TNF </END><%%%");
        }
    }
}