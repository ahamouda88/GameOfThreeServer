package com.challenge.got.rest.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.rest.constants.PathConstants;
import com.challenge.got.rest.response.BaseResponse;
import com.challenge.got.service.api.GameService;

/**
 * This class is a Rest Controller for handling the game actions, and handling
 * the game end-points
 */
@RestController
@RequestMapping(value = PathConstants.GAMES_FULL_PATH)
public class GameController implements ControllerCommonMethods {

	@Autowired
	private GameService<Long, Long> gameService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<BaseResponse<GOTGame>> createGame(@RequestBody GOTGame game) {
		List<String> errors = new LinkedList<>();
		try {
			GOTGame createdGame = gameService.createOrUpdate(game);
			if (createdGame.getId() == null) errors.add("Failed to add game!");
		} catch (Exception ex) {
			errors.add(String.format("Exception while adding a game: %s", ex.getMessage()));
		}
		return createBaseResponse(HttpStatus.CREATED, HttpStatus.BAD_REQUEST, errors, game);
	}

	@RequestMapping(method = RequestMethod.GET, value = PathConstants.GAMES_PLAYERS_PATH)
	public ResponseEntity<BaseResponse<GOTGame>> addMove(@PathVariable(name = "gameId") Long gameId,
			@PathVariable(name = "playerId") Long playerId, @RequestParam(name = "add") Integer move) {
		List<String> errors = new LinkedList<>();
		GOTGame game;
		try {
			game = gameService.addGameMove(gameId, playerId, move);
			if (game.getId() == null) {
				errors.add(String.format("Failed to add move from player: %s to the game: %s", playerId, gameId));
			}
		} catch (Exception ex) {
			errors.add(String.format("Exception while adding a game move: %s", ex.getMessage()));
			game = null;
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, game);
	}

	@RequestMapping(method = RequestMethod.GET, value = PathConstants.GAMES_PLAYER_JOIN_PATH)
	public ResponseEntity<BaseResponse<GOTGame>> joinGame(@PathVariable(name = "gameId") Long gameId,
			@RequestParam(name = "playerid") Long playerId) {
		List<String> errors = new LinkedList<>();
		GOTGame game;
		try {
			game = gameService.joinGame(gameId, playerId);
			if (game.getId() == null) {
				errors.add(String.format("Failed to add player with id: %s to the game with id: %s", playerId, gameId));
			}
		} catch (Exception ex) {
			errors.add(String.format("Exception while adding a player to a game: %s", ex.getMessage()));
			game = null;
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, game);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<BaseResponse<GOTGame>> updateGame(@RequestBody GOTGame game) {
		List<String> errors = new LinkedList<>();
		GOTGame updatedGame;
		try {
			updatedGame = gameService.createOrUpdate(game);
			if (updatedGame == null) errors.add("Failed to update game with Id: " + game.getId());
		} catch (Exception ex) {
			errors.add(String.format("Exception while updating a game: %s", ex.getMessage()));
			updatedGame = null;
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, updatedGame);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = PathConstants.MAIN_ID_PATH)
	public ResponseEntity<BaseResponse<GOTGame>> deleteGame(@PathVariable(name = "id") Long gameId) {
		List<String> errors = new LinkedList<>();
		try {
			gameService.deleteById(gameId);
		} catch (Exception ex) {
			errors.add(String.format("Exception while deleting a game: %s", ex.getMessage()));
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, null);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<BaseResponse<List<GOTGame>>> getAllGames() {
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, null, gameService.getAll());
	}

	@RequestMapping(method = RequestMethod.GET, value = PathConstants.MAIN_ID_PATH)
	public ResponseEntity<BaseResponse<GOTGame>> getGame(@PathVariable(name = "id") Long gameId) {
		List<String> errors = new LinkedList<>();
		GOTGame game;
		try {
			game = gameService.getById(gameId);
			if (game == null) errors.add(String.format("Game with the following Id %s doesn't exist", gameId));
		} catch (Exception ex) {
			errors.add(String.format("Failed to get Game with the following id %s", gameId));
			game = null;
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, game);
	}

}
