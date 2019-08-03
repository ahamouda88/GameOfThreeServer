package com.challenge.got.service.api;

import java.io.Serializable;

import com.challenge.got.persist.model.Player;

/**
 * An interface that extends {@link MainService}, and operates on {@link Player} models
 *
 * @param <E>
 *            the {@link Player} id's data type
 */
public interface PlayerService<E extends Serializable> extends MainService<Player, E> {

}
