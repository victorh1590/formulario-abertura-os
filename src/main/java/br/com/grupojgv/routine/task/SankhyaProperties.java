package br.com.grupojgv.routine.task;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:application.properties"})
public interface SankhyaProperties extends Config {
    @Key("sankhya.url")
    String sankhyaUrl();
    @Key("sankhya.module")
    String sankhyaModule();
    @Key("sankhya.resource.pv")
    String sankhyaPortalVendas();
    @Key("sankhya.resource.nota")
    String sankhyaOrcamento();
}
