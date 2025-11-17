package br.com.grupojgv.model;

import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Value
@Builder
public class Veiculo {
    @Nullable BigDecimal CODVEICULO;
    @Nullable BigDecimal CODMARCA;
    @Nullable BigDecimal CODMODELO;
    @Nullable BigDecimal CODTIPO;
}
