package com.challenge.got.factory;

import java.util.ArrayList;
import java.util.List;

import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.GameMove;
import com.challenge.got.persist.model.GameStatus;
import com.challenge.got.persist.model.Player;
import com.challenge.got.persist.model.ValueAdded;

/**
 * A Utility interface that creates objects of {@link Game}, and its instances
 * such as {@link GameStatus} and {@link GameMove}
 */
public interface GameFactory {

	/**
	 * A Utility method that creates an instance of {@link GameStatus}
	 * 
	 * @param currentNumber
	 *            the updated number
	 * @param currentPlayer
	 *            the players turn
	 * @return an instance of {@link GameStatus}
	 */
	static GameStatus createGameStatus(Integer currentNumber, Player currentPlayer) {
		return createGameStatus(currentNumber, currentPlayer, null);
	}

	/**
	 * A Utility method that creates an instance of {@link GameStatus}
	 * 
	 * @param currentNumber
	 *            the updated number
	 * @param currentPlayer
	 *            the players turn
	 * @param winnerPlayer
	 *            the winner player
	 * @return an instance of {@link GameStatus}
	 */
	static GameStatus createGameStatus(Integer currentNumber, Player currentPlayer, Player winnerPlayer) {
		if (currentNumber == null) {
			throw new IllegalArgumentException("Current number should not be null");
		}

		GameStatus status = new GameStatus();
		status.setCurrentNumber(currentNumber);
		status.setCurrentPlayer(currentPlayer);
		status.setWinnerPlayer(winnerPlayer);
		return status;
	}

	/**
	 * A Utility method that creates an instance of {@link GameMove}
	 * 
	 * @param result
	 *            the resulted value
	 * @param value
	 *            the integer representation of the {@link ValueAdded} enum
	 * 
	 * @return an instance of {@link GameMove}
	 */
	static GameMove createGameMove(Integer result, int value) {
		return createGameMove(result, ValueAdded.getEnumValue(value));
	}

	/**
	 * A Utility method that creates an instance of {@link GOTGame}
	 * 
	 * @param gameStatus
	 *            a {@link GameStatus} instance
	 * @param initNumber
	 *            the starting number of the game
	 * @param player1
	 *            the first player
	 * @return a instance of {@link GOTGame}
	 */
	static GOTGame createGame(GameStatus gameStatus, Integer initNumber, Player player1) {
		return createGame(gameStatus, initNumber, player1, null, new ArrayList<>());
	}

	/**
	 * A Utility method that creates an instance of {@link GOTGame}
	 * 
	 * @param gameStatus
	 *            a {@link GameStatus} instance
	 * @param initNumber
	 *            the starting number of the game
	 * @param player1
	 *            the first player
	 * @param player2
	 *            the second player
	 * @param gameMoves
	 *            a list of {@link GameMove}
	 * @return a instance of {@link GOTGame}
	 */
	static GOTGame createGame(GameStatus gameStatus, Integer initNumber, Player player1, Player player2,
			List<GameMove> gameMoves) {
		if (gameStatus == null || initNumber == null || player1 == null) {
			throw new IllegalArgumentException("Game Status, Initial Number, and First player should not be null");
		}

		GOTGame game = new GOTGame();
		game.setGameStatus(gameStatus);
		game.setInitialNumber(initNumber);
		game.setPlayer1(player1);
		game.setPlayer2(player2);
		game.setGameMoves(gameMoves);
		return game;
	}

	/**
	 * A Utility method that creates an instance of {@link GameMove}
	 * 
	 * @param result
	 *            the resulted value
	 * @param valueAdded
	 *            a {@link ValueAdded} object
	 * 
	 * @return an instance of {@link GameMove}
	 */
	static GameMove createGameMove(Integer result, ValueAdded valueAdded) {
		if (result == null || valueAdded == null)
			throw new IllegalArgumentException("Neither value added nor number can be null");

		GameMove move = new GameMove();
		move.setResult(result);
		move.setValueAdded(valueAdded);
		return move;
	}
}
