package com.challenge.got.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.challenge.got.persist.dao.PlayerDao;
import com.challenge.got.persist.model.Player;
import com.challenge.got.service.api.PlayerService;

/**
 * A class that implements {@link PlayerService}
 */
@Service
public class PlayerServiceImpl implements PlayerService<Long> {

	@Autowired
	private PlayerDao playerDao;

	@Override
	public JpaRepository<Player, Long> getRepository() {
		return playerDao;
	}
}
