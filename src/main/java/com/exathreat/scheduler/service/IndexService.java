package com.exathreat.scheduler.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.exathreat.common.config.factory.ElasticsearchFactory;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IndexService {

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;
	
	@ServiceActivator(inputChannel = "indexInChannel", poller = @Poller(fixedDelay = "3000", maxMessagesPerPoll = "1"))
	public void write(Message<List<Map<String, Object>>> msg) {
		List<Map<String, Object>> iocList = msg.getPayload();
		try {
			BulkRequest bulkRequest = new BulkRequest();

			for (Map<String, Object> iocMap : iocList) {
				iocMap.put("@timestamp", DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now(ZoneOffset.UTC)));
				iocMap.put("event", "IOC " + iocMap.get("ioc") + " of type " + iocMap.get("type") + " in category " + iocMap.get("category") + " and confidence score of " + iocMap.get("confidence") + ".");
				bulkRequest.add(new IndexRequest("exathreat-intel").source(iocMap));
			}

			BulkResponse bulkResponse = elasticsearchFactory.bulkDocuments(bulkRequest);
			if (bulkResponse == null) {
				throw new Exception("BulkResponse is null");
			}
			else if (bulkResponse.hasFailures()) {
				int bulkFailedItemsCount = 0;
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						bulkFailedItemsCount++;
					}
				}
				log.error("[IndexService.write] - failed to batch " + bulkFailedItemsCount + " documents. error: " + bulkResponse.buildFailureMessage() + ".");
			}
		}
		catch (Exception exception) {
			log.error("[IndexService.write] - exception: " + exception.getMessage() + ".", exception);
		}
	}
}