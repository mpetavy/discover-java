package org.mpetavy.discover;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "discover")
public class DiscoverConfiguration {

    public int port;
    public String uid;
    public int timeout;
}