package com.challenge.got.factory;

import com.challenge.got.persist.model.Player;

/**
 * A Utility interface that creates objects of {@link Player}
 */
public interface PlayerFactory {

	/**
	 * A Utility method that creates an instance of {@link Player}
	 * 
	 * @param name
	 *            the player's name
	 * @return a {@link Player} instance
	 */
	static Player createPlayer(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Player's name should not be null!");
		}

		Player player = new Player();
		player.setName(name);
		return player;
	}
}
