package br.com.grupojgv.routine.task;

import br.com.grupojgv.helpers.GestorPedidoVenda;
import br.com.sankhya.extensions.flow.ContextoEvento;
import br.com.sankhya.extensions.flow.EventoProcessoJava;
import com.sankhya.util.TimeUtils;
import com.sankhya.util.XMLUtils;
import lombok.extern.jbosslog.JBossLog;
import org.jdom.Element;

import java.math.BigDecimal;

@JBossLog
public class InsertOrcamentoOSFormatado implements EventoProcessoJava {
    @Override
    public void executar(ContextoEvento contexto) throws Exception {
        log.info("%%%><START> INICIALIZANDO AD_ORCAMENTOOS </START><%%%");
        Object codparc = contexto.getCampo("CODPARC");
        Object codemp = contexto.getCampo("CODEMP");
        Object codtipoper = contexto.getCampo("CODTIPOPER");

        Object adcodoat = contexto.getCampo("AD_CODOAT");
        Object adflagveidifparc = contexto.getCampo("AD_FLAGVEIDIFPARC");
        Object adhorimetro = contexto.getCampo("AD_HORIMETRO");
        Object adtipodeos= contexto.getCampo("AD_TIPODEOS");
        Object codcencus = contexto.getCampo("CODCENCUS");
        Object codnat = contexto.getCampo("CODNAT");
        Object codregistro = contexto.getCampo("CODREGISTRO");
        Object codtipvenda = contexto.getCampo("CODTIPVENDA");
        Object codveiculo = contexto.getCampo("CODVEICULO");
        Object codvend = contexto.getCampo("CODVEND");

        // -----------------------------------------------------------
        // Criar o Cabeçalho (TGFCAB)
        // -----------------------------------------------------------
        Element cabecalhoXml = new Element("Cabecalho");

        // Campos obrigatórios mínimos
        XMLUtils.addContentElement(cabecalhoXml, "NUNOTA", 0); // NUNOTA
        XMLUtils.addContentElement(cabecalhoXml, "CODTIPOPER", codtipoper); // TOP
        XMLUtils.addContentElement(cabecalhoXml, "CODPARC", codparc); // Parceiro
        XMLUtils.addContentElement(cabecalhoXml, "CODEMP", codemp); // Empresa
        XMLUtils.addContentElement(cabecalhoXml, "DTNEG", TimeUtils.formataDDMMYYYY(TimeUtils.getNow())); // Data
        XMLUtils.addContentElement(cabecalhoXml, "TIPMOV", "P"); // P = Pedido de Venda

        // Campos opcionais
        XMLUtils.addContentElement(cabecalhoXml, "AD_CODOAT", adcodoat); // Origem do Atendimento
        XMLUtils.addContentElement(cabecalhoXml, "AD_FLAGVEIDIFPARC", adflagveidifparc); // Flag
        XMLUtils.addContentElement(cabecalhoXml, "AD_HORIMETRO", adhorimetro); // Horímetro
        XMLUtils.addContentElement(cabecalhoXml, "AD_CODTIPODEOS", adtipodeos); // Tipo de OS
        XMLUtils.addContentElement(cabecalhoXml, "CODCENCUS", codcencus); // Centro de Custo
        XMLUtils.addContentElement(cabecalhoXml, "CODNAT", codnat); // Natureza
        XMLUtils.addContentElement(cabecalhoXml, "CODREGISTRO", codregistro); // Cód. Registro
        XMLUtils.addContentElement(cabecalhoXml, "CODTIPVENDA", codtipvenda); // Cód. Tipo de Negociação
        XMLUtils.addContentElement(cabecalhoXml, "CODVEICULO", codveiculo); // Cód. Veículo
        XMLUtils.addContentElement(cabecalhoXml, "CODVEND", codvend); // Cód. Vendedor

        GestorPedidoVenda gestorPedidoVenda = new GestorPedidoVenda();
        BigDecimal nunota = gestorPedidoVenda.gerarPedido(cabecalhoXml);

        log.info("%%%><END> ENCERRANDO AD_ORCAMENTOOS </END><%%%");
    }
}
