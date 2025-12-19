package br.com.grupojgv.routine.task;

import br.com.grupojgv.helpers.GestorPedidoVenda;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.flow.ContextoEvento;
import br.com.sankhya.extensions.flow.EventoProcessoJava;
import com.google.gson.JsonObject;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.JsonUtils;
import com.sankhya.util.TimeUtils;
import lombok.extern.jbosslog.JBossLog;
import org.aeonbits.owner.ConfigFactory;

import java.math.BigDecimal;
import java.util.Base64;

@JBossLog
public class InsertOrcamentoOSFormatado implements EventoProcessoJava {
    @Override
    public void executar(ContextoEvento contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO AD_ORCAMENTOOS </START><%%%");
        QueryExecutor qe = null;
        try {
            qe = contexto.getQuery();
            Registro[] registros = contexto.getLinhasFormulario("AD_ORCAMENTOOS");
            if(registros.length < 1) {
                throw new IllegalArgumentException("Não foi possível obter dados do formulário AD_ORCAMENTOOS.");
            }
            Registro r = registros[0];

            //
            BigDecimal nunotaExistente = BigDecimalUtil.getBigDecimal(r.getCampo("NUNOTA"));

            if (nunotaExistente != null && nunotaExistente.compareTo(BigDecimal.ZERO) > 0) {
                log.warn("ATENCAO: Tentativa de re-execucao detectada. Pedido ja criado: " + nunotaExistente);
                return;
            }
            //

            Object idinstprn = contexto.getIdInstanceProcesso();
            Object nunota = null;
            Object dtneg = TimeUtils.getNow();
            Object tipmov = "P";
            Object numnota = BigDecimal.ZERO;
            Object adacaogeradora = "SANKHYA FLOW";
            Object obs = String.format(
                "GERADO AUTOMATICAMENTE PELA SOLICITACAO NRO. %s DO FLUXO \"ABERTURA DE OS\" DO SANKHYA FLOW",
                idinstprn
            );

            Object codparc = r.getCampo("CODPARC");
            Object codemp = r.getCampo("CODEMP");
            Object codtipoper = r.getCampo("CODTIPOPER");
            Object adcodoat = r.getCampo("AD_CODOAT");
            Object adflagveidifparc = r.getCampo("AD_FLAGVEIDIFPARC");
            Object adhorimetro = r.getCampo("AD_HORIMETRO");
            Object adtipodeos= r.getCampo("AD_TIPODEOS");
            Object codcencus = r.getCampo("CODCENCUS");
            Object codnat = r.getCampo("CODNAT");
            Object codtipvenda = r.getCampo("CODTIPVENDA");
            Object codveiculo = r.getCampo("CODVEICULO");
            Object codvend = r.getCampo("CODVEND");

            CabecalhoNota.CabecalhoNotaBuilder builder = CabecalhoNota.builder()
                .NUNOTA(nunota)
                .CODTIPOPER(codtipoper)
                .CODPARC(codparc)
                .CODEMP(codemp)
                .DTNEG(dtneg)
                .TIPMOV(tipmov)
                .NUMNOTA(numnota)
                .CODCENCUS(codcencus)
                .CODNAT(codnat)
                .CODTIPVENDA(codtipvenda)
                .CODVEICULO(codveiculo)
                .CODVEND(codvend)
                .AD_CODOAT(adcodoat)
                .AD_FLAGVEIDIFPARC(adflagveidifparc)
                .AD_HORIMETRO(adhorimetro)
                .AD_CODTIPODEOS(adtipodeos)
                .AD_ACAOGERADORA(adacaogeradora)
                .OBSERVACAO(obs)
                .OBSERVACAOAC(obs);
            CabecalhoNota.loadTop(qe, builder, codtipoper);
            CabecalhoNota.loadTpv(qe, builder, codtipvenda);
            CabecalhoNota cab = builder.build();
            String json = JsonUtils.converteObjectToString(cab);
            log.info("CAB: " + json);

            GestorPedidoVenda gestorPedidoVenda = new GestorPedidoVenda();
            nunota = gestorPedidoVenda.criarCabecalho(cab);
            if((BigDecimalUtil.getBigDecimal(nunota)).compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Não foi retornado valor válido para a chave do orçamento criado.");
            }
            r.setCampo("NUNOTA", nunota);
            r.save();

            enviarAviso(contexto, (BigDecimal) idinstprn, (BigDecimal) nunota, (String) tipmov, (BigDecimal) codtipoper);

            // Colocar campo calculado HTML linkando para OS.
        } finally {
            if(qe != null) {
                qe.close();
            }
            log.info("%%%><END> ENCERRANDO AD_ORCAMENTOOS </END><%%%");
        }
    }

    private void enviarAviso(ContextoEvento contexto, BigDecimal idinstprn, BigDecimal nunota, String tipmov, BigDecimal codtipoper) throws Exception {
        // Fazer envio de Aviso.
        BigDecimal codusu = contexto.getUsuarioLogado();
        NotificacaoFacade notificacaoFacade = new NotificacaoFacade();

        SankhyaProperties cfg = ConfigFactory.create(SankhyaProperties.class);

        String forceNewHash = String.valueOf(System.currentTimeMillis());
        JsonObject json = new JsonObject();
        json.addProperty("NUNOTA", nunota);
        json.addProperty("TIPMOV", tipmov);
        json.addProperty("ehPedidoW", false);
        json.addProperty("CODTIPOPER", codtipoper);
        json.addProperty("TIPOPORTAL", "PV");
        json.addProperty("forceNewHash", forceNewHash);
        String resource = String.format("%s?%s", cfg.sankhyaOrcamento(), json);
        String url = SankhyaUrlBuilder.builder()
            .protocol("https")
            .host(cfg.sankhyaUrl())
            .module(cfg.sankhyaModule())
            .path("system.jsp#app")
            .path(Base64.getEncoder().encodeToString(resource.getBytes()))
            .paramConnector("&")
            .param("pk-refresh", forceNewHash)
            .build()
            .url();
        String link = getButton(nunota, url);
        log.info("URL: " + url);
        log.info("LINK: " + link);

        String title = "Workflow de Abertura de OS " + idinstprn;
        notificacaoFacade.enviarAvisoUsuario(codusu, title, link, 3);
    }

    private String getButton(Object nunota, String url) {
        String htmlTemplate = "<a href=\"%s\" target=\"_top\" " +
            "class=\"btn btn-primary btn-sm\" " +
            "style=\"background-color: #007bff; border: 1px solid #007bff; " +
            "color: #ffffff; padding: 5px 10px; text-align: center; " +
            "text-decoration: none; display: inline-block; font-size: 12px; " +
            "border-radius: 4px; font-family: sans-serif; cursor: pointer;\">" +
            "Abrir Orçamento %s" +
            "</a>";
        return String.format(htmlTemplate, url, nunota);
    }
}
