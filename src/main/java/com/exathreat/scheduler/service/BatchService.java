package com.exathreat.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.HeaderAttributeCorrelationStrategy;
import org.springframework.integration.aggregator.TimeoutCountSequenceSizeReleaseStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

	@Autowired
	private MessageChannel enrichmentInChannel;
	
	@Bean
	@ServiceActivator(inputChannel = "batchInChannel")
	public MessageHandler aggregator() {
		AggregatingMessageHandler aggregator = new AggregatingMessageHandler(
			new DefaultAggregatingMessageGroupProcessor(), 
			new SimpleMessageStore(),
			new HeaderAttributeCorrelationStrategy("ioc"), 
			new TimeoutCountSequenceSizeReleaseStrategy(50, 1000));
			
		aggregator.setOutputChannel(enrichmentInChannel);
		aggregator.setExpireGroupsUponCompletion(true);
		aggregator.setExpireGroupsUponTimeout(true);
		return aggregator;
	}
}