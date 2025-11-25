package br.com.grupojgv.form;

import br.com.grupojgv.utils.StringUtils;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.flow.ContextoTarefa;
import br.com.sankhya.extensions.flow.TarefaJava;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import com.sankhya.util.BigDecimalUtil;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
            BigDecimal codparc = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODPARC"));
            BigDecimal codveiculo = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODVEICULO"));
//            BigDecimal codmarca = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODMARCA"));
//            BigDecimal codmodelo = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODMODELO"));
//            String solicitacao = StringUtils.asNullOrString(aberturaOS.getCampo("SOLICITACAO"));

            BigDecimal codtipoper = new BigDecimal(1004);
            BigDecimal adcodoat = new BigDecimal(6);

            BigDecimal codEmp = new BigDecimal(4);
            String tipMov = "P";
            Timestamp dhAgora = new Timestamp(System.currentTimeMillis());
//            String hashcode = String.valueOf(System.currentTimeMillis());
            BigDecimal codcencus = BigDecimalUtil.strToBigDecimalDef("010401005", BigDecimal.ZERO);
            BigDecimal codnat = BigDecimalUtil.strToBigDecimalDef("20301001", BigDecimal.ZERO);
            BigDecimal codtipvenda = new BigDecimal(25);
            BigDecimal adcodtipodeos = new BigDecimal(2);
            BigDecimal numnota = new BigDecimal(0);

            QueryExecutor query = contexto.getQuery();

            JapeWrapper cabDAO = JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA);
            DynamicVO noteVO = cabDAO.create()
                .set("CODEMP", codEmp)
                .set("CODPARC", codparc)
                .set("CODTIPOPER", codtipoper)
                .set("TIPMOV", tipMov)
                .set("DTNEG", dhAgora)
                .set("DTENTSAI", dhAgora)
                .set("CODVEICULO", codveiculo)
                .set("AD_CODOAT", adcodoat)
                .set("CODCENCUS", codcencus)
                .set("CODNAT", codnat)
                .set("CODTIPVENDA", codtipvenda)
                .set("AD_CODTIPODEOS", adcodtipodeos)
                .set("NUMNOTA", numnota)
                .save();

            BigDecimal realNuNota = noteVO.asBigDecimal("NUNOTA");

//            Registro cab = contexto.novaLinha("TGFCAB_TNF");
//            cab.setCampo("TNF_IDINSTTAR", contexto.getIdInstanceTarefa());
//            cab.setCampo("TNF_IDINSTPRN", contexto.getIdInstanceProcesso());
//            cab.setCampo("CODPARC", codparc);
//            cab.setCampo("CODVEICULO", codveiculo);
//            cab.save();

//            Object[] insert = new Object[] {
//                contexto.getIdInstanceProcesso(),
//                contexto.getIdInstanceTarefa(),
//                codparc,
//                codveiculo,
//                codtipoper,
//                adcodoat,
//                hashcode,
//                tipMov,
//                codEmp,
//                dhAgora,
//                dhAgora
//            };
            query.update(
            "INSERT INTO TGFCAB (" +
//            "INSERT INTO TGFCAB_TNF (" +
//                "TNF_IDINSTPRN, " +
//                "TNF_IDINSTTAR, " +
//                "TNF_HASHCODE, " +
//                "TNF_DHCRIACAO, " +
                "CODPARC, " +
                "CODVEICULO, " +
                "CODTIPOPER, " +
                "AD_CODOAT, " +
                "TIPMOV, " +
                "CODEMP, " +
                "DTNEG, " +
                "NUNOTA, " +
                "CODCENCUS, " +
                "CODNAT, " +
                "CODTIPVENDA, " +
                "AD_CODTIPODEOS, " +
                "NUMNOTA" +
                ") " +
                "VALUES (" +
//                contexto.getIdInstanceProcesso() + ", " +
//                contexto.getIdInstanceTarefa() + ", " +
//                hashcode + ", " +
//                "SYSDATE" + ", " +
                codparc + ", " +
                codveiculo  + ", " +
                codtipoper + ", " +
                adcodoat + ", " +
                "'" + tipMov + "', " +
                codEmp + ", " +
                "SYSDATE" + ", " +
                realNuNota + ", " +
                codcencus + ", " +
                codnat + ", " +
                codtipvenda + ", " +
                adcodtipodeos + ", " +
                numnota +
                ")"
            );

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        } finally {
            log.info("%%%><END> INICIALIZANDO TGFCAB_TNF </END><%%%");
        }
    }
}
