package com.challenge.got.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		GOTGame gameOne = new GOTGame();
		player1 = PlayerFactory.createPlayer("Player1");
		player2 = PlayerFactory.createPlayer("Player2");
		playerService.createOrUpdate(player1);
		playerService.createOrUpdate(player2);
		gameOne.setPlayer1(player1);
		gameOne.setPlayer2(player2);
		gameOne.setGameStatus(GameFactory.createGameStatus(54, player1));
		gameOne.setInitialNumber(64);
		gameService.createOrUpdate(gameOne);
		assertNotNull(gameOne.getId());

		GOTGame gameTwo = new GOTGame();
		player3 = PlayerFactory.createPlayer("Player1");
		player4 = PlayerFactory.createPlayer("Player2");
		playerService.createOrUpdate(player3);
		playerService.createOrUpdate(player4);
		gameTwo.setPlayer1(player3);
		gameTwo.setPlayer2(player4);
		gameTwo.setGameStatus(GameFactory.createGameStatus(410, player3));
		gameTwo.setInitialNumber(520);
		gameService.createOrUpdate(gameTwo);
		assertNotNull(gameTwo.getId());
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
}
