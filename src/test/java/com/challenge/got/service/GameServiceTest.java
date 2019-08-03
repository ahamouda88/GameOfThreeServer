package com.challenge.got.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.got.application.MainApplication;
import com.challenge.got.exception.model.InvalidGameException;
import com.challenge.got.exception.model.NotFoundException;
import com.challenge.got.factory.GameFactory;
import com.challenge.got.factory.PlayerFactory;
import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.Player;
import com.challenge.got.service.api.GameService;
import com.challenge.got.service.api.PlayerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@DataJpaTest
@EntityScan(basePackages = "com.challenge.got.persist.model")
@ComponentScan(basePackages = { "com.challenge.got.service", "com.challenge.got.persist.dao" })
public class GameServiceTest {

	@Autowired
	private GameService<Long, Long> gameService;

	@Autowired
	private PlayerService<Long> playerService;

	private Player player1;
	private Player player2;

	private Player player3;
	private Player player4;

	@Before
	public void testCreateGame_givenValidGame_returnSavedGame() {
		// Create games
		player1 = PlayerFactory.createPlayer("Player1");
		player2 = PlayerFactory.createPlayer("Player2");
		GOTGame gameOne = createGame(player1, player2, 54);
		assertNotNull(gameOne.getId());
		assertEquals(player1, gameOne	.getGameStatus()
										.getCurrentPlayer());

		player3 = PlayerFactory.createPlayer("Player3");
		player4 = PlayerFactory.createPlayer("Player4");
		GOTGame gameTwo = createGame(player3, player4, 54);

		assertNotNull(gameTwo.getId());
		assertEquals(player3, gameTwo	.getGameStatus()
										.getCurrentPlayer());
	}

