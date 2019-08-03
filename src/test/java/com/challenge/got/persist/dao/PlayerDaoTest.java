package com.challenge.got.persist.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.got.application.MainApplication;
import com.challenge.got.persist.model.Player;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@DataJpaTest
public class PlayerDaoTest {

	@Autowired
	private PlayerDao playerDao;

	@Before
	public void testCreatePlayer_givenValidPlayer_returnSavedPlayer() {
		// Create first player
		Player playerOne = new Player();
		playerOne.setName("Ahmed");

		Player actualResult = playerDao.save(playerOne);
		assertNotNull(actualResult);

		// Create second player
		Player playerTwo = new Player();
		playerTwo.setName("Thiago");

		actualResult = playerDao.save(playerTwo);
		assertNotNull(actualResult);
	}

	@Test
	public void testUpdatePlayer_givenValidPlayer_playerUpdatedSuccesfully() {
		String newName = "Thiago Alcantara";
		Long playerId = playerDao	.findAll()
									.get(1)
									.getId();
		Player player = playerDao	.findById(playerId)
									.orElse(null);
		player.setName(newName);

		// Test if player is updated successfully
		Player updatedPlayer = playerDao.save(player);
		assertNotNull(updatedPlayer);
		assertEquals(newName, updatedPlayer.getName());
	}

	@Test
	public void testDeletePlayer_givenValidPlayer_returnEmtpyList() {
		// Get last added player
		long id = playerDao	.findAll()
							.get(0)
							.getId();
		Player playerToBeRemoved = playerDao.findById(id)
											.orElse(null);

		String expectedPlayerName = "Ahmed";
		assertEquals(expectedPlayerName, playerToBeRemoved.getName());

		// Test if player is deleted successfully
		playerDao.delete(playerToBeRemoved);

		// Test number of remaining players
		List<Player> players = playerDao.findAll();
		int actualPlayers = players.size();
		assertEquals(1, actualPlayers);
	}

	@Test
	public void testFindById_givenInvalidId_returnNull() {
		// Find player with invalid Id
		Player player = playerDao	.findById(100L)
									.orElse(null);
		assertNull(player);
	}

	@Test
	public void testFindAll_returnValidList() {
		List<Player> players = playerDao.findAll();

		assertEquals(2, players.size());
	}

	@Test
	public void testDeleteAll_returnEmptyList() {
		playerDao.deleteAll();

		List<Player> players = playerDao.findAll();
		assertEquals(0, players.size());

	}
}
