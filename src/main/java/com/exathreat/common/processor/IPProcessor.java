package com.exathreat.common.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class IPProcessor {
	private Pattern pattern = Pattern.compile("(?<!(-|\\d))(((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?!\\d)");
	
	public String extract(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			line = matcher.group();
		}
		return line;
	}
}