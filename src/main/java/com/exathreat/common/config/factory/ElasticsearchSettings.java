package com.exathreat.common.config.factory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ElasticsearchSettings {
	private String domain;
	private int port;
	private String scheme;
	private int connectTimeout;
	private int socketTimeout;
}