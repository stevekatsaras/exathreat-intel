package com.exathreat.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exathreat.common.config.factory.FeedSettings;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.reactive.function.client.WebClient;

public class FeedScheduler implements Runnable {
	private FeedSettings feedSettings;
	private WebClient webClient;
	private MessageChannel messageChannel;

	public FeedScheduler(FeedSettings feedSettings, WebClient webClient, MessageChannel messageChannel) {
		this.feedSettings = feedSettings;
		this.webClient = webClient;
		this.messageChannel = messageChannel;
	}

	@Override
	public void run() {
		try {
			List<String> responseItems = fetch();
			exclude(responseItems);
			include(responseItems);

			for (String responseItem : responseItems) {
				Map<String, Object> iocMap = map(responseItem);
				messageChannel.send(new GenericMessage<Map<String, Object>>(iocMap, Map.of("ioc", "*")));
			}
		}
		catch (Exception exception) {
			System.err.println("[FeedScheduler.run." + feedSettings.getCode() + "] - exception: " + exception.getMessage() + ".");
		}
	}

	// fetch response from URL and split into line items
	private List<String> fetch() {
		String responseBodyStr = webClient.get().uri(feedSettings.getUrl()).retrieve().bodyToMono(String.class).block();
		return new ArrayList<String>(Arrays.asList(responseBodyStr.split("\\r?\\n")));
	}

	// excludes response items that match any of the exclude-regex patterns
	private void exclude(List<String> responseItems) {
		if (feedSettings.getExcludeRegex() != null) {
			for (String regex : feedSettings.getExcludeRegex()) {
				responseItems.removeIf(lineItem -> lineItem.matches(regex));
			}
		}
	}

	// remove response items that don't match any of the include-regex patterns
	private void include(List<String> responseItems) {
		if (feedSettings.getIncludeRegex() != null) {
			for (String regex : feedSettings.getIncludeRegex()) {
				responseItems.removeIf(lineItem -> !lineItem.matches(regex));
			}
		}
	}

	// map the response item either as "fields" or simple text
	private Map<String, Object> map(String responseItem) {
		Map<String, Object> iocMap = new HashMap<String, Object>(Map.of(
			"code", feedSettings.getCode(), 
			"name", feedSettings.getName(), 
			"provider", feedSettings.getManaged(),
			"category", feedSettings.getCategory(), 
			"type", feedSettings.getType(), 
			"confidence", feedSettings.getConfidence()
		));

		if (feedSettings.getDelimeter() != null) {
			String[] itemFields = responseItem.split(feedSettings.getDelimeter());
			for (String field : feedSettings.getFields()) {
				Integer fieldIndex = Integer.parseInt(field.split(":")[0]);
				String fieldName = field.split(":")[1];
				String fieldValue = itemFields[fieldIndex];

				fieldValue = scrape(fieldValue);
				fieldValue = transform(fieldValue);
				iocMap.put(fieldName, fieldValue);
			}
		}
		else {
			iocMap.put("ioc", responseItem);
		}
		return iocMap;
	}

	// scrapes the text of a field, returning subset of text and removing unwanted text
	private Pattern scrapePattern;
	private String scrape(String field) {
		if (feedSettings.getFieldScrapeRegex() != null) {
			scrapePattern = (scrapePattern != null) ? scrapePattern : Pattern.compile(feedSettings.getFieldScrapeRegex());
			Matcher matcher = scrapePattern.matcher(field);
			if (matcher.find()) {
				field = matcher.group(1);
			}
		}
		return field;
	}

	// transforms the text of a field, given a list of transformations
	private String transform(String field) {
		if (feedSettings.getFieldTransform() != null) {
			for (String transform : feedSettings.getFieldTransform()) {
				String oldValue = transform.split("=")[0];
				String newValue = transform.split("=")[1];
				field = field.replace(oldValue, newValue);
			}
		}
		return field;
	}

}