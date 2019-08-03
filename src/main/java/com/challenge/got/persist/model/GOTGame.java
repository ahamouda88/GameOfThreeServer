package com.challenge.got.persist.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity(name = "games")
public class GOTGame implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
	private Integer initialNumber;
	@NotNull
	@OneToOne(cascade = { CascadeType.ALL })
	private GameStatus gameStatus;
	@NotNull
	@OneToOne
	private Player player1;
	@OneToOne
	private Player player2;
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private List<GameMove> gameMoves = new ArrayList<>();

	public synchronized boolean addMove(GameMove move) {
		if (move == null || gameMoves == null) return false;

		return gameMoves.add(move);
	}
}
