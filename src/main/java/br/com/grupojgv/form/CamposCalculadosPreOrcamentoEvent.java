package br.com.grupojgv.form;

import br.com.grupojgv.utils.StringUtils;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.flow.ContextoEvento;
import br.com.sankhya.extensions.flow.EventoProcessoJava;
import br.com.sankhya.jape.bmp.AbstractBMPEntity;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import com.sankhya.util.BigDecimalUtil;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@JBossLog
public class CamposCalculadosPreOrcamentoEvent implements EventoProcessoJava {

    private static final String FORM_NAME = "AD_ORCAMENTOOS";

//    @Override
//    public void executar(ContextoEvento contexto) throws Exception {
//        DynamicVO vo = (DynamicVO) event.getVo();
//
//        BigDecimal idinstprn = vo.asBigDecimal("IDINSTPRN");
//        BigDecimal idinsttar = vo.asBigDecimal("IDINSTTAR");
//        BigDecimal codregistro = vo.asBigDecimal("CODREGISTRO");
//
//        log.info("IDINSTPRN = " + idinstprn);
//        log.info("IDINSTTAR = " + idinsttar);
//        log.info("CODREGISTRO = " + codregistro);
//
//        if(idinstprn == null || idinsttar == null || idinstprn.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new Exception("Não foi possível obter identificador de processo.");
//        }
//
//        AbstractBMPEntity bmp = event.getTargetDAO().getBMPFromCache(
//            EntityPrimaryKey.getInstance(new Object[]{ vo.getPrimaryKey() })
//        );
//
//        vo.setProperty("CODPARC", new BigDecimal(5));
//        event.getTargetDAO().setValueObject((EntityVO) vo, bmp);
//    }

    @Override
    public void executar(ContextoEvento contexto) throws Exception {
        BigDecimal idinstprn = BigDecimalUtil.strToBigDecimalDef(
            StringUtils.asNullOrString(contexto.getIdInstanceProcesso()), BigDecimal.ZERO
        );
        BigDecimal idinsttar =  BigDecimalUtil.strToBigDecimalDef(
            StringUtils.asNullOrString(contexto.getIdInstanceTarefa()), BigDecimal.ZERO
        );
        Registro registro = Arrays.stream(contexto.getLinhasFormulario("AD_ORCAMENTOOS"))
            .findFirst()
            .orElse(null);
        if(registro == null) {
            throw new Exception("Registro não encontrado");
        }
        log.info("IDINSTPRN = " + idinstprn);
        log.info("IDINSTTAR = " + idinsttar);
        if(idinstprn.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Não foi possível obter identificador de processo.");
        }
        contexto.setCampo("CODPARC", new BigDecimal(5));
        contexto.salvarCamposAlterados();
        contexto.saveAll();
    }
}