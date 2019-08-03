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
import org.springframework.web.bind.annotation.RestController;

import com.challenge.got.persist.model.Player;
import com.challenge.got.rest.constants.PathConstants;
import com.challenge.got.rest.response.BaseResponse;
import com.challenge.got.service.api.PlayerService;

/**
 * This class is a Rest Controller for handling the player actions, and handling
 * the player end-points
 */
@RestController
@RequestMapping(value = PathConstants.PLAYERS_FULL_PATH)
public class PlayerController implements ControllerCommonMethods {

	@Autowired
	private PlayerService<Long> playerService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<BaseResponse<Player>> createPlayer(@RequestBody Player player) {
		List<String> errors = new LinkedList<>();
		try {
			Player createdPlayer = playerService.createOrUpdate(player);
			if (createdPlayer.getId() == null) errors.add("Failed to add player!");
		} catch (Exception ex) {
			errors.add(String.format("Exception while adding a player: %s", ex.getMessage()));
		}
		return createBaseResponse(HttpStatus.CREATED, HttpStatus.BAD_REQUEST, errors, player);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<BaseResponse<Player>> updatePlayer(@RequestBody Player player) {
		List<String> errors = new LinkedList<>();
		Player updatedPlayer;
		try {
			updatedPlayer = playerService.createOrUpdate(player);
			if (updatedPlayer == null) errors.add("Failed to update player with Id: " + player.getId());
		} catch (Exception ex) {
			errors.add(String.format("Exception while updating a player: %s", ex.getMessage()));
			updatedPlayer = null;
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, updatedPlayer);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = PathConstants.MAIN_ID_PATH)
	public ResponseEntity<BaseResponse<Player>> deletePlayer(@PathVariable(name = "id") Long playerId) {
		List<String> errors = new LinkedList<>();
		try {
			playerService.deleteById(playerId);
		} catch (Exception ex) {
			errors.add(String.format("Exception while deleting a player: %s", ex.getMessage()));
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, null);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<BaseResponse<List<Player>>> getAllPlayers() {
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, null, playerService.getAll());
	}

	@RequestMapping(method = RequestMethod.GET, value = PathConstants.MAIN_ID_PATH)
	public ResponseEntity<BaseResponse<Player>> getPlayer(@PathVariable(name = "id") Long playerId) {
		List<String> errors = new LinkedList<>();
		Player player;
		try {
			player = playerService.getById(playerId);
			if (player == null) errors.add(String.format("Player with the following Id %s doesn't exist", playerId));
		} catch (Exception ex) {
			errors.add(String.format("Failed to get player with the following Id %s", playerId));
			player = null;
		}
		return createBaseResponse(HttpStatus.OK, HttpStatus.BAD_REQUEST, errors, player);
	}

}
