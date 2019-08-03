package com.challenge.got.rest.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.challenge.got.application.MainApplication;
import com.challenge.got.factory.GameFactory;
import com.challenge.got.factory.PlayerFactory;
import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.GameStatus;
import com.challenge.got.persist.model.Player;
import com.challenge.got.rest.constants.PathConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class GameControllerTest {

	private static final String PLAYERS_END_POINT = PathConstants.PLAYERS_FULL_PATH;
	private static final String GAMES_END_POINT = PathConstants.GAMES_FULL_PATH;

	private ObjectMapper mapper = new ObjectMapper();

	private MockMvc mockMvc;

	@Autowired
	private GameController gameController;

	@Autowired
	private PlayerController playerController;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders	.standaloneSetup(gameController, playerController)
									.build();
	}

	@Test
	public void testCreateRemoveAndGetGame_givenValidData_returnValidResponses() throws Exception {
		Pair<Integer, Pair<Integer, Integer>> gameDetails1 = testCreateGameAndReturnDetails("Ahmed", "Messi", 120, 430);
		Pair<Integer, Pair<Integer, Integer>> gameDetails2 = testCreateGameAndReturnDetails("Omar", "Ibrahim", 20, 50);

		Integer player1Id = gameDetails1.getSecond()
										.getFirst();
		Integer player2Id = gameDetails1.getSecond()
										.getSecond();

		/** Test get method with valid Id **/
		String gameIdUrl = String.format("%s/%d", GAMES_END_POINT, gameDetails1.getFirst());
		mockMvc	.perform(get(gameIdUrl))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")))
				.andExpect(jsonPath("$.data.initialNumber", is(430)))
				.andExpect(jsonPath("$.data.gameStatus.currentPlayer.id", is(player1Id)))
				.andExpect(jsonPath("$.data.gameStatus.currentPlayer.name", is("Ahmed")));

		/** Test get method with invalid Id **/
		mockMvc	.perform(get(GAMES_END_POINT + "/100"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(jsonPath("$.status.errors[0]", is("Game with the following Id 100 doesn't exist")));

		/** Test delete method with valid Id **/
		String gameDeleteUrl = String.format("%s/%d", GAMES_END_POINT, gameDetails2.getFirst());
		mockMvc	.perform(delete(gameDeleteUrl))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")));

		/** Test delete method with invalid Id **/
		mockMvc	.perform(delete(GAMES_END_POINT + "/10"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(jsonPath("$.status.errors[0]",
						is("Exception while deleting a game: Model with the following id 10 doesn't exist")));

		/** Test a valid add move **/
		mockMvc	.perform(
				get(createGameMoveUrl(gameDetails1.getFirst(), player1Id, 0)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")))
				.andExpect(jsonPath("$.data.initialNumber", is(430)))
				.andExpect(jsonPath("$.data.gameStatus.currentPlayer.id", is(player2Id)))
				.andExpect(jsonPath("$.data.gameStatus.currentNumber", is(40)));

		/** Test invalid player turn **/
		mockMvc	.perform(
				get(createGameMoveUrl(gameDetails1.getFirst(), player1Id, 1)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(jsonPath("$.status.errors[0]",
						is("Exception while adding a game move: Invalid move, current player is incorrect")));

		/** Test a invalid add move **/
		mockMvc	.perform(
				get(createGameMoveUrl(gameDetails1.getFirst(), player1Id, 2)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(jsonPath("$.status.errors[0]", is(
						"Exception while adding a game move: Invalid move value, value should be one of the following: -1, 0, 1")));
	}

	@Test
	public void testAddingMove_givenGameIsOver_throwException() throws Exception {
		Pair<Integer, Pair<Integer, Integer>> gameDetails = testCreateGameAndReturnDetails("Spring", "Hibernate", 3, 0);
		int player1Id = gameDetails	.getSecond()
									.getFirst();
		int player2Id = gameDetails	.getSecond()
									.getSecond();
		/** Test a winner player move **/
		mockMvc	.perform(
				get(createGameMoveUrl(gameDetails.getFirst(), player1Id, 0)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		/** Test doing a move on an invalid game **/
		mockMvc	.perform(
				get(createGameMoveUrl(gameDetails.getFirst(), player2Id, 1)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status.message", is("Failed")))
				.andExpect(
						jsonPath("$.status.errors[0]", is("Exception while adding a game move: Game is already over")));
	}

	@Test
	public void testJoiningGame_givenValidPlayer_returnUpdatedGame() throws Exception {
		Pair<Integer, Pair<Integer, Integer>> gameDetails = testCreateGameAndReturnDetails("Jenna", null, 54, 12);
		Player newPlayer = createPlayer("Mikel");

		/** Test adding a new player to the game **/
		mockMvc	.perform(get(createJoinGameUrl(gameDetails.getFirst(), newPlayer.getId()
																				.intValue())).contentType(
																						MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")))
				.andExpect(jsonPath("$.data.player2.id", is(newPlayer	.getId()
																		.intValue())))
				.andExpect(jsonPath("$.data.player2.name", is(newPlayer.getName())));
	}

	@After
	public void testGetAll() throws Exception {
		mockMvc	.perform(get(GAMES_END_POINT))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status.message", is("Success")));
	}

	/*
	 * Helper methods
	 */
	private Pair<Integer, Pair<Integer, Integer>> testCreateGameAndReturnDetails(String player1Name, String player2Name,
			int currentNumber, int initNumber) throws Exception {
		Player player1 = createPlayer(player1Name);
		Player player2 = createPlayer(player2Name);

		GameStatus status = GameFactory.createGameStatus(currentNumber, player1);
		GOTGame game = GameFactory.createGame(status, initNumber, player1, player2, new ArrayList<>());
		MvcResult gameResult = mockMvc	.perform(post(GAMES_END_POINT, game).contentType(MediaType.APPLICATION_JSON)
																			.content(mapper.writeValueAsString(game)))
										.andExpect(status().isCreated())
										.andExpect(jsonPath("$.status.message", is("Success")))
										.andExpect(jsonPath("$.data.player1.name", is(player1Name)))
										.andExpect(player2 != null ? jsonPath("$.data.player2.name", is(player2Name))
												: jsonPath("$.data.player2", nullValue()))
										.andExpect(jsonPath("$.data.id", notNullValue()))
										.andReturn();
		int gameId = getIdFromData(gameResult);
		int player1Id = player1	.getId()
								.intValue();
		int player2Id = player2 == null ? -1
				: player2	.getId()
							.intValue();
		Pair<Integer, Integer> players = Pair.of(player1Id, player2Id);
		Pair<Integer, Pair<Integer, Integer>> gamePlayersPair = Pair.of(gameId, players);
		return gamePlayersPair;
	}

	private Player createPlayer(String playerName) throws Exception {
		if (playerName == null) return null;

		Player player = PlayerFactory.createPlayer(playerName);
		MvcResult player2Result = mockMvc	.perform(
				post(PLAYERS_END_POINT, player)	.contentType(MediaType.APPLICATION_JSON)
												.content(mapper.writeValueAsString(player)))
											.andExpect(status().isCreated())
											.andExpect(jsonPath("$.status.message", is("Success")))
											.andReturn();
		int playerId = getIdFromData(player2Result);
		player.setId(Integer.toUnsignedLong(playerId));
		return player;
	}

	private String createGameMoveUrl(int gameId, int playerId, int move) {
		List<String> sections = new ArrayList<>();
		sections.add(GAMES_END_POINT);
		sections.add(String.valueOf(gameId));
		sections.add(PathConstants.PLAYERS_PATH);
		sections.add(playerId + "?add=" + move);

		return StringUtils.join(sections, "/");
	}

	private String createJoinGameUrl(int gameId, int playerId) {
		List<String> sections = new ArrayList<>();
		sections.add(GAMES_END_POINT);
		sections.add(String.valueOf(gameId));
		sections.add("join?playerid=" + playerId);

		return StringUtils.join(sections, "/");
	}

	private int getIdFromData(MvcResult result) throws IOException {
		return JsonPath.read(result	.getResponse()
									.getContentAsString(),
				"$.data.id");
	}
}
