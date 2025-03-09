package com.exathreat.common.config.factory;

import java.util.List;

import javax.annotation.PostConstruct;

import com.exathreat.scheduler.FeedScheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FeedFactory {

	@Autowired
	private MessageChannel batchInChannel;

	@Autowired
	@Qualifier("feeds")
	private List<FeedSettings> feeds;

	@Autowired
	public ThreadPoolTaskScheduler threadPoolTaskScheduler;

	@Autowired
	private WebClient webClient;

	@PostConstruct
	public void init() {
		for (FeedSettings feedSettings : feeds) {
			if (feedSettings.getEnabled()) {
				FeedScheduler feedScheduler = new FeedScheduler(feedSettings, webClient, batchInChannel);
				threadPoolTaskScheduler.schedule(feedScheduler, new CronTrigger(feedSettings.getCron()));
			}
		}
	}
}