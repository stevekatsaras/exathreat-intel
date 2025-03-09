package com.exathreat.scheduler;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.config.factory.ElasticsearchFactory;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexScheduler {

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;
	
	@Scheduled(cron = "@daily")	// runs daily at midnight
	public void purge() {
		try {
			ZonedDateTime purgeDate = ZonedDateTime.now(ZoneOffset.UTC).minusDays(28);

			DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest("exathreat-intel");
			deleteByQueryRequest.setQuery(QueryBuilders.rangeQuery("@timestamp").lt(purgeDate));
			elasticsearchFactory.deleteByQuery(deleteByQueryRequest);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}