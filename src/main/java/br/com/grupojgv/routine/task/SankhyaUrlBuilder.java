package br.com.grupojgv.routine.task;

import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder
public class SankhyaUrlBuilder {
    private String protocol;
    private String host;
    private String port;
    private String module;
    @Singular
    private List<String> paths;
    @Builder.Default
    private String paramConnector = "?";
    @Singular
    private Map<String, String> params;

    public String url() {
        StringBuilder sb = new StringBuilder();
        if(protocol != null && !protocol.isEmpty()) {
            sb.append(protocol).append("://");
        }
        sb.append(host);
        if(port != null && !port.isEmpty()) {
            sb.append(":").append(port);
        }
        if(module != null && !module.isEmpty()) {
            sb.append("/").append(module);
        }
        for(String path: paths) {
            sb.append("/").append(path);
        }
        if(params != null && !params.isEmpty()) {
            sb.append(paramConnector);
            for(String key: params.keySet()) {
                sb.append(key).append("=").append(params.get(key));
            }
        }
        return sb.toString();
    }
}
