package br.com.grupojgv;

import br.com.grupojgv.routine.task.SankhyaUrlBuilder;
import com.sankhya.util.JsonUtils;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@JBossLog
public class URLTest {
    @Test
    public void createUrlTest() throws ConfigurationException {
        PropertiesConfiguration cfg = (new Configurations()).properties(new File("application.properties"));
        Object nunota = new BigDecimal(1111);
        Map<String, String> resourceProperties = new HashMap<>();
        resourceProperties.put("NUNOTA", nunota.toString());
        String resource =
            String.format(
                "%s?%s",
                cfg.getProperty("sankhya.resource.orcamento").toString(),
                JsonUtils.getGson().toJson(resourceProperties)
            );
        String url = SankhyaUrlBuilder.builder()
            .protocol("https")
            .host(cfg.getProperty("sankhya.url").toString())
            .module(cfg.getProperty("sankhya.module").toString())
            .path("system.jsp#app")
            .path(Base64.getEncoder().encodeToString(resource.getBytes()))
            .build()
            .url();
        String link = String.format("<a href=%s>%s</a>", url, nunota);
        log.info("URL: " + url);
        log.info("LINK: " + link);
    }
}
