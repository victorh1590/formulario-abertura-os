package br.com.grupojgv.form;

import br.com.grupojgv.helpers.GestorPedidoVenda;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.flow.ContextoTarefa;
import br.com.sankhya.extensions.flow.TarefaJava;
import com.sankhya.util.BigDecimalUtil;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.util.Arrays;

@JBossLog
public class AberturaOrcamentoTask implements TarefaJava {
    @Override
    public void executar(ContextoTarefa contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO TGFCAB_TNF </START><%%%");

        Registro aberturaOS = Arrays.stream(contexto.getLinhasFormulario("AD_ABERTURAOS"))
            .findFirst()
            .orElse(null);
        if(aberturaOS == null) {
            throw new NullPointerException("Formulário Pré-OS não foi encontrado.");
        }
        try {
//            BigDecimal codveiculo = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODVEICULO"));
//            BigDecimal codmarca = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODMARCA"));
//            BigDecimal codmodelo = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODMODELO"));
//            String solicitacao = StringUtils.asNullOrString(aberturaOS.getCampo("SOLICITACAO"));

            BigDecimal codtipoper = new BigDecimal(1004);
            BigDecimal codparc = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODPARC"));
            BigDecimal codemp = new BigDecimal(4);
            BigDecimal codnat = BigDecimalUtil.strToBigDecimalDef("20301001", BigDecimal.ZERO);
            BigDecimal codcencus = BigDecimalUtil.strToBigDecimalDef("010401005", BigDecimal.ZERO);
            BigDecimal codtipvenda = BigDecimalUtil.strToBigDecimalDef("25", BigDecimal.ZERO);
            BigDecimal adcodtipodeos = BigDecimalUtil.strToBigDecimalDef("2", BigDecimal.ZERO);

//            BigDecimal adcodoat = new BigDecimal(6);
//            String tipMov = "P";
//            Timestamp dhAgora = new Timestamp(System.currentTimeMillis());
//            String hashcode = String.valueOf(System.currentTimeMillis());
//            BigDecimal codtipvenda = new BigDecimal(25);
//            BigDecimal adcodtipodeos = new BigDecimal(2);
//            BigDecimal numnota = new BigDecimal(0);


            GestorPedidoVenda gestorPedidoVenda = new GestorPedidoVenda();
            BigDecimal nunota = gestorPedidoVenda.criarCabecalho(
                codtipoper,
                codparc,
                codemp,
                codnat,
                codcencus,
                codtipvenda,
                adcodtipodeos
            );
            log.infov("NUNOTA criado = {0}", nunota);

            QueryExecutor query = contexto.getQuery();
            String sqlInsert = "INSERT INTO TGFCAB_TNF (" +
                "TNF_IDINSTPRN, " +
                "TNF_IDINSTTAR, " +
                "TNF_DHCRIACAO, " +
                "NUNOTA" +
                ") VALUES (" +
                contexto.getIdInstanceProcesso() + ", " +
                "0, " +
                "SYSDATE, " +
                nunota +
                ")";

            query.update(sqlInsert);

            log.info("Vínculo criado na TGFCAB_TNF com IDINSTTAR=0 e NUNOTA=" + nunota);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            log.info("%%%><END> INICIALIZANDO TGFCAB_TNF </END><%%%");
        }
    }
}
