package br.com.grupojgv.model;

import br.com.grupojgv.annotations.PkMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Builder
public class OrcamentoOS implements FormularioFormatado {
    @PkMember @Setter @Getter @Nullable
    private BigDecimal IDINSTPRN;
    @PkMember @Setter @Getter @Nullable
    private BigDecimal IDINSTTAR;
    @PkMember @Setter @Getter @Nullable
    private String IDTAREFA;
    @PkMember @Setter @Getter @Nullable
    private BigDecimal CODREGISTRO;
    @Setter @Getter @Nullable private BigDecimal CODPARC;
    @Setter @Getter @Nullable private BigDecimal CODEMP;
    @Setter @Getter @Nullable private BigDecimal CODNAT;
    @Setter @Getter @Nullable private BigDecimal CODCENCUS;
    @Setter @Getter @Nullable private BigDecimal CODVEICULO;
    @Setter @Getter @Nullable private BigDecimal AD_CODOAT;
    @Setter @Getter @Nullable private BigDecimal AD_TIPODEOS;
    @Setter @Getter @Nullable private BigDecimal NUMNOTA;
    @Setter @Getter @Nullable private BigDecimal AD_HORIMETRO;
    @Setter @Getter @Nullable private String AD_FLAGVEIDIFPARC;

    public Object[] pk() {
        return new Object[] { IDINSTPRN, IDINSTTAR, CODREGISTRO };
    }
}
