package com.challenge.got.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.got.persist.model.Player;

/**
 * An interface that defines the persistence operations to be performed on
 * {@link Player}
 */
@Repository
public interface PlayerDao extends JpaRepository<Player, Long> {
}
