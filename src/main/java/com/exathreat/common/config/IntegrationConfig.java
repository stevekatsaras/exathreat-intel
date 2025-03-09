package com.exathreat.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@Import({ ApplicationConfig.class })
public class IntegrationConfig {
	
	@Bean
	public MessageChannel batchInChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel enrichmentInChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel indexInChannel() {
		return new QueueChannel();
	}
}