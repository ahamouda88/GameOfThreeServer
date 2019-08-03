package com.challenge.got.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.got.exception.model.InvalidCurrentPlayerException;
import com.challenge.got.exception.model.InvalidGameException;
import com.challenge.got.exception.model.NotFoundException;
import com.challenge.got.factory.GameFactory;
import com.challenge.got.persist.dao.GameDao;
import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.GameMove;
import com.challenge.got.persist.model.GameStatus;
import com.challenge.got.persist.model.Player;
import com.challenge.got.persist.model.ValueAdded;
import com.challenge.got.service.api.GameService;

/**
 * A class that implements {@link GameService}
 */
@Service
@Transactional
public class GameServiceImpl implements GameService<Long, Long> {

	public static final int WINNING_RESULT = 1;

	@Autowired
	private GameDao gameDao;

	@Override
	public GOTGame addGameMove(Long gameId, Long playerId, Integer move) {
		// Doing all sort of validations before updating the game
		if (gameId == null || move == null || playerId == null) {
			throw new IllegalArgumentException("Neither game Id nor move should be null!");
		}

		if (ValueAdded.getEnumValue(move) == null) {
			throw new IllegalArgumentException("Invalid move value, value should be one of the following: -1, 0, 1");
		}

		GOTGame game = this.getById(gameId);
		if (game == null) {
			throw new NotFoundException(String.format("Game with the following id %s doesn't exist", gameId));
		}

		GameStatus status = game.getGameStatus();
		if (status.getWinnerPlayer() != null) {
			throw new InvalidGameException("Game is already over!");
		}

		Player currentPlayer = status.getCurrentPlayer();
		if (currentPlayer == null || currentPlayer.getId() != playerId) {
			throw new InvalidCurrentPlayerException("Invalid move, current player is incorrect");
		}

		// Calculate Result
		int result = (status.getCurrentNumber() + move) / 3;
		GameMove gameMove = GameFactory.createGameMove(result, move);
		game.addMove(gameMove);

		Player nextPlayer = game.getPlayer1()
								.getId() == playerId ? game.getPlayer2() : game.getPlayer1();
		Player winnerPlayer = result == WINNING_RESULT ? status.getCurrentPlayer() : null;

		GameStatus newStatus = GameFactory.createGameStatus(result, nextPlayer, winnerPlayer);
		game.setGameStatus(newStatus);
		return this.createOrUpdate(game);
	}

	@Override
	public JpaRepository<GOTGame, Long> getRepository() {
		return gameDao;
	}
}
