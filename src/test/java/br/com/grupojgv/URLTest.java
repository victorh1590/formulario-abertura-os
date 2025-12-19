package br.com.grupojgv;

import br.com.grupojgv.routine.task.SankhyaProperties;
import br.com.grupojgv.routine.task.SankhyaUrlBuilder;
import com.sankhya.util.JsonUtils;
import lombok.extern.jbosslog.JBossLog;
import org.junit.Test;

import javax.naming.ConfigurationException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@JBossLog
public class URLTest {
    @Test
    public void createUrlTest() throws ConfigurationException {
        BigDecimal nunota = new BigDecimal(1111);
        // Fazer envio de Aviso.
        SankhyaProperties cfg = org.aeonbits.owner.ConfigFactory.create(SankhyaProperties.class);
        Map<String, String> resourceProperties = new HashMap<>();
        resourceProperties.put("NUNOTA", nunota.toString());
        String resource =
            String.format(
                "%s?%s",
                cfg.sankhyaOrcamento(),
                JsonUtils.getGson().toJson(resourceProperties)
            );
        String url = SankhyaUrlBuilder.builder()
            .protocol("https")
            .host(cfg.sankhyaUrl())
            .module(cfg.sankhyaModule())
            .path("system.jsp#app")
            .path(Base64.getEncoder().encodeToString(resource.getBytes()))
            .build()
            .url();
        String link = String.format("<a href=%s>%s</a>", url, nunota);
        log.info("URL: " + url);
        log.info("LINK: " + link);
    }
}
