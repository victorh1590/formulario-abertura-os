package br.com.grupojgv.routine.task;

import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import lombok.Builder;

@Builder
public class CabecalhoNota implements TypedVO {

//    /// Obrigatórios
//    private BigDecimal NUNOTA;
//    private BigDecimal CODTIPOPER;
//    private BigDecimal CODPARC;
//    private BigDecimal CODEMP;
//    private Timestamp DTNEG;
//    private String TIPMOV;
//
//    // Regras JGV
//    private BigDecimal NUMNOTA;
//    private BigDecimal CODCENCUS;
//    private BigDecimal CODNAT;
//    private BigDecimal CODTIPVENDA;
//    private BigDecimal CODVEICULO;
//    private BigDecimal CODVEND;
//
//    // Adicionais
//    private BigDecimal AD_CODOAT;
//    private BigDecimal AD_FLAGVEIDIFPARC;
//    private BigDecimal AD_HORIMETRO;
//    private BigDecimal AD_CODTIPODEOS;
//

    private Object NUNOTA;
    private Object CODTIPOPER;
    private Object CODPARC;
    private Object CODEMP;
    private Object DTNEG;
    private Object TIPMOV;
    private Object NUMNOTA;
    private Object CODCENCUS;
    private Object CODNAT;
    private Object CODTIPVENDA;
    private Object CODVEICULO;
    private Object CODVEND;
    private Object AD_CODOAT;
    private Object AD_FLAGVEIDIFPARC;
    private Object AD_HORIMETRO;
    private Object AD_CODTIPODEOS;
    private Object AD_ACAOGERADORA;
    private Object OBSERVACAO;
    private Object OBSERVACAOAC;
    private Object DHTIPOPER;
    private Object DHTIPVENDA;

    public static void loadTop(QueryExecutor qe, CabecalhoNotaBuilder builder, Object codtipoper) throws Exception {
        qe.reset();
        qe.setParam("CODTIPOPER", codtipoper);
        qe.nativeSelect("SELECT MAX(DHALTER) FROM TGFTOP WHERE CODTIPOPER = {CODTIPOPER}");
        if(!qe.next()) {
            throw new Exception("Não foi encontrada versão do Tipo de Operação (DHALTER) para utilização.");
        }
        builder.DHTIPOPER(qe.getTimestamp(1));
    }

    public static void loadTpv(QueryExecutor qe, CabecalhoNotaBuilder builder, Object codtipvenda) throws Exception {
        qe.reset();
        qe.setParam("CODTIPVENDA", codtipvenda);
        qe.nativeSelect("SELECT MAX(DHALTER) FROM TGFTPV WHERE CODTIPVENDA = {CODTIPVENDA}");
        if(!qe.next()) {
            throw new Exception("Não foi encontrada versão do Tipo de Venda/Negociação (DHALTER) para utilização.");
        }
        builder.DHTIPVENDA(qe.getTimestamp(1));
    }
}
