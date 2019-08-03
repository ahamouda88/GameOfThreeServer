package com.challenge.got.rest.constants;

/**
 * An interface that contains all the constant endpoint paths
 */
public interface PathConstants {

	String MAIN_PATH = "/api/v1/got";
	String MAIN_ID_PATH = "/{id}";
	String PLAYERS_PATH = "/players";
	String PLAYERS_FULL_PATH = MAIN_PATH + PLAYERS_PATH;

	String GAMES_PATH = "/games";
	String GAMES_FULL_PATH = MAIN_PATH + GAMES_PATH;
	String GAMES_PLAYERS_PATH = "/{gameId}" + PLAYERS_PATH + "/{playerId}";
}
