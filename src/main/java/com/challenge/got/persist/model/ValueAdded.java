package com.challenge.got.persist.model;

import java.util.Arrays;
import java.util.Random;

public enum ValueAdded {

	ONE(1), NEGATIVE_ONE(-1), ZERO(0);

	private static final Random RANDOM = new Random();
	private int value;

	private ValueAdded(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static Integer getRandomValue() {
		return ValueAdded.values()[RANDOM.nextInt(ValueAdded.values().length)].value();
	}

	public static ValueAdded getEnumValue(int value) {
		return Arrays	.asList(ValueAdded.values())
						.stream()
						.filter(check -> check.value() == value)
						.findFirst()
						.orElse(null);
	}
}
