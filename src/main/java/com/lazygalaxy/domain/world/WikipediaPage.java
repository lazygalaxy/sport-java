package com.lazygalaxy.domain.world;

import com.lazygalaxy.domain.MongoDocument;
import com.mongodb.client.model.geojson.Point;

public class WikipediaPage extends MongoDocument {
	public String processor;
	public boolean mustUpdate = false;
	public Image image;
	public String summary;
	public Point coords;
	public String location;

	public WikipediaPage() {
	}

	public WikipediaPage(String name, String[] labels) throws Exception {
		super(name, name, labels);
	}
}
