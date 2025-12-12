package br.com.grupojgv.routine.task;

import br.com.grupojgv.metadata.Flow;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.flow.ContextoTarefa;
import br.com.sankhya.extensions.flow.TarefaJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.workflow.model.engine.DefaultProcessEngineService;
import lombok.extern.jbosslog.JBossLog;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.StringJoiner;

@JBossLog
public class InsertOrcamentoOSNativo implements TarefaJava {
    @Override
    public void executar(ContextoTarefa contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO TGFCAB_WF </START><%%%");
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;
        NativeSql insert = null;
        NativeSql twfivarUpd = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            insert = new NativeSql(jdbc);

            @Nullable BigDecimal idinstprn = (BigDecimal) contexto.getIdInstanceProcesso();
            StringJoiner fields =
                (new StringJoiner(", "))
                    .add("SW_IDINSTPRN")
                    .add("SW_IDINSTTAR")
                    .add("SW_IDTAREFA")
                    .add("SW_DBSTATE").add("SW_SEQREGISTRO").add("SW_MODIFIED");

            StringJoiner modified =
                (new StringJoiner(", "))
                    .add("CODPARC").add("CODEMP").add("CODNAT")
                    .add("CODCENCUS").add("CODVEICULO").add("AD_CODOAT")
                    .add("NUMNOTA").add("AD_HORIMETRO").add("CODTIPOPER")
                    .add("CODTIPVENDA").add("AD_FLAGVEIDIFPARC")
                    .add("AD_CODTIPODEOS");

            StringJoiner params =
                (new StringJoiner(", "))
                    .add(":IDINSTPRN")
                    .add(":IDINSTTAR")
                    .add(":IDTAREFA")
                    .add(":DBSTATE").add(":SEQREGISTRO").add(":MODIFIED")
                    .add(":CODPARC").add(":CODEMP").add(":CODNAT")
                    .add(":CODCENCUS").add(":CODVEICULO").add(":AD_CODOAT")
                    .add(":NUMNOTA").add(":AD_HORIMETRO").add(":CODTIPOPER")
                    .add(":CODTIPVENDA").add(":AD_FLAGVEIDIFPARC")
                    .add(":AD_CODTIPODEOS");

            insert.appendSql(String.format("INSERT INTO TGFCAB_WF (%s, %s) ", fields, modified));
            insert.appendSql(String.format("VALUES (%s)", params));

            // Dados da Pré OS
            QueryExecutor qe = contexto.getQuery();
            String aberturaOS = "SELECT CODPARC, CODVEICULO, HORIMETRO FROM AD_ABERTURAOS WHERE IDINSTPRN = " + idinstprn;
            qe.nativeSelect(aberturaOS);
            if(qe.next()) {
                String escopo = "P";
                BigDecimal codparc = qe.getBigDecimal("CODPARC");
                BigDecimal codemp = new BigDecimal(2);
                BigDecimal codnat = new BigDecimal("20301001");
                BigDecimal codcencus = new BigDecimal("010401005");
                BigDecimal codveiculo = qe.getBigDecimal("CODVEICULO");
                // Oficina
                BigDecimal adcodoat = new BigDecimal(6);
                // 1	GARANTIA
                // 2	ATENDIMENTO CLIENTE
                BigDecimal adtipodeos = new BigDecimal(2);
                BigDecimal numnota = BigDecimal.ZERO;
                BigDecimal adhorimetro = qe.getBigDecimal("HORIMETRO");
                String adflagveidifparc = "S";

                // Add parameters and save instance of task
                insert.setNamedParameter("IDINSTPRN", idinstprn);
                insert.setNamedParameter("IDINSTTAR", BigDecimal.ZERO);
                insert.setNamedParameter("IDTAREFA", "UserTask_1vyf2vt");
                insert.setNamedParameter("DBSTATE", "I");
                insert.setNamedParameter("SEQREGISTRO", 0);
                insert.setNamedParameter("MODIFIED", modified.toString().replace(" ", ""));

                insert.setNamedParameter("CODPARC", codparc);
                insert.setNamedParameter("CODEMP", codemp);
                insert.setNamedParameter("CODNAT", codnat);
                insert.setNamedParameter("CODCENCUS", codcencus);
                insert.setNamedParameter("CODVEICULO", codveiculo);
                insert.setNamedParameter("AD_CODOAT", adcodoat);
                insert.setNamedParameter("NUMNOTA", numnota);
                insert.setNamedParameter("AD_HORIMETRO", adhorimetro);
                insert.setNamedParameter("CODTIPOPER", 1004);
                insert.setNamedParameter("CODTIPVENDA", 25);
                insert.setNamedParameter("AD_FLAGVEIDIFPARC", adflagveidifparc);
                insert.setNamedParameter("AD_CODTIPODEOS", adtipodeos);
                insert.executeUpdate();

                qe.reset();
                qe.nativeSelect(
                    String.format(
                        "SELECT NUNOTA FROM TGFCAB_WF " +
                        "WHERE SW_IDINSTPRN = %s AND SW_IDINSTTAR = %s AND SW_IDTAREFA = %s",
                        idinstprn.toString(),
                        BigDecimal.ZERO,
                        Flow.ORCAMENTO.getIdtarefa()
                    )
                );
                if(qe.next()) {
                    BigDecimal nunota = qe.getBigDecimal("NUNOTA");
                    twfivarUpd = new NativeSql(jdbc);
                    twfivarUpd.cleanParameters();
                    twfivarUpd.resetSqlBuf();
                    twfivarUpd.appendSql("INSERT INTO TWFIVAR (IDINSTPRN, IDINSTTAR, NOME, TIPO, VALORDEC) ");
                    twfivarUpd.appendSql("VALUES (:PRN, 0, 'NUNOTA', 'I', :VALOR)");

                    twfivarUpd.setNamedParameter("PRN", idinstprn);
                    twfivarUpd.setNamedParameter("VALOR", nunota);
                    twfivarUpd.executeUpdate();

                    DefaultProcessEngineService.getInstance().setVariable(idinstprn.toString(), "NUNOTA", nunota);
                }

                String str = insert.getSqlBuf().toString();
                log.info(str);

            } else {
                throw new Exception("Não foi possível gerar o Orçamento.");
            }
        } catch (Exception e) {
            e.printStackTrace();
                throw new Exception("Error inserting into WF table: " + e.getMessage());
        } finally {
            NativeSql.releaseResources(insert);
            JdbcWrapper.closeSession(jdbc);
            JapeSession.close(hnd);
            log.info("%%%><END> INICIALIZANDO TGFCAB_WF </END><%%%");
        }
    }
}
