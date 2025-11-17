package br.com.grupojgv.command;

import br.com.grupojgv.kt.utilitariosJGV.shared.Infrastructure.BaseCommand;
import br.com.grupojgv.model.FormAberturaOS;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import lombok.extern.jbosslog.JBossLog;

import java.lang.reflect.Field;
import java.util.StringJoiner;

@JBossLog
public class FormAberturaOSCommand extends BaseCommand {
    public FormAberturaOSCommand() {
        super("AD_ABERTURAOS", "AD_ABERTURAOS");
    }

    public void save(FormAberturaOS faos) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            hnd.setCanTimeout(false);
            hnd.setPriorityLevel(JapeSession.LOW_PRIORITY);
            log.info(
                (new StringJoiner(System.lineSeparator()))
                    .add("IDINSTPRN " + faos.getIDINSTPRN())
                    .add("IDINSTTAR " + faos.getIDINSTTAR())
                    .add("CODREGISTRO " + faos.getCODREGISTRO())
            );
            FluidUpdateVO fluidUpdateVO = JapeFactory.dao("AD_ABERTURAOS").prepareToUpdateByPK(faos.pk());
            for(Field f: FormAberturaOS.class.getDeclaredFields()) {
                f.setAccessible(true);
                String key = f.getName();
                Object value = f.get(faos);
                log.info("Updating field " + key);
                fluidUpdateVO.set(key, value);
            }
            fluidUpdateVO.update();
            log.info("ValueObject saved for AD_ABERTURAOS");
        } finally {
            JapeSession.close(hnd);
        }
    }
}
