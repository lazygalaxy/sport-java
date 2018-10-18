package com.lazygalaxy.load.world.wikipedia;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.lazygalaxy.load.jsoup.JSoupElementProcessor;

public class WikipediaSummaryProcessor implements JSoupElementProcessor<String> {
	@Override
	public String apply(Element element) throws Exception {
		String summary = "";

		for (Element paragraph : element.select("p,h2")) {
			if (paragraph.is("p")) {
				summary += paragraph.text().trim() + " ";
			} else {
				break;
			}
		}
		if (StringUtils.isBlank(summary)) {
			return empty();
		}
		return summary.trim();
	}

	@Override
	public String empty() {
		return null;
	}

}
