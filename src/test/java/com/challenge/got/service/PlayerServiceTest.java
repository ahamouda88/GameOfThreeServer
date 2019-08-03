package com.challenge.got.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.got.application.MainApplication;
import com.challenge.got.exception.model.NotFoundException;
import com.challenge.got.factory.PlayerFactory;
import com.challenge.got.persist.model.Player;
import com.challenge.got.service.api.PlayerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@DataJpaTest
@ComponentScan(basePackages = { "com.challenge.got.service" })
public class PlayerServiceTest {

	@Autowired
	private PlayerService<Long> playerService;

	@Before
	public void testCreatePlayer_givenValidPlayer_returnSavedPlayer() {
		// Create players
		Player player1 = PlayerFactory.createPlayer("Messi");
		playerService.createOrUpdate(player1);
		assertNotNull(player1.getId());

		Player player2 = PlayerFactory.createPlayer("Xavi");
		playerService.createOrUpdate(player2);
		assertNotNull(player2.getId());

		Player player3 = PlayerFactory.createPlayer("Andres");
		playerService.createOrUpdate(player3);
		assertNotNull(player3.getId());
	}

	@Test
	public void testGetAllPlayers_returnValidList() {
		List<Player> players = playerService.getAll();
		assertEquals(3, players.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePlayer_givenNull_throwException() {
		playerService.createOrUpdate(null);
	}

	@Test
	public void testUpdatePlayer_givenValidPlayer_updatePlayer() {
		String newName = "New Name!";
		Player player = playerService	.getAll()
										.get(1);
		player.setName(newName);
		Player updatedPlayer = playerService.createOrUpdate(player);

		assertEquals(newName, updatedPlayer.getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdatePlayer_givenNull_throwException() {
		playerService.createOrUpdate(null);
	}

	@Test
	public void testDelete_givenValidId_returnValidNumberOfPlayers() {
		long id = playerService	.getAll()
								.get(0)
								.getId();

		// Test if player is removed successfully
		playerService.deleteById(id);

		// Test number of remaining players
		List<Player> players = playerService.getAll();
		assertEquals(2, players.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeletePlayer_givenNull_throwException() {
		// Test removing player with null value
		playerService.deleteById(null);
	}

	@Test(expected = NotFoundException.class)
	public void testDeletePlayer_givenInvalidId_throwException() {
		// Test removing player with invalid Id
		playerService.deleteById(120L);
	}
}
