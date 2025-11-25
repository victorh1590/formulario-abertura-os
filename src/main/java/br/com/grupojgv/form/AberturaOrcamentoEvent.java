package br.com.grupojgv.form;

//import br.com.grupojgv.command.FlowVarCommand;
import br.com.grupojgv.query.CabecalhoNotaFlowQuery;
import br.com.sankhya.extensions.flow.ContextoEvento;
import br.com.sankhya.extensions.flow.EventoProcessoJava;
import lombok.extern.jbosslog.JBossLog;

import java.math.BigDecimal;

@JBossLog
public class AberturaOrcamentoEvent implements EventoProcessoJava {
    @Override
    public void executar(ContextoEvento contexto) throws Exception {
        CabecalhoNotaFlowQuery cnfQuery = new CabecalhoNotaFlowQuery();
//        FlowVarCommand flowVarCommand = new FlowVarCommand();

        Object idinstprn = contexto.getIdInstanceProcesso();
        Object idinsttar = contexto.getIdInstanceTarefa();

        log.info("NUNOTA = " + contexto.getCampo("NUNOTA"));

        try {
            BigDecimal nunota = cnfQuery.findPk(idinstprn, idinsttar);
            if(nunota == null) {
                throw new Exception("Não foi possível obter NUNOTA gerado.");
            }
            contexto.setCampo("NUNOTA", nunota);
            contexto.salvarCamposAlterados();
//            flowVarCommand.saveNumint(idinstprn, idinsttar, "NUNOTA", nunota.intValue());
        } catch (Exception e) {
            throw new Exception("Erro ao tentar buscar orçamento gerado: " + e.getMessage());
        }
    }
}
