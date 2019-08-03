package com.challenge.got.service.api;

import java.io.Serializable;

import com.challenge.got.persist.model.GOTGame;

/**
 * An interface that extends {@link MainService}, and operates on
 * {@link GOTGame} models
 *
 * @param <E>
 *            the {@link GOTGame} id's data type
 */
public interface GameService<E extends Serializable, P extends Serializable> extends MainService<GOTGame, E> {

	/**
	 * This method updates a game by adding a new move
	 * 
	 * @param gameId
	 *            the id of the game
	 * @param playerId
	 *            the id of the player who did the move
	 * @param move
	 *            the added number
	 * @return the updated game
	 */
	GOTGame addGameMove(E gameId, P playerId, Integer move);

}
