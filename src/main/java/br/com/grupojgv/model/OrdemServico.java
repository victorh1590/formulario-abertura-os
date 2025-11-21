package br.com.grupojgv.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrdemServico {
    BigDecimal codparc;
    BigDecimal codveiculo;
    BigDecimal codmarca;
    BigDecimal codmodelo;
    String problema;
}
