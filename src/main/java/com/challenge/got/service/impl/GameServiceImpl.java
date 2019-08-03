package com.challenge.got.service.impl;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.got.exception.model.InvalidCurrentPlayerException;
import com.challenge.got.exception.model.InvalidGameException;
import com.challenge.got.exception.model.NotFoundException;
import com.challenge.got.factory.GameFactory;
import com.challenge.got.persist.dao.GameDao;
import com.challenge.got.persist.dao.PlayerDao;
import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.GameMove;
import com.challenge.got.persist.model.GameStatus;
import com.challenge.got.persist.model.Player;
import com.challenge.got.persist.model.ValueAdded;
import com.challenge.got.service.api.GameService;

import lombok.extern.slf4j.Slf4j;

/**
 * A class that implements {@link GameService}
 */
@Service
@Transactional
@Slf4j
public class GameServiceImpl implements GameService<Long, Long> {

	public static final int WINNING_RESULT = 1;

	@Autowired
	private GameDao gameDao;

	@Autowired
	private PlayerDao playerDao;

	@Override
	public GOTGame createOrUpdate(@Valid GOTGame game) {
		if (game == null) {
			throw new IllegalArgumentException("Failed to create or update model, model shouldnot be null!");
		}
		// Apply auto moves only if the game is full and both players are registered
		if (game.getPlayer2() != null) {
			applyAutoMoves(game);
		}
		return gameDao.save(game);
	}

	@Override
	public GOTGame addGameMove(Long gameId, Long playerId, Integer move) {
		// Doing all sort of validations before updating the game
		if (gameId == null || move == null || playerId == null) {
			throw new IllegalArgumentException("Neither game Id nor move can be null!");
		}

		if (ValueAdded.getEnumValue(move) == null) {
			throw new IllegalArgumentException("Invalid move value, value should be one of the following: -1, 0, 1");
		}

		GOTGame game = this.getById(gameId);
		if (game == null) {
			throw new NotFoundException(String.format("Game with the following id %d doesn't exist", gameId));
		}

		GameStatus status = game.getGameStatus();
		if (status.getWinnerPlayer() != null) {
			throw new InvalidGameException("Game is already over");
		}

		Player currentPlayer = status.getCurrentPlayer();
		if (currentPlayer == null || currentPlayer.getId() != playerId) {
			throw new InvalidCurrentPlayerException("Invalid move, current player is incorrect");
		}

		applyGameMove(game, playerId, move);
		applyAutoMoves(game);
		return gameDao.save(game);
	}

	@Override
	public GOTGame joinGame(Long gameId, Long playerId) {
		if (gameId == null || playerId == null) {
			throw new IllegalArgumentException("Neither game Id nor player Id can be null!");
		}

		GOTGame game = this.getById(gameId);
		if (game == null) {
			throw new NotFoundException(String.format("Game with the following id %d doesn't exist", gameId));
		}
		if (game.getPlayer2() != null) {
			throw new InvalidGameException(String.format("Game already has two players with the following Ids: %d & %d",
					game.getPlayer1()
						.getId(),
					game.getPlayer2()
						.getId()));
		}
		Player player = playerDao	.findById(playerId)
									.orElse(null);
		if (player == null) {
			throw new NotFoundException(String.format("Player with the following id %d doesn't exist", playerId));
		}
		if (player.getId() == game	.getPlayer1()
									.getId()) {
			throw new InvalidGameException("Player already registered for this game");
		}
		game.setPlayer2(player);
		/*
		 * If the first player was an auto player that means the current player is null,
		 * so we need to set it to be the new added player
		 */
		if (game.getGameStatus()
				.getCurrentPlayer() == null) {
			game.getGameStatus()
				.setCurrentPlayer(player);
		}
		// We can apply the auto moves since both players are registered
		applyAutoMoves(game);
		return gameDao.save(game);
	}

	private void applyAutoMoves(GOTGame game) {
		Player player1 = game.getPlayer1();
		Player player2 = game.getPlayer2();
		boolean player1CanMove = player1.isAuto();
		boolean player2CanMove = player2 != null ? player2.isAuto() : false;
		if (player1CanMove && player2CanMove) {
			// If two players can play automatically then play till one wins
			Long currentPlayerId = player1.getId();
			while (game	.getGameStatus()
						.getWinnerPlayer() == null) {
				applyGameMove(game, currentPlayerId, ValueAdded.getRandomValue());
				currentPlayerId = game	.getGameStatus()
										.getCurrentPlayer()
										.getId();
			}
		} else if (player1CanMove) {
			applyGameMove(game, player1.getId(), ValueAdded.getRandomValue());
		} else if (player2CanMove) {
			applyGameMove(game, player2.getId(), ValueAdded.getRandomValue());
		}
	}

	private void applyGameMove(GOTGame game, Long playerId, Integer move) {
		GameStatus status = game.getGameStatus();
		int result = (status.getCurrentNumber() + move) / 3;
		GameMove gameMove = GameFactory.createGameMove(result, move);
		game.addMove(gameMove);

		Player nextPlayer = game.getPlayer1()
								.getId() == playerId ? game.getPlayer2() : game.getPlayer1();
		Player winnerPlayer = result == WINNING_RESULT ? status.getCurrentPlayer() : null;

		log.info("Player with Id: {} added value: {}, leading to result: {}", playerId, move, result);
		GameStatus newStatus = GameFactory.createGameStatus(result, nextPlayer, winnerPlayer);
		game.setGameStatus(newStatus);
	}

	@Override
	public JpaRepository<GOTGame, Long> getRepository() {
		return gameDao;
	}
}
