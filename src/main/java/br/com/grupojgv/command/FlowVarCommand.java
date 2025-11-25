package br.com.grupojgv.command;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseCommand;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.util.StringJoiner;

@JBossLog
public class FlowVarCommand extends BaseCommand {
    public FlowVarCommand() {
        super("TWFIVAR", DynamicEntityNames.INSTANCIA_VARIAVEL);
    }

    public void saveNumint(BigDecimal idinstprn, BigDecimal idinsttar, String nomeVar, Integer numint) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            log.info(
                (new StringJoiner(System.lineSeparator()))
                    .add("IDINSTPRN " + idinstprn)
                    .add("IDINSTTAR " + idinsttar)
                    .add("NOMEVAR " + nomeVar)
                    .add("NUMINT " + numint)
            );
            JapeFactory.dao(DynamicEntityNames.INSTANCIA_VARIAVEL)
                .prepareToUpdateByPK(idinstprn, idinsttar, nomeVar)
                .set("NUMINT", numint)
                .update();
            log.info("ValueObject saved for " + DynamicEntityNames.INSTANCIA_VARIAVEL);
        } finally {
            JapeSession.close(hnd);
        }
    }
}
