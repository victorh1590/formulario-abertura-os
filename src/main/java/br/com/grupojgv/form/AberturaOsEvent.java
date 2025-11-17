package br.com.grupojgv.form;

import br.com.grupojgv.command.FormAberturaOSCommand;
import br.com.grupojgv.model.FormAberturaOS;
import br.com.grupojgv.model.Parceiro;
import br.com.grupojgv.model.Veiculo;
import br.com.grupojgv.query.FormAberturaOSQuery;
import br.com.grupojgv.query.ParceiroQuery;
import br.com.grupojgv.query.VeiculoQuery;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.io.IOUtils;

import java.math.BigDecimal;
import java.util.StringJoiner;

@JBossLog
public class AberturaOsEvent implements EventoProgramavelJava {

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
        FormAberturaOSQuery faosQuery = new FormAberturaOSQuery();
        FormAberturaOSCommand faosCommand = new FormAberturaOSCommand();
        ParceiroQuery parceiroQuery = new ParceiroQuery();
        VeiculoQuery veiculoQuery = new VeiculoQuery();
        DynamicVO vo = (DynamicVO) event.getVo();

        BigDecimal idinstprn = vo.asBigDecimal("IDINSTPRN");
        BigDecimal idinsttar = vo.asBigDecimal("IDINSTTAR");
        BigDecimal codregistro = vo.asBigDecimal("CODREGISTRO");

        FormAberturaOS faos = faosQuery.buscar(
            idinstprn,
            idinsttar,
            codregistro
        );

        BigDecimal codparc = vo.asBigDecimal("CODPARC");
        try {
            Parceiro p = parceiroQuery.findOne(codparc);
            faos.setNOMEPARC(p.getNOMEPARC());
            faos.setRAZAOSOCIAL(p.getRAZAOSOCIAL());
            faos.setCGC_CPF(p.getCGC_CPF());
        } catch (NullPointerException e) {
            faos.setNOMEPARC(null);
            faos.setRAZAOSOCIAL(null);
            faos.setCGC_CPF(null);
        }

        BigDecimal codveiculo = vo.asBigDecimal("CODVEICULO");
        try {
            Veiculo v = veiculoQuery.findOne(codveiculo);
            faos.setCODMARCA(v.getCODMARCA());
            faos.setCODMODELO(v.getCODMODELO());
            faos.setCODTIPO(v.getCODTIPO());
        } catch (NullPointerException e) {
            faos.setCODMARCA(null);
            faos.setCODMODELO(null);
            faos.setCODTIPO(null);
        }

        faos.setSOLICITACAO(
            new StringJoiner(System.lineSeparator())
                .add("Solicitação de Abertura de OS")
                .add("---")
                .add("CPF/CNPJ: " + fmtReqField(faos.getCGC_CPF()))
                .add("Cód. Parceiro: " + fmtReqField(faos.getCODPARC()))
                .add("---")
                .add("Cód. Veículo: " + fmtReqField(faos.getCODVEICULO()))
                .add("Cód. Tipo: " + fmtReqField(faos.getCODTIPO()))
                .add("Cód. Marca: " + fmtReqField(faos.getCODMARCA()))
                .add("Cód. Modelo: " + fmtReqField(faos.getCODMODELO()))
                .add("---")
                .add("Horímetro: " + fmtReqField(faos.getHORIMETRO()))
                .add("Descrição do problema: " + fmtReqField(faos.getSOLICITACAO()))
                .toString()
                .toCharArray()
        );
        faosCommand.save(faos);
    }

    public String fmtReqField(Object field) {
        try {
            if(field instanceof char[]) {
                return String.valueOf(field);
            }
            return field.toString();
        } catch (Exception e) {
            return "<não preenchido>";
        }
    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        FormAberturaOSQuery faosQuery = new FormAberturaOSQuery();
        FormAberturaOSCommand faosCommand = new FormAberturaOSCommand();
        ParceiroQuery parceiroQuery = new ParceiroQuery();
        VeiculoQuery veiculoQuery = new VeiculoQuery();
        DynamicVO vo = (DynamicVO) event.getVo();

        BigDecimal idinstprn = vo.asBigDecimal("IDINSTPRN");
        BigDecimal idinsttar = vo.asBigDecimal("IDINSTTAR");
        BigDecimal codregistro = vo.asBigDecimal("CODREGISTRO");

        ModifingFields modifingFields = event.getModifingFields();
        if(modifingFields.isModifingAny("CODVEICULO,CODPARC")) {
            FormAberturaOS faos = faosQuery.buscar(
                idinstprn,
                idinsttar,
                codregistro
            );

            if(modifingFields.isModifing("CODPARC")) {
                BigDecimal codparc = vo.asBigDecimal("CODPARC");
                try {
                    Parceiro p = parceiroQuery.findOne(codparc);
                    faos.setNOMEPARC(p.getNOMEPARC());
                    faos.setRAZAOSOCIAL(p.getRAZAOSOCIAL());
                    faos.setCGC_CPF(p.getCGC_CPF());
                } catch (NullPointerException e) {
                    faos.setNOMEPARC(null);
                    faos.setRAZAOSOCIAL(null);
                    faos.setCGC_CPF(null);
                }
            }

            if(modifingFields.isModifing("CODVEICULO")) {
                BigDecimal codveiculo = vo.asBigDecimal("CODVEICULO");
                try {
                    Veiculo v = veiculoQuery.findOne(codveiculo);
                    faos.setCODMARCA(v.getCODMARCA());
                    faos.setCODMODELO(v.getCODMODELO());
                    faos.setCODTIPO(v.getCODTIPO());
                } catch (NullPointerException e) {
                    faos.setCODMARCA(null);
                    faos.setCODMODELO(null);
                    faos.setCODTIPO(null);
                }
            }

            faosCommand.save(faos);
        }
    }
}