	@Test
	public void testGetAll_returnValidList() {
		List<GOTGame> games = gameService.getAll();
		assertEquals(2, games.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGame_givenNull_throwException() {
		gameService.createOrUpdate(null);
	}

	@Test
	public void testUpdateGame_givenValidGame_updateGame() {
		Integer currentNumber = 310;
		GOTGame game = gameService	.getAll()
									.get(1);
		game.setGameStatus(GameFactory.createGameStatus(currentNumber, player4));
		GOTGame updatedGame = gameService.createOrUpdate(game);

		assertEquals(currentNumber, updatedGame	.getGameStatus()
												.getCurrentNumber());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateGame_givenNull_throwException() {
		gameService.createOrUpdate(null);
	}

	@Test
	public void testDeleteGame_givenValidId_returnValidNumberOfGames() {
		long id = gameService	.getAll()
								.get(0)
								.getId();

		// Test if user is removed successfully
		gameService.deleteById(id);

		// Test number of remaining games
		List<GOTGame> games = gameService.getAll();
		assertEquals(1, games.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteGame_givenNull_throwException() {
		// Test removing game with null value
		gameService.deleteById(null);
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteGame_givenInvalidId_throwException() {
		// Test removing game with invalid Id
		gameService.deleteById(999L);
	}

	@Test
	public void testCreateGame_givenTwoAutoPlayers_returnGameWithWinnerPlayer() {
		int initNumber = 4;
		Player playerOne = PlayerFactory.createPlayer("Player1", true);
		Player playerTwo = PlayerFactory.createPlayer("Player2", true);
		GOTGame game = createGame(playerOne, playerTwo, initNumber);

		assertNotNull(game.getId());
		assertNotNull(game	.getGameStatus()
							.getWinnerPlayer());
	}

	@Test
	public void testCreateGame_givenOneAutoPlayer_alwaysReturnGameWithPlayerTwoTurn() {
		int initNumber = 40;
		Player playerOne = PlayerFactory.createPlayer("Player1", true);
		Player playerTwo = PlayerFactory.createPlayer("Player2");

		GOTGame game = createGame(playerOne, playerTwo, initNumber);

		// Test if it is the second's player turn
		assertNotNull(game.getId());
		assertEquals(playerTwo, game.getGameStatus()
									.getCurrentPlayer());

		// It still should be the second's player turn since the first player is auto
		GOTGame updatedGame = gameService.addGameMove(game.getId(), playerTwo.getId(), 1);
		assertNotNull(updatedGame.getId());
		assertEquals(game.getId(), updatedGame.getId());
		assertEquals(playerTwo, game.getGameStatus()
									.getCurrentPlayer());
	}

	@Test
	public void testJoinGame_givenValidPlayer_returnGameWithPlayerOneTurn() {
		int initNumber = 40;
		Player playerOne = PlayerFactory.createPlayer("Player1");

		// Create a game with one player only
		GOTGame game = createGame(playerOne, null, initNumber);

		assertNotNull(game.getId());
		assertNull(game.getPlayer2());

		// Test adding a player to the game
		Player playerTwo = PlayerFactory.createPlayer("Player2");
		playerService.createOrUpdate(playerTwo);

		GOTGame updatedGame = gameService.joinGame(game.getId(), playerTwo.getId());
		assertNotNull(updatedGame.getId());
		assertEquals(game.getId(), updatedGame.getId());
		// Player one should have the turn since it is not an auto player
		assertEquals(playerOne, updatedGame	.getGameStatus()
											.getCurrentPlayer());
		assertNotNull(updatedGame.getPlayer2());
	}

	@Test
	public void testJoinGame_givenValidPlayerAndAutoFirstPlayer_returnGameWithPlayerTwoTurn() {
		int initNumber = 40;
		Player playerOne = PlayerFactory.createPlayer("Player1", true);

		// Create a game with one player only
		GOTGame game = createGame(playerOne, null, initNumber);

		assertNotNull(game.getId());
		assertNull(game.getPlayer2());

		// Test adding a player to the game
		Player playerTwo = PlayerFactory.createPlayer("Player2");
		playerService.createOrUpdate(playerTwo);

		GOTGame updatedGame = gameService.joinGame(game.getId(), playerTwo.getId());
		assertNotNull(updatedGame.getId());
		assertEquals(game.getId(), updatedGame.getId());
		// Player two should have the turn since player one is an auto player
		assertEquals(playerTwo, updatedGame	.getGameStatus()
											.getCurrentPlayer());
		assertNotNull(updatedGame.getPlayer2());
	}

	@Test(expected = NotFoundException.class)
	public void testJoinGame_givenInvalidGame_throwException() {
		Player playerTwo = PlayerFactory.createPlayer("Player2");
		playerService.createOrUpdate(playerTwo);

		gameService.joinGame(100L, playerTwo.getId());
	}

	@Test(expected = NotFoundException.class)
	public void testJoinGame_givenInvalidPlayer_throwException() {
		int initNumber = 40;
		Player playerOne = PlayerFactory.createPlayer("Player1", true);

		GOTGame game = createGame(playerOne, null, initNumber);

		assertNotNull(game.getId());
		assertNull(game.getPlayer2());

		gameService.joinGame(game.getId(), 100L);
	}

	@Test(expected = InvalidGameException.class)
	public void testJoinGame_givenAlreadyRegisteredPlayer_throwException() {
		int initNumber = 40;
		Player playerOne = PlayerFactory.createPlayer("Player1", true);

		// Create a game with two players
		GOTGame game = createGame(playerOne, null, initNumber);

		gameService.joinGame(game.getId(), playerOne.getId());
	}

	@Test(expected = InvalidGameException.class)
	public void testJoinGame_givenFullGame_throwException() {
		int initNumber = 40;
		Player playerOne = PlayerFactory.createPlayer("Player1", true);
		Player playerTwo = PlayerFactory.createPlayer("Player2");

		// Create a game with two players
		GOTGame game = createGame(playerOne, playerTwo, initNumber);

		Player playerThree = PlayerFactory.createPlayer("Player3");
		playerService.createOrUpdate(playerThree);
		gameService.joinGame(game.getId(), playerThree.getId());
	}

	private GOTGame createGame(Player player1, Player player2, int initNumber) {
		GOTGame game = new GOTGame();
		playerService.createOrUpdate(player1);
		game.setPlayer1(player1);

		if (player2 != null) {
			playerService.createOrUpdate(player2);
			game.setPlayer2(player2);
		}
		game.setGameStatus(GameFactory.createGameStatus(initNumber, player1));
		game.setInitialNumber(initNumber);
		return gameService.createOrUpdate(game);
	}
}
