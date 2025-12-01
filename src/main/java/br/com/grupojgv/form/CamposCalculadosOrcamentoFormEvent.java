package br.com.grupojgv.form;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;

@JBossLog
public class CamposCalculadosOrcamentoFormEvent implements EventoProgramavelJava {

    private static final String FORM_NAME = "AD_ABERTURAOS";

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {}

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {}

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {}

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {}

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {}

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {
        DynamicVO vo = (DynamicVO) event.getVo();

        BigDecimal idinstprn = vo.asBigDecimal("IDINSTPRN");
        BigDecimal idinsttar = vo.asBigDecimal("IDINSTTAR");
        BigDecimal codregistro = vo.asBigDecimal("CODREGISTRO");

        log.info("IDINSTPRN = " + idinstprn);
        log.info("IDINSTTAR = " + idinsttar);
        log.info("CODREGISTRO = " + codregistro);

        if(idinstprn == null || idinsttar == null || idinstprn.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Não foi possível obter identificador de processo.");
        }

        vo.setProperty("CODPARC", new BigDecimal(5));
        EntityFacade facade = EntityFacadeFactory.getDWFFacade();
        facade.saveEntity("AD_ORCAMENTOOS", (EntityVO) vo);
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        DynamicVO vo = (DynamicVO) event.getVo();

        BigDecimal idinstprn = vo.asBigDecimal("IDINSTPRN");
        BigDecimal idinsttar = vo.asBigDecimal("IDINSTTAR");
        BigDecimal codregistro = vo.asBigDecimal("CODREGISTRO");

        log.info("IDINSTPRN = " + idinstprn);
        log.info("IDINSTTAR = " + idinsttar);
        log.info("CODREGISTRO = " + codregistro);

        if(idinstprn == null || idinsttar == null || idinstprn.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Não foi possível obter identificador de processo.");
        }

        vo.setProperty("CODPARC", new BigDecimal(5));
        EntityFacade facade = EntityFacadeFactory.getDWFFacade();
        facade.saveEntity("AD_ORCAMENTOOS", (EntityVO) vo);
    }
}