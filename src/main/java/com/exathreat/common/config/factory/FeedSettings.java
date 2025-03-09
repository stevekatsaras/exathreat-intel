package com.exathreat.common.config.factory;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class FeedSettings {
	private String code;
	private String name;
	private String managed;
	private String url;
	private String cron;
	private Boolean enabled;
	private String category;
	private String type;
	private Integer confidence;
	private List<String> excludeRegex;		// what lines to exclude
	private List<String> includeRegex;		// what lines to include
	private String delimeter;							// CSV
	private String fieldScrapeRegex;			// CSV
	private List<String> fieldTransform;	// CSV - list of: old-value:new-value
	private List<String> fields;					// CSV - list of: field-index:field-name
}