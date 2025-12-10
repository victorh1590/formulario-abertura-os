package br.com.grupojgv.logic.task;

import br.com.grupojgv.model.OrcamentoOS;
import br.com.grupojgv.utils.CommandUtils;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.flow.ContextoTarefa;
import br.com.sankhya.extensions.flow.TarefaJava;
import com.sankhya.util.BigDecimalUtil;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.util.Arrays;

@JBossLog
public class PrefillOrcamentoOS implements TarefaJava {
    @Override
    public void executar(ContextoTarefa contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO AD_ORCAMENTOOS </START><%%%");
        try {
            Registro registroAberturaOS = Arrays.stream(contexto.getLinhasFormulario("AD_ABERTURAOS"))
                .findFirst()
                .orElse(null);
            if(registroAberturaOS == null) {
                return;
            }

            BigDecimal codparc = (BigDecimal) registroAberturaOS.getCampo("CODPARC");
            BigDecimal codveiculo = (BigDecimal) registroAberturaOS.getCampo("CODVEICULO");
//            registroAberturaOS.getCampo("CODEMP");
            BigDecimal adhorimetro = (BigDecimal) registroAberturaOS.getCampo("HORIMETRO");

//            BigDecimal codparc = new BigDecimal(5);
//            BigDecimal codveiculo = new BigDecimal(35505);
            BigDecimal codemp = new BigDecimal(4);
//            BigDecimal adhorimetro = new BigDecimal(0);
            BigDecimal codnat = BigDecimalUtil.strToBigDecimalDef("20301001", BigDecimal.ZERO);
            BigDecimal codcencus = BigDecimalUtil.strToBigDecimalDef("010401005", BigDecimal.ZERO);
            BigDecimal adcodoat = new BigDecimal(6);
            BigDecimal adtipodeos = BigDecimalUtil.strToBigDecimalDef("2", BigDecimal.ZERO);
            BigDecimal numnota = new BigDecimal(0);
            String adflagveidfparc = "S";

            Registro registroOrcamentoOS = contexto.novaLinhaFormulario("AD_ORCAMENTOOS");
            OrcamentoOS orcamentoOS = OrcamentoOS.builder()
                .IDINSTPRN((BigDecimal) contexto.getIdInstanceProcesso())
                .IDINSTTAR(null)
                .IDTAREFA(null)
                .CODREGISTRO(null)
                .CODPARC(codparc)
                .CODEMP(codemp)
                .CODNAT(codnat)
                .CODCENCUS(codcencus)
                .CODVEICULO(codveiculo)
                .AD_CODOAT(adcodoat)
                .AD_TIPODEOS(adtipodeos)
                .NUMNOTA(numnota)
                .AD_HORIMETRO(adhorimetro)
                .AD_FLAGVEIDIFPARC(adflagveidfparc)
                .build();

            CommandUtils.setFieldsFromKvps(registroOrcamentoOS, orcamentoOS);
            registroOrcamentoOS.save();
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            log.info("%%%><END> INICIALIZANDO AD_ORCAMENTOOS </END><%%%");
        }
    }

}
