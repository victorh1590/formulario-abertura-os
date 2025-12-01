package br.com.grupojgv.form;

import br.com.grupojgv.helpers.GestorPedidoVenda;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.flow.ContextoEvento;
import br.com.sankhya.extensions.flow.EventoProcessoJava;
import com.sankhya.util.BigDecimalUtil;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;
import java.sql.Timestamp;

@JBossLog
public class GerarInstanciaCabEvent implements EventoProcessoJava {
    @Override
    public void executar(ContextoEvento contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO TGFCAB_TNF </START><%%%");

//        Registro aberturaOS = Arrays.stream(contexto.getLinhasFormulario("AD_ABERTURAOS"))
//            .findFirst()
//            .orElse(null);
//        if(aberturaOS == null) {
//            throw new NullPointerException("Formulário Pré-OS não foi encontrado.");
//        }
        try {
//            BigDecimal codparc = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODPARC"));
//            BigDecimal codveiculo = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODVEICULO"));
//            BigDecimal codmarca = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODMARCA"));
//            BigDecimal codmodelo = BigDecimalUtil.getBigDecimal(aberturaOS.getCampo("CODMODELO"));
//            String solicitacao = StringUtils.asNullOrString(aberturaOS.getCampo("SOLICITACAO"));

            BigDecimal codparc = BigDecimalUtil.getBigDecimal(2944);
            BigDecimal codveiculo = BigDecimalUtil.getBigDecimal(35505);
            BigDecimal codtipoper = new BigDecimal(1004);
            BigDecimal adcodoat = new BigDecimal(6);

            BigDecimal codEmp = new BigDecimal(4);
            String tipMov = "P";
            Timestamp dhAgora = new Timestamp(System.currentTimeMillis());
            String hashcode = String.valueOf(System.currentTimeMillis());
            BigDecimal codcencus = BigDecimalUtil.strToBigDecimalDef("010401005", BigDecimal.ZERO);
            BigDecimal codnat = BigDecimalUtil.strToBigDecimalDef("20301001", BigDecimal.ZERO);
            BigDecimal codtipvenda = new BigDecimal(25);
            BigDecimal adcodtipodeos = new BigDecimal(2);
            BigDecimal numnota = new BigDecimal(0);
            BigDecimal codemp = new BigDecimal(4);

            QueryExecutor query = contexto.getQuery();

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

//            JapeWrapper cabDAO = JapeFactory.dao("CabecalhoNota");
//            DynamicVO noteVO = cabDAO.create()
//                .set("CODEMP", codEmp)
//                .set("CODPARC", codparc)
//                .set("CODTIPOPER", codtipoper)
//                .set("TIPMOV", tipMov)
//                .set("DTNEG", dhAgora)
//                .set("DTENTSAI", dhAgora)
//                .set("CODVEICULO", codveiculo)
//                .set("AD_CODOAT", adcodoat)
//                .set("CODCENCUS", codcencus)
//                .set("CODNAT", codnat)
//                .set("CODTIPVENDA", codtipvenda)
//                .set("AD_CODTIPODEOS", adcodtipodeos)
//                .set("NUMNOTA", numnota)
//                .save();
//
//            BigDecimal realNuNota = noteVO.asBigDecimal("NUNOTA");

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
                "INSERT INTO TGFCAB_TNF (" +
                    "TNF_IDINSTPRN, " +
                    "TNF_IDINSTTAR, " +
                    "CODPARC, " +
                    "CODVEICULO, " +
                    "CODTIPOPER, " +
                    "AD_CODOAT, " +
                    "TNF_HASHCODE, " +
                    "TIPMOV, " +
                    "CODEMP, " +
                    "TNF_DHCRIACAO, " +
                    "DTNEG, " +
                    "NUNOTA, " +
                    "CODCENCUS, " +
                    "CODNAT, " +
                    "CODTIPVENDA, " +
                    "AD_CODTIPODEOS, " +
                    "NUMNOTA" +
                    ") " +
                    "VALUES (" +
                    contexto.getIdInstanceProcesso() + ", " +
//                    contexto.getIdInstanceTarefa() + ", " +
                    contexto.getIdInstanceTarefa() + ", " +
                    codparc + ", " +
                    codveiculo  + ", " +
                    codtipoper + ", " +
                    adcodoat + ", " +
                    hashcode + ", " +
                    "'" + tipMov + "', " +
                    codEmp + ", " +
                    "SYSDATE" + ", " +
                    "SYSDATE" + ", " +
                    nunota + ", " +
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
