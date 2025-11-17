package br.com.grupojgv.model;

import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Value
@Builder
public class Parceiro {
    @Nullable BigDecimal CODPARC;
    @Nullable String NOMEPARC;
    @Nullable String RAZAOSOCIAL;
    @Nullable String CGC_CPF;
}
