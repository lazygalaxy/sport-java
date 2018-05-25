package com.lazygalaxy.sport.load.csv;

import java.util.Arrays;

import com.lazygalaxy.sport.domain.Country;
import com.lazygalaxy.sport.domain.Team;
import com.lazygalaxy.sport.helpers.MongoHelper;

public class TeamCSVLoad extends CSVLoad<Team> {

	private MongoHelper<Country> countryHelper = MongoHelper.getHelper(Country.class);

	public TeamCSVLoad() {
		super(Team.class);
	}

	@Override
	protected Team getMongoDocument(String[] tokens) {
		Country country = countryHelper.getDocumentByLabel(tokens[2]);
		Team team = new Team(tokens[0], tokens[1], Arrays.copyOfRange(tokens, 3, tokens.length), country);

		return team;
	}

}