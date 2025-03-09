package com.exathreat.common.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.exathreat.common.config.factory.FeedSettings;
import com.exathreat.common.config.factory.ElasticsearchSettings;
import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class ApplicationConfig {

	@Autowired
	private ElasticsearchSettings elasticsearchSettings;

	@Autowired
	private ResourceLoader resourceLoader;
	
	@PostConstruct
	public void init() {
		log.info("elasticsearchSettings: " + elasticsearchSettings);
		log.info("feedSettings: " + feeds());
	}

	@Bean
	@ConfigurationProperties(prefix = "feeds")
	public List<FeedSettings> feeds() {
		return new ArrayList<FeedSettings>();
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(30);
		return threadPoolTaskScheduler;
	}

	@Bean
	public RestHighLevelClient elasticsearchClient() {
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(
			elasticsearchSettings.getDomain(), 
			elasticsearchSettings.getPort(), 
			elasticsearchSettings.getScheme()));

		restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
			.setDefaultIOReactorConfig(IOReactorConfig.custom()
				.setSoKeepAlive(true)
				.build()));

		restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> 
			requestConfigBuilder.setConnectTimeout(elasticsearchSettings.getConnectTimeout())
				.setSocketTimeout(elasticsearchSettings.getSocketTimeout()));

		return new RestHighLevelClient(restClientBuilder);
  }

	// max mind

	@Bean
	public DatabaseReader geoLite2ASNReader() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:maxmind/GeoLite2-ASN.mmdb");
		return new DatabaseReader
			.Builder(resource.getInputStream())
			.fileMode(Reader.FileMode.MEMORY)
			.withCache(new CHMCache())
      .build();
	}

	@Bean
	public DatabaseReader geoLite2CityReader() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:maxmind/GeoLite2-City.mmdb");
		return new DatabaseReader
			.Builder(resource.getInputStream())
			.fileMode(Reader.FileMode.MEMORY)
			.withCache(new CHMCache())
      .build();
	}

	@Bean
	public WebClient webClient() throws Exception {
		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

		return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
			.codecs(configurer -> configurer
				.defaultCodecs()
				.maxInMemorySize(16 * 1024 * 1024))	// 16MB
			.build())
			.clientConnector(new ReactorClientHttpConnector(httpClient))
		.build();
	}

}