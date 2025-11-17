package br.com.grupojgv.model;

import lombok.*;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.Clob;

@Builder
public class FormAberturaOS {
    @Setter @Getter @Nullable private BigDecimal IDINSTPRN;
    @Setter @Getter @Nullable private BigDecimal IDINSTTAR;
    @Setter @Getter @Nullable private BigDecimal CODREGISTRO;
    @Setter @Getter @Nullable private String IDTAREFA;
    @Setter @Getter @Nullable private char[] SOLICITACAO;
    @Setter @Getter @Nullable private BigDecimal CODPARC;
    @Setter @Getter @Nullable private String NOMEPARC;
    @Setter @Getter @Nullable private String RAZAOSOCIAL;
    @Setter @Getter @Nullable private String CGC_CPF;
    @Setter @Getter @Nullable private BigDecimal CODVEICULO;
    @Setter @Getter @Nullable private BigDecimal CODMARCA;
    @Setter @Getter @Nullable private BigDecimal CODMODELO;
    @Setter @Getter @Nullable private BigDecimal CODTIPO;
    @Setter @Getter @Nullable private BigDecimal HORIMETRO;
    @Setter @Getter @Nullable private char[] PROBLEMA;

    public Object[] pk() {
        return new Object[] { IDINSTPRN, IDINSTTAR, CODREGISTRO };
    }
}
