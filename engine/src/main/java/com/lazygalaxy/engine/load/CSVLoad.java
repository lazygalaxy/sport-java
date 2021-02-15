package com.lazygalaxy.engine.load;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lazygalaxy.engine.domain.MongoDocument;
import com.lazygalaxy.engine.helper.MongoHelper;

public abstract class CSVLoad<T extends MongoDocument> {
	private static final Logger LOGGER = LogManager.getLogger(CSVLoad.class);

	private final MongoHelper<T> helper;

	public CSVLoad(Class<T> clazz) throws Exception {
		this.helper = MongoHelper.getHelper(clazz);
	}

	public void load(String file, boolean merge) throws Exception {
		Stream<String> lines = Files.lines(Paths.get(ClassLoader.getSystemResource(file).toURI()));
		lines.forEach(s -> {
			String[] tokens = s.split(",");
			try {
				T document = getMongoDocument(tokens);
				helper.upsert(document);
			} catch (Exception e) {
				LOGGER.error("could not process: " + s, e);
			}

		});
		lines.close();
	}

	protected abstract T getMongoDocument(String[] tokens) throws Exception;
}
