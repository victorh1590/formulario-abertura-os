package br.com.grupojgv.routine.task;

import br.com.grupojgv.helpers.GestorPedidoVenda;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.flow.ContextoEvento;
import br.com.sankhya.extensions.flow.EventoProcessoJava;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.JsonUtils;
import com.sankhya.util.TimeUtils;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.File;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

            // Fazer envio de Aviso.
            BigDecimal codusu = contexto.getUsuarioLogado();
            NotificacaoFacade notificacaoFacade = new NotificacaoFacade();

            PropertiesConfiguration cfg = (new Configurations()).properties(new File("application.properties"));
            Map<String, String> resourceProperties = new HashMap<>();
            resourceProperties.put("NUNOTA", nunota.toString());
            String resource =
                String.format(
                    "%s?%s",
                    cfg.getProperty("sankhya.resource.orcamento").toString(),
                    JsonUtils.getGson().toJson(resourceProperties)
                );
            String url = SankhyaUrlBuilder.builder()
                .protocol("https")
                .host(cfg.getProperty("sankhya.url").toString())
                .module(cfg.getProperty("sankhya.module").toString())
                .path("system.jsp#app")
                .path(Base64.getEncoder().encodeToString(resource.getBytes()))
                .build()
                .url();
            String link = String.format("<a href=%s>%s</a>", url, nunota);
            log.info("URL: " + url);
            log.info("LINK: " + link);

            notificacaoFacade.enviarAvisoUsuario(
                codusu,
                "Workflow de Abertura de OS #" + idinstprn,
                "Foi gerado o orçamento #" + link,
                3
            );
            // Colocar campo calculado HTML linkando para OS.
        } finally {
            if(qe != null) {
                qe.close();
            }
            log.info("%%%><END> ENCERRANDO AD_ORCAMENTOOS </END><%%%");
        }
    }
}
