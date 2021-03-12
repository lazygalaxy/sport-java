package com.lazygalaxy.game.main;

import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import com.lazygalaxy.engine.helper.MongoConnectionHelper;
import com.lazygalaxy.engine.load.XMLLoad;
import com.lazygalaxy.engine.merge.Merge;
import com.lazygalaxy.engine.util.GeneralUtil;
import com.lazygalaxy.engine.util.XMLUtil;
import com.lazygalaxy.game.Constant.Genre;
import com.lazygalaxy.game.domain.Game;
import com.lazygalaxy.game.merge.GameMerge;
import com.lazygalaxy.game.util.GameUtil;
import com.mongodb.client.model.Filters;

public class B_RunVManEnrichGameLoad {
	private static final Logger LOGGER = LogManager.getLogger(B_RunVManEnrichGameLoad.class);

	public static void main(String[] args) throws Exception {
		try {
			Merge<Game> merge = new GameMerge();

			new RetroArchGameLoad().load("xml/vman/retroarch_arcade_games.xml", "game", merge);
			new RetroArchGameLoad().load("xml/vman/retroarch_atomiswave_games.xml", "game", merge);
			new RetroArchGameLoad().load("xml/vman/retroarch_daphne_games.xml", "game", merge);
			new RetroArchGameLoad().load("xml/vman/retroarch_naomi_games.xml", "game", merge);
			new RetroArchGameLoad().load("xml/vman/retroarch_neogeo_games.xml", "game", merge);
			LOGGER.info("vman enrich load completed!");

		} finally {
			MongoConnectionHelper.INSTANCE.close();
		}
	}

	private static class RetroArchGameLoad extends XMLLoad<Game> {

		public RetroArchGameLoad() throws Exception {
			super(Game.class);
		}

		@Override
		protected List<Game> getMongoDocument(Element element, List<String> extraTagValues) throws Exception {
			String path = XMLUtil.getTagAsString(element, "path", 0);
			String rom = StringUtils.substring(path, 0, StringUtils.lastIndexOf(path, "."));
			rom = StringUtils.substring(rom, StringUtils.lastIndexOf(rom, "/") + 1, rom.length());

			String image = XMLUtil.getTagAsString(element, "image", 0);
			String alternativeRom = null;
			if (image != null) {
				alternativeRom = StringUtils.substring(image, 0, StringUtils.lastIndexOf(image, "."));
				alternativeRom = StringUtils.substring(alternativeRom, StringUtils.lastIndexOf(alternativeRom, "/") + 1,
						alternativeRom.length());
			}
			String name = GeneralUtil.alphanumerify(GameUtil.pretify(XMLUtil.getTagAsString(element, "name", 0)));
			String year = StringUtils.left(XMLUtil.getTagAsString(element, "releasedate", 0), 4);

			List<Game> games = GameUtil.getGames(false, true, name + " " + year,
					Filters.and(Filters.in("labels", name), Filters.eq("year", year)));

			if (games == null) {
				games = GameUtil.getGames(true, true, rom + " " + alternativeRom,
						Filters.or(Filters.eq("rom", rom), Filters.eq("rom", alternativeRom), Filters.in("clones", rom),
								Filters.in("clones", alternativeRom)));
			}

			if (games != null) {
				for (Game game : games) {
					game.description = GameUtil.pretify(XMLUtil.getTagAsString(element, "desc", 0));

					String genreString = XMLUtil.getTagAsString(element, "genre", 0);
					if (genreString != null) {
						game.genres = new TreeSet<String>();
						genreString = genreString.toLowerCase();
						genreString = genreString.replaceAll("plateform", "platform");
						genreString = genreString.replaceAll(" and ", " ");
						String[] genreArray = GeneralUtil.split(genreString, "[/,-]");

						for (String token : genreArray) {
							token = token.replace("swimming", Genre.POOL);
							token = token.replace("sports", Genre.SPORT);
							token = token.replace("lightgun shooter", Genre.LIGHTGUN);
							game.addGenre(token);
						}
					}
				}

				return games;
			}
			return null;
		}
	}
}
