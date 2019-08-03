package com.challenge.got.persist.dao;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.got.application.MainApplication;
import com.challenge.got.factory.GameFactory;
import com.challenge.got.factory.PlayerFactory;
import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.GameStatus;
import com.challenge.got.persist.model.Player;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@DataJpaTest
public class GameDaoTest {

	@Autowired
	private GameDao gameDao;

	@Autowired
	private PlayerDao playerDao;

	private Player player1;
	private Player player2;

	@Before
	public void testCreateGame_givenValidGame_returnSavedGame() {
		// Create first game
		GOTGame gameOne = new GOTGame();
		player1 = PlayerFactory.createPlayer("Player1");
		player2 = PlayerFactory.createPlayer("Player2");
		playerDao.save(player1);
		playerDao.save(player2);
		gameOne.setPlayer1(player1);
		gameOne.setPlayer2(player2);
		gameOne.setGameStatus(GameFactory.createGameStatus(54, player1));
		gameOne.setInitialNumber(64);

		GOTGame actualResult = gameDao.save(gameOne);
		assertNotNull(actualResult);
	}

	@Test
	public void testUpdateGame_givenValidGame_gameUpdatedSuccesfully() {
		Long gameId = gameDao	.findAll()
								.get(0)
								.getId();
		GOTGame game = gameDao	.findById(gameId)
								.orElse(null);
		GameStatus newStatus = GameFactory.createGameStatus(43, player2);
		game.setGameStatus(newStatus);

		// Test if game is updated successfully
		GOTGame updatedGame = gameDao.save(game);
		assertNotNull(updatedGame);
		assertEquals(Integer.valueOf(43), updatedGame	.getGameStatus()
														.getCurrentNumber());
		assertEquals(player2, updatedGame	.getGameStatus()
											.getCurrentPlayer());
	}

	@Test
	public void testDeleteGame_givenValidGame_returnEmtpyList() {
		long id = gameDao	.findAll()
							.get(0)
							.getId();
		GOTGame gameToBeRemoved = gameDao	.findById(id)
											.orElse(null);

		// Test if game is deleted successfully
		gameDao.delete(gameToBeRemoved);

		// Test number of remaining games
		List<GOTGame> games = gameDao.findAll();
		int actualGOTGames = games.size();
		assertEquals(0, actualGOTGames);
	}

	@Test
	public void testFindById_givenInvalidId_returnNull() {
		// Find game with invalid Id
		GOTGame game = gameDao	.findById(100L)
								.orElse(null);
		assertNull(game);
	}

	@Test
	public void testDeleteAll_returnEmptyList() {
		gameDao.deleteAll();

		List<GOTGame> games = gameDao.findAll();
		assertEquals(0, games.size());
	}

	@Test
	public void testFindAll_returnValidList() {
		List<GOTGame> games = gameDao.findAll();
		assertEquals(1, games.size());
	}
}
