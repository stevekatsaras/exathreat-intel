package com.exathreat.common.config.factory;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchFactory {

	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	public BulkResponse bulkDocuments(BulkRequest bulkRequest) throws Exception {
		return elasticsearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
	}

	public BulkByScrollResponse deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) throws Exception {
		return elasticsearchClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
	}
}