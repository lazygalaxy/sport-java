package com.lazygalaxy.load.world.wikipedia;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lazygalaxy.domain.world.Image;
import com.lazygalaxy.domain.world.WikipediaPage;
import com.lazygalaxy.load.jsoup.JSoupElementProcessor;
import com.lazygalaxy.load.jsoup.TableJSoupElementProcessor;
import com.lazygalaxy.load.jsoup.TableJSoupElementProcessor.JSoupTable;
import com.lazygalaxy.load.jsoup.TextJSoupElementProcessor;
import com.mongodb.client.model.geojson.Point;

public class WHSByCountryProcessor implements JSoupElementProcessor<List<WikipediaPage>> {
	private static final TextJSoupElementProcessor TEXT_PROCESSOR = new TextJSoupElementProcessor(". ");
	private static final WikipediaImageProcessor IMAGE_PROCESSOR = new WikipediaImageProcessor();
	private static final WikipediaTitleProcessor TITLE_PROCESSOR = new WikipediaTitleProcessor();
	private static final TableJSoupElementProcessor TABLE_PROCESSOR = new TableJSoupElementProcessor();
	private static final WikipediaCoordsProcessor COORDS_PROCESSOR = new WikipediaCoordsProcessor();

	@Override
	public List<WikipediaPage> apply(Element element) throws Exception {
		List<WikipediaPage> pageList = new ArrayList<WikipediaPage>();

		Elements tables = element.select("table[class*=wikitable]");
		for (Element tableElement : tables) {
			JSoupTable jsoupTable = TABLE_PROCESSOR.apply(tableElement);

			for (int i = 0; i < jsoupTable.getDataRows(); i++) {
				List<String> titles = jsoupTable.process(i, "site", TITLE_PROCESSOR);
				titles.addAll(jsoupTable.process(i, "name", TITLE_PROCESSOR));

				String summary = jsoupTable.process(i, "site", TEXT_PROCESSOR)
						+ jsoupTable.process(i, "name", TEXT_PROCESSOR)
						+ jsoupTable.process(i, "description", TEXT_PROCESSOR);
				List<Image> images = jsoupTable.process(i, "image", IMAGE_PROCESSOR);
				Point coords = jsoupTable.process(i, "location", COORDS_PROCESSOR);

				for (String title : titles) {
					WikipediaPage page = new WikipediaPage(title, new String[] {});
					page.summary = summary;
					page.coords = coords;
					if (!images.isEmpty()) {
						page.image = images.get(0);
					}
					pageList.add(page);
				}
			}
		}

		return pageList;
	}

	@Override
	public List<WikipediaPage> empty() {
		return new ArrayList<WikipediaPage>();
	}

}
