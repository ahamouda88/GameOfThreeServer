package com.challenge.got.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import com.challenge.got.persist.model.GOTGame;
import com.challenge.got.persist.model.Player;

/**
 * An interface that defines the persistence operations to be performed on
 * {@link Player}
 */
@Repository
@Validated
public interface GameDao extends JpaRepository<GOTGame, Long> {
}
