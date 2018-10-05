package com.lazygalaxy.domain;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.lazygalaxy.utils.GeneralUtil;

public abstract class MongoDocument {
	protected static String buildId(String seperator, String... parts) {
		// none of the parts of the id should be null
		for (Object part : parts) {
			if (part == null) {
				return null;
			}
		}
		return String.join(seperator, parts);
	}

	public String id;
	public String name;
	public Set<String> labels;

	public MongoDocument() {
	}

	public MongoDocument(String id, String name, String[] labels) throws Exception {
		if (id == null) {
			throw new Exception("null id not valid");
		}
		this.id = id.toLowerCase();
		this.name = name;

		if (labels != null) {
			this.labels = new TreeSet<String>();
			addLabel(name);
			for (String label : labels) {
				addLabel(label);
			}
		}
	}

	public void addLabel(String label) {
		this.labels.add(GeneralUtil.alphanumerify(label));
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}